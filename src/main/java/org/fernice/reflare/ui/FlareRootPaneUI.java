package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ComponentKt;
import org.fernice.reflare.element.RootPaneElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareRootPaneUI extends BasicRootPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareRootPaneUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JRootPane rootPane) {
        super.installDefaults(rootPane);

        if (element == null) {
            element = new RootPaneElement(rootPane);
        }

        rootPane.setOpaque(false);
        rootPane.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(rootPane, element);
    }

    @Override
    protected void uninstallDefaults(JRootPane rootPane) {
        ComponentKt.deregisterElement(rootPane);

        super.uninstallDefaults(rootPane);
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
