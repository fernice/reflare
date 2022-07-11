package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.MenuBarElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareMenuBarUI extends BasicMenuBarUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareMenuBarUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        if (element == null) {
            element = new MenuBarElement(menuBar);
        }

        UIDefaultsHelper.installDefaultProperties(this, menuBar);

        if (menuBar.getLayout() == null || menuBar.getLayout() instanceof UIResource) {
            menuBar.setLayout(new FlareMenuLayout(menuBar, BoxLayout.LINE_AXIS));
        }

        StyleTreeElementLookup.registerElement(menuBar, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(menuBar);
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
        element.paintBackground(graphics);
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
