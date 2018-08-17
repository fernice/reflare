package modern.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import modern.reflare.FlareLookAndFeel;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.element.ProgressBarElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareProgressBarUI extends BasicProgressBarUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareProgressBarUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        if (element == null) {
            element = new ProgressBarElement(progressBar);
        }

        progressBar.setOpaque(false);
        progressBar.setBorder(new FlareBorder(this));
        progressBar.setFont(FlareLookAndFeel.DEFAULT_FONT);

        ComponentKt.registerElement(progressBar, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(progressBar);
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
