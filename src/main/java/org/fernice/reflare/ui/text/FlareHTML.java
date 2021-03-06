/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.fernice.reflare.ui.text;

import fernice.reflare.font.FontUtil;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.CSS.Attribute;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import org.fernice.reflare.internal.SunFontHelper;
import org.jetbrains.annotations.NotNull;

public class FlareHTML {

    /**
     * Create an html renderer for the given component and
     * string of html.
     */
    public static View createHTMLView(JComponent c, String html) {
        BasicEditorKit kit = getFactory();
        Document doc = kit.createDefaultDocument(c.getFont(), c.getForeground());
        Object base = c.getClientProperty(documentBaseKey);
        if (base instanceof URL) {
            ((HTMLDocument) doc).setBase((URL) base);
        }
        Reader r = new StringReader(html);
        try {
            kit.read(r, doc, 0);
        } catch (Throwable ignore) {
        }
        ViewFactory f = kit.getViewFactory();
        View hview = f.create(doc.getDefaultRootElement());
        return new Renderer(c, f, hview);
    }

    public static void disableFeedbackBehaviour(@NotNull JComponent component) {
        View view = (View) component.getClientProperty(FlareHTML.propertyKey);
        if (view instanceof Renderer) {
            ((Renderer) view).feedbackEnabled = false;
        }
    }

    public static void enableFeedbackBehaviour(@NotNull JComponent component) {
        View view = (View) component.getClientProperty(FlareHTML.propertyKey);
        if (view instanceof Renderer) {
            ((Renderer) view).feedbackEnabled = true;
        }
    }

    public static boolean isFeedbackBehaviourEnabled(@NotNull JComponent component) {
        View view = (View) component.getClientProperty(FlareHTML.propertyKey);
        if (view instanceof Renderer) {
            return ((Renderer) view).feedbackEnabled;
        }
        return true;
    }

    /**
     * Returns the baseline for the html renderer.
     *
     * @param view the View to get the baseline for
     * @param w the width to get the baseline for
     * @param h the height to get the baseline for
     * @throws IllegalArgumentException if width or height is &lt; 0
     * @return baseline or a value &lt; 0 indicating there is no reasonable
     *                  baseline
     * @see FontMetrics
     * @see JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public static int getHTMLBaseline(View view, int w, int h) {
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        if (view instanceof Renderer) {
            Renderer renderer = (Renderer) view;
            return renderer.getBaseline(w, h);
        }
        return -1;
    }

    /**
     * Gets the baseline for the specified component.  This digs out
     * the View client property, and if non-null the baseline is calculated
     * from it.  Otherwise the baseline is the value <code>y + ascent</code>.
     */
    public static int getBaseline(JComponent c, int y, int ascent, int w, int h) {
        View view = (View) c.getClientProperty(FlareHTML.propertyKey);
        if (view != null) {
            int baseline = getHTMLBaseline(view, w, h);
            if (baseline < 0) {
                return baseline;
            }
            return y + baseline;
        }
        return y + ascent;
    }

    /**
     * Check the given string to see if it should trigger the
     * html rendering logic in a non-text component that supports
     * html rendering.
     */
    public static boolean isHTMLString(String s) {
        if (s != null) {
            if ((s.length() >= 6) && (s.charAt(0) == '<') && (s.charAt(5) == '>')) {
                String tag = s.substring(1, 5);
                return tag.equalsIgnoreCase(propertyKey);
            }
        }
        return false;
    }

    /**
     * Stash the HTML render for the given text into the client
     * properties of the given JComponent. If the given text is
     * <em>NOT HTML</em> the property will be cleared of any
     * renderer.
     * <p>
     * This method is useful for ComponentUI implementations
     * that are static (i.e. shared) and get their state
     * entirely from the JComponent.
     */
    public static void updateRenderer(JComponent c, String text) {
        View value = null;
        View oldValue = (View) c.getClientProperty(FlareHTML.propertyKey);
        Boolean htmlDisabled = (Boolean) c.getClientProperty(htmlDisable);
        if (htmlDisabled != Boolean.TRUE && FlareHTML.isHTMLString(text)) {
            value = FlareHTML.createHTMLView(c, text);
        }
        if (value != oldValue && oldValue != null) {
            for (int i = 0; i < oldValue.getViewCount(); i++) {
                oldValue.getView(i).setParent(null);
            }
        }
        c.putClientProperty(FlareHTML.propertyKey, value);
    }

    /**
     * If this client property of a JComponent is set to Boolean.TRUE
     * the component's 'text' property is never treated as HTML.
     */
    private static final String htmlDisable = "html.disable";

