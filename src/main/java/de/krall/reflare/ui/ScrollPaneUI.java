package de.krall.reflare.ui;

import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.ScrollPaneElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import org.jetbrains.annotations.NotNull;

public class ScrollPaneUI extends BasicScrollPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new ScrollPaneUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JScrollPane scrollPane) {
        super.installDefaults(scrollPane);

        element = new ScrollPaneElement(scrollPane);

        scrollPane.setOpaque(false);
        scrollPane.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(scrollPane, element);
    }

    @Override
    protected void uninstallDefaults(JScrollPane scrollPane) {
        ComponentKt.deregisterElement(scrollPane);

        super.uninstallDefaults(scrollPane);
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
