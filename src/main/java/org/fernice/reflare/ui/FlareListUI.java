/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import fernice.reflare.light.DefaultListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ListElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.element.StyleTreeHelper;
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

        list.remove(rendererPane);
        rendererPane = new org.fernice.reflare.render.CellRendererPane();
        list.add(rendererPane);

        list.setOpaque(false);
        UIDefaultsHelper.installDefaultProperties(this, list);
        list.setSelectionBackground(Defaults.COLOR_TRANSPARENT);

        StyleTreeElementLookup.registerElement(list, this);

        if (list.getCellRenderer() == null || (list.getCellRenderer() instanceof UIResource)) {
            list.setCellRenderer(new FlareListCellRenderer());
        }
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(list);

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
    public Dimension getMinimumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        element.pulseForComputation();
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMaximumSize(c);
    }

    @Override
    protected void updateLayoutState() {
        super.updateLayoutState();

        rendererPane.removeAll();
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(graphics);

        super.paint(graphics, component);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g) {
        element.paintBorder(g);
    }

    @Override
    protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer, ListModel dataModel, ListSelectionModel selModel,
            int leadIndex) {
        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);

        AWTComponentElement element = StyleTreeHelper.getElement(rendererComponent);

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

    }
}
