package modern.reflare.ui;

import modern.reflare.FlareLookAndFeel;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.element.PanelElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import org.jetbrains.annotations.NotNull;

public class PanelUI extends BasicPanelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new PanelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JPanel panel) {
        panel.setFont(FlareLookAndFeel.DEFAULT_FONT);

        if (element == null) {
            element = new PanelElement(panel);
        }

        panel.setOpaque(false);
        panel.setBorder(new FlareBorder(this));

        ComponentKt.registerElement(panel, element);
    }

    @Override
    protected void uninstallDefaults(JPanel panel) {
        ComponentKt.deregisterElement(panel);
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