    /**
     * Key to use for the html renderer when stored as a
     * client property of a JComponent.
     */
    public static final String propertyKey = "html";

    /**
     * Key stored as a client property to indicate the base that relative
     * references are resolved against. For example, lets say you keep
     * your images in the directory resources relative to the code path,
     * you would use the following the set the base:
     * <pre>
     *   jComponent.putClientProperty(documentBaseKey,
     *                                xxx.class.getResource("resources/"));
     * </pre>
     */
    public static final String documentBaseKey = "html.base";

    static BasicEditorKit getFactory() {
        if (basicHTMLFactory == null) {
            basicHTMLViewFactory = new BasicHTMLViewFactory();
            basicHTMLFactory = new BasicEditorKit();
        }
        return basicHTMLFactory;
    }

    /**
     * The source of the html renderers
     */
    private static BasicEditorKit basicHTMLFactory;

    /**
     * Creates the Views that visually represent the model.
     */
    private static ViewFactory basicHTMLViewFactory;

    /**
     * Overrides to the default stylesheet.  Should consider
     * just creating a completely fresh stylesheet.
     */
    private static final String styleChanges = "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }" +
            "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";

    /**
     * The views produced for the ComponentUI implementations aren't
     * going to be edited and don't need full html support.  This kit
     * alters the HTMLEditorKit to try and trim things down a bit.
     * It does the following:
     * <ul>
     * <li>It doesn't produce Views for things like comments,
     * head, title, unknown tags, etc.
     * <li>It installs a different set of css settings from the default
     * provided by HTMLEditorKit.
     * </ul>
     */
    static class BasicEditorKit extends HTMLEditorKit {

        /** Shared base style for all documents created by us use. */
        private static StyleSheet defaultStyles;

        /**
         * Overriden to return our own slimmed down style sheet.
         */
        public StyleSheet getStyleSheet() {
            if (defaultStyles == null) {
                defaultStyles = new StyleSheet();
                StringReader r = new StringReader(styleChanges);
                try {
                    defaultStyles.loadRules(r, null);
                } catch (Throwable e) {
                    // don't want to die in static initialization...
                    // just display things wrong.
                }
                r.close();
                defaultStyles.addStyleSheet(super.getStyleSheet());
            }
            return defaultStyles;
        }

        /**
         * Sets the async policy to flush everything in one chunk, and
         * to not display unknown tags.
         */
        public Document createDefaultDocument(Font defaultFont, Color foreground) {
            StyleSheet styles = getStyleSheet();
            StyleSheet ss = new StyleSheet();
            ss.addStyleSheet(styles);
            BasicDocument doc = new BasicDocument(ss, defaultFont, foreground);
            doc.setAsynchronousLoadPriority(Integer.MAX_VALUE);
            doc.setPreservesUnknownTags(false);
            return doc;
        }

        /**
         * Returns the ViewFactory that is used to make sure the Views don't
         * load in the background.
         */
        public ViewFactory getViewFactory() {
            return basicHTMLViewFactory;
        }
    }


    /**
     * BasicHTMLViewFactory extends HTMLFactory to force images to be loaded
     * synchronously.
     */
    static class BasicHTMLViewFactory extends HTMLEditorKit.HTMLFactory {

        public View create(Element elem) {
            View view = super.create(elem);

            if (view instanceof ImageView) {
                ((ImageView) view).setLoadsSynchronously(true);
            }
            return view;
        }
    }


    /**
     * The subclass of HTMLDocument that is used as the model. getForeground
     * is overridden to return the foreground property from the Component this
     * was created for.
     */
    static class BasicDocument extends HTMLDocument {

        /** The host, that is where we are rendering. */
        // private JComponent host;

        BasicDocument(StyleSheet s, Font defaultFont, Color foreground) {
            super(s);
            setPreservesUnknownTags(false);
            setFontAndColor(defaultFont, foreground);
        }

        /**
         * Sets the default font and default color. These are set by
         * adding a rule for the body that specifies the font and color.
         * This allows the html to override these should it wish to have
         * a custom font or color.
         */
        private void setFontAndColor(Font font, Color fg) {
            getStyleSheet().addRule(displayPropertiesToCSS(font, fg));
        }

