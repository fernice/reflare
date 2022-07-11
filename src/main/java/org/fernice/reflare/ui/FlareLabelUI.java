package org.fernice.reflare.ui;

import fernice.reflare.StyledIcon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.LabelElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.ui.text.FlareHTML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FlareLabelUI extends BasicLabelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareLabelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JLabel label) {
        if (element == null) {
            element = new LabelElement(label);
        }

        UIDefaultsHelper.installDefaultProperties(this, label);

        label.setIconTextGap(5);

        StyleTreeElementLookup.registerElement(label, this);
    }

    @Override
    protected void uninstallDefaults(JLabel label) {
        StyleTreeElementLookup.deregisterElement(label);
    }

    @Override
    protected void installComponents(JLabel c) {
        //        super.installComponents(c);
        FlareHTML.updateRenderer(c, c.getText());
        c.setInheritsPopupMenu(true);
    }


    @Override
    public void propertyChange(@NotNull PropertyChangeEvent e) {
        String name = e.getPropertyName();
        if (name == "text" || "font" == name || "foreground" == name) {
            // remove the old html view client property if one
            // existed, and install a new one if the text installed
            // into the JLabel is html source.
            JLabel lbl = (JLabel) e.getSource();
            String text = lbl.getText();
            FlareHTML.updateRenderer(lbl, text);
        } else {
            super.propertyChange(e);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        element.pulseForComputation();

        JLabel label = (JLabel) c;
        String text = label.getText();
        Icon icon = getIcon(label);
        Insets insets = label.getInsets(null);
        Font font = label.getFont();

        int dx = insets.left + insets.right;
        int dy = insets.top + insets.bottom;

        if ((icon == null) && ((text == null) || (font == null))) {
            return new Dimension(dx, dy);
        } else if ((text == null) || ((icon != null) && (font == null))) {
            return new Dimension(icon.getIconWidth() + dx, icon.getIconHeight() + dy);
        } else {
            FontMetrics fm = label.getFontMetrics(font);
            Rectangle iconR = new Rectangle();
            Rectangle textR = new Rectangle();
            Rectangle viewR = new Rectangle();

            iconR.x = iconR.y = iconR.width = iconR.height = 0;
            textR.x = textR.y = textR.width = textR.height = 0;
            viewR.x = dx;
            viewR.y = dy;
            viewR.width = viewR.height = Short.MAX_VALUE;

            layoutCL(label, fm, text, icon, viewR, iconR, textR);
            int x1 = Math.min(iconR.x, textR.x);
            int x2 = Math.max(iconR.x + iconR.width, textR.x + textR.width);
            int y1 = Math.min(iconR.y, textR.y);
            int y2 = Math.max(iconR.y + iconR.height, textR.y + textR.height);
            Dimension rv = new Dimension(x2 - x1, y2 - y1);

            rv.width += dx;
            rv.height += dy;
            return rv;
        }
    }

    @Override
    public int getBaseline(JComponent c, int width, int height) {
        super.getBaseline(c, width, height);
        JLabel label = (JLabel) c;
        String text = label.getText();
        if (text == null || "".equals(text) || label.getFont() == null) {
            return -1;
        }
        Icon icon = getIcon(label);
        FontMetrics fm = label.getFontMetrics(label.getFont());
        layout(label, text, icon, fm, width, height);
        return FlareHTML.getBaseline(label, paintTextR.y, fm.getAscent(), paintTextR.width, paintTextR.height);
    }

    @Override
    public void paint(final Graphics g, JComponent component) {
        paintBackground(component, g);

        JLabel label = (JLabel) component;
        String text = label.getText();
        Icon icon = getIcon(label);

        if ((icon == null) && (text == null)) {
            return;
        }

        FontMetrics fm = SwingUtilitiesHelper.getFontMetrics(label, g);
        String clippedText = layout(label, text, icon, fm, label.getWidth(), label.getHeight());

        if (icon != null) {
            icon.paintIcon(label, g, paintIconR.x, paintIconR.y);
        }

        if (text != null) {
            View v = (View) label.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, paintTextR);
            } else {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                } else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }
        }
    }

    protected @Nullable Icon getIcon(@NotNull JLabel label) {
        Icon icon = label.getIcon();

        if (!label.isEnabled()) {
            Icon disabledIcon = label.getDisabledIcon();
            if (disabledIcon != null && (!(disabledIcon instanceof UIResource) || !(icon instanceof StyledIcon))) {
                icon = disabledIcon;
            }
        }

        return icon;
    }

    private final Rectangle paintIconR = new Rectangle();
    private final Rectangle paintTextR = new Rectangle();

    protected String layout(JLabel label, @Nullable String text, @Nullable Icon icon, FontMetrics fm, int width, int height) {
        Insets insets = label.getInsets(null);
        Rectangle paintViewR = new Rectangle();
        paintViewR.x = insets.left;
        paintViewR.y = insets.top;
        paintViewR.width = width - (insets.left + insets.right);
        paintViewR.height = height - (insets.top + insets.bottom);
        paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
        paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
        return layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        paintEnabledText(l, g, s, textX, textY);
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(g);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g) {
        element.paintBorder(g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
