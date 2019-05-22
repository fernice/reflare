package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ScrollPaneElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareScrollPaneUI extends BasicScrollPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareScrollPaneUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JScrollPane scrollPane) {
        super.installDefaults(scrollPane);

        if (element == null) {
            element = new ScrollPaneElement(scrollPane);
        }

        scrollPane.setOpaque(false);
        scrollPane.setBorder(new FlareBorder(this));

        StyleTreeElementLookup.registerElement(scrollPane, this);
    }

    @Override
    protected void uninstallDefaults(JScrollPane scrollPane) {
        StyleTreeElementLookup.deregisterElement(scrollPane);

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
