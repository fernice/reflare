package org.fernice.reflare.ui;

import fernice.reflare.FlareLookAndFeel;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ComponentKt;
import org.fernice.reflare.element.MenuBarElement;
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
        menuBar.setFont(FlareLookAndFeel.DEFAULT_FONT);

        if (element == null) {
            element = new MenuBarElement(menuBar);
        }

        menuBar.setOpaque(false);
        menuBar.setBorder(new FlareBorder(this));

        if (menuBar.getLayout() == null || menuBar.getLayout() instanceof UIResource) {
            menuBar.setLayout(new FlareMenuLayout(menuBar, BoxLayout.LINE_AXIS));
        }

        ComponentKt.registerElement(menuBar, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(menuBar);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(component, graphics);
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
}