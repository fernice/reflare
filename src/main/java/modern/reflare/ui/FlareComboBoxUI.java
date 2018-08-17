package modern.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import modern.reflare.element.AWTComponentElement;
import modern.reflare.element.ComboBoxElement;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
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
    protected void installComponents() {
        super.installComponents();

        arrowButton.setFocusable(false);
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

    // Prevents any background from being painted apart from our CSS Background
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer renderer = getRendererWrapper();
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

            AWTComponentElement element = ComponentKt.into(component);

            element.focusHint(focus);
            element.activeHint(isSelected);
            element.getCache().setUncachable();

            return component;
        }
    }

    private class FlareComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource {

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
}
