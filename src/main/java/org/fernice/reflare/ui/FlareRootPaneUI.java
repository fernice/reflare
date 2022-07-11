package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.RootPaneElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
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

        UIDefaultsHelper.installDefaultProperties(this, rootPane);

        StyleTreeElementLookup.registerElement(rootPane, this);
    }

    @Override
    protected void uninstallDefaults(JRootPane rootPane) {
        StyleTreeElementLookup.deregisterElement(rootPane);

        super.uninstallDefaults(rootPane);
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
