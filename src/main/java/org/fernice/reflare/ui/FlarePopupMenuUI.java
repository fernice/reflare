package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.PopupMenuElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.layout.VerticalLayout;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlarePopupMenuUI extends BasicPopupMenuUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlarePopupMenuUI();
    }

    private ComponentElement element;

    @Override
    public void installDefaults() {
        super.installDefaults();

        if (element == null) {
            element = new PopupMenuElement(popupMenu);
        }

        UIDefaultsHelper.installDefaultProperties(this, popupMenu);

        StyleTreeElementLookup.registerElement(popupMenu, this);
    }

    @Override
    public void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(popupMenu);

        super.uninstallDefaults();
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

    private static final class FlarePopupMenuLayout extends VerticalLayout implements UIResource {

    }
}
