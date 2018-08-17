package modern.reflare.ui;

import modern.reflare.element.ComponentElement;
import modern.reflare.element.ViewportElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareViewportUI extends BasicViewportUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareViewportUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JComponent component) {
        JViewport viewport = (JViewport) component;

        if (element == null) {
            element = new ViewportElement(viewport);
        }

        component.setOpaque(false);
       // component.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(component, element);
    }

    @Override
    protected void uninstallDefaults(JComponent component) {
        ComponentKt.deregisterElement(component);
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
