package modern.reflare.ui;

import modern.reflare.element.ComponentElement;
import modern.reflare.element.PopupMenuElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
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
