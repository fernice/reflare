package de.krall.reflare.ui;

import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.PopupMenuElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import org.jetbrains.annotations.NotNull;

public class PopupMenuUI extends BasicPopupMenuUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new PopupMenuUI();
    }

    private ComponentElement element;

    @Override
    public void installDefaults() {
        super.installDefaults();

        element = new PopupMenuElement(popupMenu);

        popupMenu.setOpaque(false);
        popupMenu.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(popupMenu, element);
    }

    @Override
    public void uninstallDefaults() {
        ComponentKt.deregisterElement(popupMenu);

        super.uninstallDefaults();
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
