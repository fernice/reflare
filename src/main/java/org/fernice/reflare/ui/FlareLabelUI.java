package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.LabelElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareLabelUI extends BasicLabelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareLabelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JLabel label) {
        if (element == null) {
            element = new LabelElement(label);
        }

        FlareUIHelper.installDefaults(this, label);

        StyleTreeElementLookup.registerElement(label, this);
    }

    @Override
    protected void uninstallDefaults(JLabel label) {
        StyleTreeElementLookup.deregisterElement(label);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        super.paint(graphics, component);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        paintEnabledText(l, g, s, textX, textY);
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
