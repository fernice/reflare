package de.krall.reflare.ui;

import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.LabelElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import org.jetbrains.annotations.NotNull;

public class LabelUI extends BasicLabelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new LabelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JLabel label) {
        element = new LabelElement(label);

        label.setOpaque(false);
        label.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(label, element);
    }

    @Override
    protected void uninstallDefaults(JLabel label) {
        ComponentKt.deregisterElement(label);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component,graphics);

        super.paint(graphics,component);
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
