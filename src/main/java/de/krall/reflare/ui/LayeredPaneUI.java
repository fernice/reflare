package de.krall.reflare.ui;

import de.krall.reflare.FlareBorder;
import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.LayeredPaneElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.plaf.ComponentUI;
import org.jetbrains.annotations.NotNull;

public class LayeredPaneUI extends ComponentUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new LayeredPaneUI();
    }

    private ComponentElement element;

    @Override
    public void installUI(JComponent component) {
        super.installUI(component);

        final JLayeredPane layeredPane = (JLayeredPane) component;

        element = new LayeredPaneElement(layeredPane);

        layeredPane.setOpaque(false);
        layeredPane.setBorder(new FlareBorder(this));

        UIKt.registerUI(layeredPane, this);
    }

    @Override
    public void uninstallUI(JComponent rootPane) {
        UIKt.deregisterUI(rootPane);

        super.uninstallUI(rootPane);
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
