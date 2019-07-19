package org.fernice.reflare.ui;

import fernice.reflare.light.Button;
import fernice.reflare.light.Label;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
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

    @Override
    protected void installDefaults() {
        super.installDefaults();

        element = new ComboBoxElement(comboBox);

        currentValuePane = new CellRendererPane();
        squareButton = false;

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
    protected Button createArrowButton() {
        Button button = new Button();
        button.setCursor(Cursor.getDefaultCursor());
        return button;
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        hasFocus = comboBox.hasFocus();
        if (!comboBox.isEditable()) {
            Rectangle r = rectangleForCurrentValue();
            element.paintBackground(component, graphics);
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
    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension(cachedMinimumSize);
        }
        Dimension size = getDisplaySize();
        Insets insets = getInsets();
        //calculate the width and height of the button
        int buttonHeight = size.height + insets.top + insets.bottom;
        int buttonWidth = squareButton ? buttonHeight : arrowButton.getPreferredSize().width;
        //adjust the size based on the button width
        size.height += insets.top + insets.bottom;
        size.width +=  insets.left + insets.right + buttonWidth;

        cachedMinimumSize.setSize( size.width, size.height );
        isMinimumSizeDirty = false;

        return new Dimension(size);
    }

    // Prevents any background from being painted apart from our CSS Background
    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer renderer = getRendererWrapper();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
        } else {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
        }

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
    protected ListCellRenderer createRenderer() {
        return new FlareComboBoxRenderer();
    }

    @Override
    protected ComboPopup createPopup() {
        return new FlareComboBoxPopup(comboBox);
    }

    private class FlareComboBoxRendererWrapper implements ListCellRenderer<Object>, UIResource {

        private final ListCellRenderer<Object> renderer;

        FlareComboBoxRendererWrapper(ListCellRenderer<Object> renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean focus) {
            Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, focus);

            AWTComponentElement element = StyleTreeHelper.getElement(component);

            element.focusHint(focus);
            element.activeHint(isSelected);

            return component;
        }
    }

    private class FlareComboBoxRenderer extends Label implements ListCellRenderer<Object>, UIResource {

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
            JComboBox cb = (JComboBox) parent;
            int width = cb.getWidth();
            int height = cb.getHeight();

            FlareBorder border = (FlareBorder) cb.getBorder();
            Insets buttonInsets = border.getMarginAndBorderInsets();

            int buttonHeight = height - (buttonInsets.top + buttonInsets.bottom);
            int buttonWidth = buttonHeight;
            if (arrowButton != null) {
                Insets arrowInsets = arrowButton.getInsets();
                buttonWidth = squareButton ? buttonHeight : arrowButton.getPreferredSize().width + arrowInsets.left + arrowInsets.right;
            }
            Rectangle cvb;

            if (arrowButton != null) {
                if (comboBox.getComponentOrientation().isLeftToRight()) {
                    arrowButton.setBounds(width - (buttonInsets.right + buttonWidth), buttonInsets.top, buttonWidth, buttonHeight);
                } else {
                    arrowButton.setBounds(buttonInsets.left, buttonInsets.top, buttonWidth, buttonHeight);
                }
            }
            if (editor != null) {
                cvb = rectangleForCurrentValue();
                editor.setBounds(cvb);
            }
        }
    }
}