        private static String displayPropertiesToCSS(Font font, Color fg) {
            StringBuilder rule = new StringBuilder("body {");
            if (font != null) {
                rule.append(" font-family: ");
                rule.append(font.getFamily());
                rule.append(" ; ");
                rule.append(" font-size: ");
                rule.append(font.getSize());
                rule.append("pt ;");
                int weight = FontUtil.getWeight(font);
                if (weight != 400) {
                    rule.append(" font-weight: ").append(weight).append(" ; ");
                }
                if (FontUtil.getItalic(font)) {
                    rule.append(" font-style: italic ; ");
                }
            }
            if (fg != null) {
                rule.append(" color: #");
                if (fg.getRed() < 16) {
                    rule.append('0');
                }
                rule.append(Integer.toHexString(fg.getRed()));
                if (fg.getGreen() < 16) {
                    rule.append('0');
                }
                rule.append(Integer.toHexString(fg.getGreen()));
                if (fg.getBlue() < 16) {
                    rule.append('0');
                }
                rule.append(Integer.toHexString(fg.getBlue()));
                rule.append(" ; ");
            }
            rule.append(" }");
            return rule.toString();
        }

        @Override
        public Font getFont(AttributeSet attr) {
            String fontFamily = StyleConstants.getFontFamily(attr);
            int fontWeight = getFontWeight(attr);
            boolean fontItalic = StyleConstants.isItalic(attr);

            Font font = SunFontHelper.findFont(fontFamily, fontWeight, fontItalic);
            if (font != null) {
                int fontSize = StyleConstants.getFontSize(attr);

                return font.deriveFont((float) fontSize);
            }

            return super.getFont(attr);
        }

