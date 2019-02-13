package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ComponentKt;
import org.fernice.reflare.element.ListElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
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
        list.setBackground(FlareConstants.TRANSPARENT);
        list.setFont(FlareConstants.DEFAULT_FONT);
        list.setSelectionBackground(FlareConstants.TRANSPARENT);

        ComponentKt.registerElement(list, element);

        if (list.getCellRenderer() == null || (list.getCellRenderer() instanceof UIResource)) {
            list.setCellRenderer(new FlareListCellRenderer());
        }

        //rendererPane = next CellRendererPane();
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

    @Override
    protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer, ListModel dataModel, ListSelectionModel selModel,
            int leadIndex) {
        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);

        AWTComponentElement element = ComponentKt.getElement(rendererComponent);

        element.activeHint(isSelected);
        element.focusHint(cellHasFocus);

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;

        rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
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

            return this;
        }
    }
}
