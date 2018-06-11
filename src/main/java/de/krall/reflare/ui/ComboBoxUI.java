package de.krall.reflare.ui;

import de.krall.reflare.element.ComboBoxElement;
import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.LabelElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import org.jetbrains.annotations.NotNull;

public class ComboBoxUI extends BasicComboBoxUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new ComboBoxUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();

        element = new ComboBoxElement(comboBox);

        comboBox.setOpaque(false);
        comboBox.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(comboBox, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(comboBox);

        super.uninstallDefaults();
    }

    @Override
    protected JButton createArrowButton() {
        return new JButton();
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

    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
        } else {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }

        boolean shouldValidate = false;
        if (c instanceof JPanel) {
            shouldValidate = true;
        }

        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);
    }

    @Override
    protected ListCellRenderer createRenderer() {
        return new FlareComboBoxRenderer();
    }

    private class FlareComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource {

        public FlareComboBoxRenderer() {
            super();
            setText(" ");
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean focus) {

            LabelElement element = (LabelElement) ComponentKt.into(this);

            element.focusHint(focus);
            element.activeHint(isSelected);

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

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            LabelElement element = (LabelElement) ComponentKt.into(this);

            element.focusHint(false);
            element.activeHint(false);
        }
    }

    class FlareComboPopup extends BasicComboPopup {
        public FlareComboPopup( JComboBox combo ) {
            super(combo);
        }

        @Override
        protected void configureList() {
            list.setFont( comboBox.getFont() );
            list.setCellRenderer( comboBox.getRenderer() );
            list.setFocusable( false );
            list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            int selectedIndex = comboBox.getSelectedIndex();
            if ( selectedIndex == -1 ) {
                list.clearSelection();
            }
            else {
                list.setSelectedIndex( selectedIndex );
                list.ensureIndexIsVisible( selectedIndex );
            }
            installListListeners();
        }

        /**
         * @inheritDoc
         *
         * Overridden to take into account any popup insets specified in
         * SynthComboBoxUI
         */
        @Override
        protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
            return super.computePopupBounds(px, py, pw, ph);
        }

        protected int getPopupHeightForRowCount(int maxRowCount) {
            // Set the cached value of the minimum row count
            int minRowCount = Math.min( maxRowCount, comboBox.getItemCount() );
            int height = 0;
            ListCellRenderer renderer = list.getCellRenderer();
            Object value = null;

            for ( int i = 0; i < minRowCount; ++i ) {
                value = list.getModel().getElementAt( i );
                Component c = renderer.getListCellRendererComponent( list, value, i, false, false );
                ComponentKt.into(c).invalidateStyle();
                height += c.getPreferredSize().height;
            }

            if (height == 0) {
                height = comboBox.getHeight();
            }

            Border border = scroller.getViewportBorder();
            if (border != null) {
                Insets insets = border.getBorderInsets(null);
                height += insets.top + insets.bottom;
            }

            border = scroller.getBorder();
            if (border != null) {
                Insets insets = border.getBorderInsets(null);
                height += insets.top + insets.bottom;
            }

            return height;
        }
    }
}
