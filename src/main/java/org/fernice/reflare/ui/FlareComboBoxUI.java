package org.fernice.reflare.ui;

import fernice.reflare.StyleHelper;
import fernice.reflare.StyledImageIcon;
import fernice.reflare.light.FButton;
import fernice.reflare.light.FLabel;
import fernice.reflare.light.FTextField;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.ComboBoxElement;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.element.StyleTreeHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.render.CellRendererPane;
import org.jetbrains.annotations.NotNull;

public class FlareComboBoxUI extends BasicComboBoxUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareComboBoxUI();
    }

    private ComponentElement element;

    public FlareComboBoxUI() {
        currentValuePane = new CellRendererPane();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();

        element = new ComboBoxElement(comboBox);

        squareButton = false;
        padding = Defaults.INSETS_EMPTY;

        UIDefaultsHelper.installDefaultProperties(this, comboBox);

        comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        StyleTreeElementLookup.registerElement(comboBox, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(comboBox);

        super.uninstallDefaults();
    }

    @Override
    protected void installComponents() {
        super.installComponents();

        arrowButton.setFocusable(false);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new FlareComboBoxLayout();
    }

    @Override
    protected FButton createArrowButton() {
        FButton button = new FButton();
        button.setIcon(StyledImageIcon.fromResource("/reflare/icons/combobox.png"));
        button.setCursor(Cursor.getDefaultCursor());
        return button;
    }

    @Override
    protected ComboBoxEditor createEditor() {
        return new FlareComboBoxEditor();
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(component, graphics);

        hasFocus = comboBox.hasFocus();
        if (!comboBox.isEditable()) {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(graphics, r, hasFocus);
        }
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g, final int x, final int y, final int width, final int height) {
        element.paintBorder(c, g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        if (!isMinimumSizeDirty) {
            return new Dimension(cachedMinimumSize);
        }
        Dimension size = getDisplaySize();
        Insets insets = getInsets();
        //calculate the width and height of the button
        int buttonHeight = size.height + insets.top + insets.bottom;
        int buttonWidth = squareButton ? buttonHeight : arrowButton.getPreferredSize().width;
        //adjust the size based on the button width
        size.height += insets.top + insets.bottom;
        size.width += insets.left + insets.right + buttonWidth;

        cachedMinimumSize.setSize(size.width, size.height);
        isMinimumSizeDirty = false;

        return new Dimension(size);
    }

    @Override
    protected Dimension getDisplaySize() {
        Dimension size = super.getDisplaySize();
        currentValuePane.removeAll();
        return size;
    }

    @Override
    protected Dimension getDefaultSize() {
        FlareComboBoxRenderer renderer = new FlareComboBoxRenderer();
        Dimension size = getSizeForComponent(renderer.getListCellRendererComponent(listBox, " ", -1, false, false));
        currentValuePane.removeAll();
        return size;
    }

    @Override
    protected Dimension getSizeForComponent(Component comp) {
        StyleHelper.getClasses(comp).remove("flr-list-cell");
        if (comp.getParent() != currentValuePane) {
            currentValuePane.add(comp);
        }
        return comp.getPreferredSize();
    }

    // Prevents any background from being painted apart from our CSS Background
    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer<Object> renderer = getRendererWrapper();
        Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);

        StyleHelper.getClasses(c).remove("flr-list-cell");

        boolean shouldValidate = false;
        if (c instanceof JPanel) {
            shouldValidate = true;
        }

        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);
    }

    private ListCellRenderer<Object> renderer;
    private FlareComboBoxRendererWrapper wrapper;

    public FlareComboBoxRendererWrapper getRendererWrapper() {
        if (renderer == null || comboBox.getRenderer() != renderer) {
            renderer = comboBox.getRenderer();
            if (renderer == null) {
                return null;
            }

            wrapper = new FlareComboBoxRendererWrapper(renderer);
        }
        return wrapper;
    }

    @Override
    protected ListCellRenderer<Object> createRenderer() {
        return new FlareComboBoxRenderer();
    }

    @Override
    protected ComboPopup createPopup() {
        return new FlareComboBoxPopup(comboBox);
    }

    private static final class FlareComboBoxRendererWrapper implements ListCellRenderer<Object>, UIResource {

        private final ListCellRenderer<Object> renderer;

        FlareComboBoxRendererWrapper(ListCellRenderer<Object> renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean focus) {
            Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, focus);

            AWTComponentElement element = StyleTreeHelper.getElement(component);

            if (index >= 0) {
                element.getClasses().add("flr-list-cell");
            }

            element.focusHint(focus);
            element.activeHint(isSelected);

            return component;
        }
    }

    private class FlareComboBoxRenderer extends FLabel implements ListCellRenderer<Object>, UIResource {

        public FlareComboBoxRenderer() {
            super();
            setText(" ");
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean focus) {
            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText("");
            } else {
                String text = (value == null) ? " " : value.toString();

                if ("".equals(text)) {
                    text = " ";
                }
                setText(text);
            }

            if (comboBox != null) {
                setEnabled(comboBox.isEnabled());
                setComponentOrientation(comboBox.getComponentOrientation());
            }

            return this;
        }
    }

    private class FlareComboBoxLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return parent.getPreferredSize();
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return parent.getMinimumSize();
        }

        @Override
        public void layoutContainer(Container parent) {
            JComboBox<?> cb = (JComboBox<?>) parent;

            if (arrowButton != null) {
                int width = cb.getWidth();
                int height = cb.getHeight();

                FlareBorder border = (FlareBorder) cb.getBorder();
                Insets adjustedInsets = border.getMarginAndBorderInsets();

                int buttonHeight = height - (adjustedInsets.top + adjustedInsets.bottom);
                int buttonWidth = squareButton ? buttonHeight : arrowButton.getPreferredSize().width;

                if (comboBox.getComponentOrientation().isLeftToRight()) {
                    arrowButton.setBounds(width - (adjustedInsets.right + buttonWidth), adjustedInsets.top, buttonWidth, buttonHeight);
                } else {
                    arrowButton.setBounds(adjustedInsets.left, adjustedInsets.top, buttonWidth, buttonHeight);
                }
            }
            if (editor != null) {
                Rectangle cvb = rectangleForCurrentValue();
                editor.setBounds(cvb);
            }
        }
    }

    private static class FlareComboBoxEditor extends BasicComboBoxEditor.UIResource {

        @Override
        public JTextField createEditorComponent() {
            FTextField f = new FTextField("", 9);
            f.setName("ComboBox.textField");
            return f;
        }
    }
}