        private int getFontWeight(@NotNull AttributeSet attr) {
            Object fontWeight = attr.getAttribute(Attribute.FONT_WEIGHT);
            if (fontWeight != null) {
                String value = fontWeight.toString();
                if (value.equals("bold")) {
                    return 700;
                } else if (value.equals("normal")) {
                    return 400;
                } else {
                    // PENDING(prinz) add support for relative values
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException nfe) {
                        return 400;
                    }
                }
            }
            return 400;
        }
    }


    /**
     * Root text view that acts as an HTML renderer.
     */
    static class Renderer extends View {

        private final @NotNull JComponent host;

        private final @NotNull View view;
        private final @NotNull ViewFactory factory;

        private int width;

        private int externalWidth;
        private int externalHeight;

        boolean feedbackEnabled = true;

        Renderer(@NotNull JComponent c, @NotNull ViewFactory f, @NotNull View v) {
            super(null);
            host = c;
            factory = f;
            view = v;
            view.setParent(this);
            // initially layout to the preferred size
            setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }

        /**
         * Fetches the attributes to use when rendering.  At the root
         * level there are no attributes.  If an attribute is resolved
         * up the view hierarchy this is the end of the line.
         */
        public AttributeSet getAttributes() {
            return null;
        }

        /**
         * Determines the preferred span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getPreferredSpan(int axis) {
            if (axis == X_AXIS) {
                // width currently laid out to
                return width;
            }
            return view.getPreferredSpan(axis);
        }

        /**
         * Determines the minimum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMinimumSpan(int axis) {
            return view.getMinimumSpan(axis);
        }

        /**
         * Determines the maximum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMaximumSpan(int axis) {
            return Integer.MAX_VALUE;
        }

        /**
         * Specifies that a preference has changed.
         * Child views can call this on the parent to indicate that
         * the preference has changed.  The root view routes this to
         * invalidate on the hosting component.
         * <p>
         * This can be called on a different thread from the
         * event dispatching thread and is basically unsafe to
         * propagate into the component.  To make this safe,
         * the operation is transferred over to the event dispatching
         * thread for completion.  It is a design goal that all view
         * methods be safe to call without concern for concurrency,
         * and this behavior helps make that true.
         *
         * @param child the child view
         * @param width true if the width preference has changed
         * @param height true if the height preference has changed
         */
        public void preferenceChanged(View child, boolean width, boolean height) {
            if (feedbackEnabled) {
                host.revalidate();
                host.repaint();
            }
        }

        /**
         * Determines the desired alignment for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the desired alignment, where 0.0 indicates the origin
         *     and 1.0 the full span away from the origin
         */
        public float getAlignment(int axis) {
            return view.getAlignment(axis);
        }

        /**
         * Renders the view.
         *
         * @param g the graphics context
         * @param allocation the region to render into
         */
        public void paint(Graphics g, Shape allocation) {
            Rectangle alloc = allocation.getBounds();
            setExternalSize(alloc.width, alloc.height);
            view.setSize(alloc.width, alloc.height);
            view.paint(g, allocation);
        }

        /**
         * Sets the view parent.
         *
         * @param parent the parent view
         */
        public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        /**
         * Returns the number of views in this view.  Since
         * this view simply wraps the root of the view hierarchy
         * it has exactly one child.
         *
         * @return the number of views
         * @see #getView
         */
        public int getViewCount() {
            return 1;
        }

        /**
         * Gets the n-th view in this container.
         *
         * @param n the number of the view to get
         * @return the view
         */
        public View getView(int n) {
            return view;
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            return view.modelToView(pos, a, b);
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param p0 the position to convert >= 0
         * @param b0 the bias toward the previous character or the
         *  next character represented by p0, in case the
         *  position is a boundary of two views.
         * @param p1 the position to convert >= 0
         * @param b1 the bias toward the previous character or the
         *  next character represented by p1, in case the
         *  position is a boundary of two views.
         * @param a the allocated region to render into
         * @return the bounding box of the given position is returned
         * @exception BadLocationException  if the given position does
         *   not represent a valid location in the associated document
         * @exception IllegalArgumentException for an invalid bias argument
         * @see View#viewToModel
         */
        public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1, Shape a) throws BadLocationException {
            return view.modelToView(p0, b0, p1, b1, a);
        }

        /**
         * Provides a mapping from the view coordinate space to the logical
         * coordinate space of the model.
         *
         * @param x x coordinate of the view location to convert
         * @param y y coordinate of the view location to convert
         * @param a the allocated region to render into
         * @return the location within the model that best represents the
         *    given point in the view
         */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
            return view.viewToModel(x, y, a, bias);
        }

        /**
         * Returns the document model underlying the view.
         *
         * @return the model
         */
        public Document getDocument() {
            return view.getDocument();
        }

        /**
         * Returns the starting offset into the model for this view.
         *
         * @return the starting offset
         */
        public int getStartOffset() {
            return view.getStartOffset();
        }

        /**
         * Returns the ending offset into the model for this view.
         *
         * @return the ending offset
         */
        public int getEndOffset() {
            return view.getEndOffset();
        }

        /**
         * Gets the element that this view is mapped to.
         *
         * @return the view
         */
        public Element getElement() {
            return view.getElement();
        }

        /**
         * Sets the view size.
         *
         * @param width the width
         * @param height the height
         */
        public void setSize(float width, float height) {
            this.width = (int) width;
            setExternalSize(width, height);
            view.setSize(width, height);
        }

        private void setExternalSize(float width, float height) {
            this.externalWidth = (int) width;
            this.externalHeight = (int) height;
        }

        /**
         * Fetches the container hosting the view.  This is useful for
         * things like scheduling a repaint, finding out the host
         * components font, etc.  The default implementation
         * of this is to forward the query to the parent view.
         *
         * @return the container
         */
        public Container getContainer() {
            return host;
        }

        /**
         * Fetches the factory to be used for building the
         * various view fragments that make up the view that
         * represents the model.  This is what determines
         * how the model will be represented.  This is implemented
         * to fetch the factory provided by the associated
         * EditorKit.
         *
         * @return the factory
         */
        public ViewFactory getViewFactory() {
            return factory;
        }

        /**
         * Gets the baseline for the specified View.
         */
        int getBaseline(int w, int h) {
            if (hasParagraph(view)) {
                feedbackEnabled = false;
                view.setSize(w, h);
                try {
                    return getBaseline(view, new Rectangle(0, 0, w, h));
                } finally {
                    view.setSize(externalWidth, externalHeight);
                    feedbackEnabled = true;
                }
            }
            return -1;
        }

        private static int getBaseline(View view, Shape bounds) {
            if (view.getViewCount() == 0) {
                return -1;
            }
            AttributeSet attributes = view.getElement().getAttributes();
            Object name = null;
            if (attributes != null) {
                name = attributes.getAttribute(StyleConstants.NameAttribute);
            }
            int index = 0;
            if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
                // For html on widgets the header is not visible, skip it.
                index++;
            }
            bounds = view.getChildAllocation(index, bounds);
            if (bounds == null) {
                return -1;
            }
            View child = view.getView(index);
            if (view instanceof javax.swing.text.ParagraphView) {
                Rectangle rect;
                if (bounds instanceof Rectangle) {
                    rect = (Rectangle) bounds;
                } else {
                    rect = bounds.getBounds();
                }
                return rect.y + (int) (rect.height * child.getAlignment(View.Y_AXIS));
            }
            return getBaseline(child, bounds);
        }

        private static boolean hasParagraph(View view) {
            if (view instanceof javax.swing.text.ParagraphView) {
                return true;
            }
            if (view.getViewCount() == 0) {
                return false;
            }
            AttributeSet attributes = view.getElement().getAttributes();
            Object name = null;
            if (attributes != null) {
                name = attributes.getAttribute(StyleConstants.NameAttribute);
            }
            int index = 0;
            if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
                // For html on widgets the header is not visible, skip it.
                index = 1;
            }
            return hasParagraph(view.getView(index));
        }
    }
}
