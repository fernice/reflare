package de.krall.reflare.ui;

import de.krall.reflare.FlareBorder;
import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.RootPaneElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import org.jetbrains.annotations.NotNull;

public class RootPaneUI extends BasicRootPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new RootPaneUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JRootPane rootPane) {
        super.installDefaults(rootPane);

        element = new RootPaneElement(rootPane);

        rootPane.setOpaque(false);
        rootPane.setBorder(new FlareBorder(this));

        UIKt.registerUI(rootPane, this);
    }

    @Override
    protected void uninstallDefaults(JRootPane rootPane) {
        UIKt.deregisterUI(rootPane);

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
