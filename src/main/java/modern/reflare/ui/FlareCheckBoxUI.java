package modern.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import modern.reflare.element.ButtonElement;
import modern.reflare.element.CheckBoxElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

public class FlareCheckBoxUI extends FlareToggleButtonUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareCheckBoxUI();
    }

    @Override
    protected ButtonElement createElement(AbstractButton button) {
        return new CheckBoxElement((JCheckBox) button);
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
        super.paint(graphics, component);
    }

    @Override
    public void paintBorder(@NotNull Component c, @NotNull Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
    }
}
