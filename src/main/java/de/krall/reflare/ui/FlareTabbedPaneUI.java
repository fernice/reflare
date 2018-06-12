package de.krall.reflare.ui;

import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.TabbedPaneElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import org.jetbrains.annotations.NotNull;

public class FlareTabbedPaneUI extends BasicTabbedPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareTabbedPaneUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();

        if (element == null) {
            element = new TabbedPaneElement(tabPane);
        }

        tabPane.setOpaque(false);
        tabPane.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(tabPane, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(tabPane);

        super.uninstallDefaults();
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        super.paint(graphics, component);
    }

    @Override
    protected void paintContentBorder(final Graphics g, final int tabPlacement, final int selectedIndex) {

    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(component, g);
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
