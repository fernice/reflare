package org.fernice.reflare.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.element.TabbedPaneElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareTabbedPaneUI extends BasicTabbedPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareTabbedPaneUI();
    }

    private TabbedPaneElement element;

    @Override
    protected void installDefaults() {
        if (element == null) {
            element = new TabbedPaneElement(tabPane);
        }

        UIDefaultsHelper.installDefaultProperties(this, tabPane);

        tabInsets = new Insets(5, 5, 5, 5);
        selectedTabPadInsets = Defaults.INSETS_EMPTY;
        tabAreaInsets = Defaults.INSETS_EMPTY;
        contentBorderInsets = Defaults.INSETS_EMPTY;

        StyleTreeElementLookup.registerElement(tabPane, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(tabPane);
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
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        //paint(graphics);
        super.paint(graphics, component);
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isSelected && tabPane.getTabComponentAt(tabIndex) == null) {
            int thickness = 3;

            Color foreground = tabPane.getForegroundAt(tabIndex);

            g.setColor(tabPane.hasFocus() ? foreground.brighter() : foreground);
            g.fillRect(x, y + (h - thickness), w, thickness);
        }
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect,
            boolean isSelected) {
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(Defaults.COLOR_GRAY_TRANSLUCENT);
        g.drawLine(x, y - 1, x + w, y - 1);
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(g);
    }

    @Override
    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        return tabPane.getTabComponentAt(tabIndex) == null ? tabInsets : Defaults.INSETS_EMPTY;
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

    private static Color darker(Color color, float factor) {
        return new Color(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0), color.getAlpha());
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        // Subtract unwanted constant added by super implementation
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) - 2;
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        // Subtract unwanted constant added by super implementation
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) - 3;
    }
}
