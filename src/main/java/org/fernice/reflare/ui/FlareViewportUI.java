package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.element.ViewportElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareViewportUI extends BasicViewportUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareViewportUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JComponent component) {
        JViewport viewport = (JViewport) component;

        if (element == null) {
            element = new ViewportElement(viewport);
        }

        // viewport is a special case in term of defaults as it
        // primarily draws other components it does not require
        // and partially does not support the usual defaults
        component.setOpaque(false);
        component.setFont(Defaults.FONT_SERIF);

        StyleTreeElementLookup.registerElement(component, this);
    }

    @Override
    protected void uninstallDefaults(JComponent component) {
        StyleTreeElementLookup.deregisterElement(component);
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
