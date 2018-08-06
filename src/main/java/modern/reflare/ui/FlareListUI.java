package modern.reflare.ui;

import modern.reflare.element.ComponentElement;
import modern.reflare.element.LabelElement;
import modern.reflare.element.ListElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;
import org.jetbrains.annotations.NotNull;

// fixme(kralli) render needs to be wrapped
public class FlareListUI extends BasicListUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareListUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        if (element == null) {
            element = new ListElement(list);
        }

        list.setOpaque(false);
        list.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(list, element);

        if (list.getCellRenderer() == null || (list.getCellRenderer() instanceof UIResource)) {
            list.setCellRenderer(new FlareListCellRenderer());
        }

        //rendererPane = new CellRendererPane();
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(list);

        if (list.getCellRenderer() instanceof UIResource) {
            list.setCellRenderer(null);
        }

    }

    private final static int heightChanged = 1 << 8;

    public void invalidateHeight() {
        updateLayoutStateNeeded = heightChanged;
    }

    public CellRendererPane getRenderPane() {
        return rendererPane;
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(component, graphics);

        super.paint(graphics, component);
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

    private static class FlareListCellRenderer extends DefaultListCellRenderer.UIResource {

        @Override
        public void setBorder(Border b) {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, isSelected, focus);

            LabelElement element = (LabelElement) ComponentKt.into(this);

            element.focusHint(focus);
            element.activeHint(isSelected);

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
}
