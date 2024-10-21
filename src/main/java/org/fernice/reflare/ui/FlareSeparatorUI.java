package org.fernice.reflare.ui;

import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.SeparatorElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import java.awt.*;

@SuppressWarnings("unused")
public class FlareSeparatorUI extends BasicSeparatorUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareSeparatorUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JSeparator separator) {
        //        super.installDefaults();

        if (element == null) {
            element = new SeparatorElement(separator);
        }

        UIDefaultsHelper.installDefaultProperties(this, separator);

        StyleTreeElementLookup.registerElement(separator, this);
    }

    @Override
    protected void uninstallDefaults(JSeparator separator) {
        StyleTreeElementLookup.deregisterElement(separator);
        super.uninstallDefaults(separator);
    }

    private final Insets insets = new Insets(0, 0, 0, 0);

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Insets insets = c.getInsets(this.insets);

        if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL) {
            return new Dimension(insets.left + insets.right + 1, insets.top + insets.bottom);
        } else {
            return new Dimension(insets.left + insets.right, insets.top + insets.bottom + 1);
        }
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return super.getMaximumSize(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        element.paintBackground(g);

        Dimension s = c.getSize();

        Insets insets = c.getInsets(this.insets);

        if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL) {
            g.setColor(c.getForeground());
            g.drawLine(insets.left, insets.top, insets.left, s.height - insets.top - insets.bottom);
        } else  // HORIZONTAL
        {
            g.setColor(c.getForeground());
            g.drawLine(insets.left, insets.top, s.width - insets.left - insets.right, insets.top);
        }
    }

    @Override
    public void paintBorder(@NotNull Component c, @NotNull Graphics g) {
        element.paintBorder(g);
    }

    @Override
    public @NotNull ComponentElement getElement() {
        return element;
    }
}
