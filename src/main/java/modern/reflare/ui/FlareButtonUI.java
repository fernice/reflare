package modern.reflare.ui;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import modern.reflare.FlareLookAndFeel;
import modern.reflare.element.ButtonElement;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import org.jetbrains.annotations.NotNull;
import sun.swing.SwingUtilities2;

public class FlareButtonUI extends BasicButtonUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareButtonUI();
    }

    private ComponentElement element;

    protected ButtonElement createElement(AbstractButton button) {
        return new ButtonElement(button);
    }

    @Override
    protected void installDefaults(AbstractButton button) {
        if (element == null) {
            element = createElement(button);
        }

        button.setOpaque(false);
        button.setBorder(new FlareBorder(this));
        button.setFont(FlareLookAndFeel.DEFAULT_FONT);

        ComponentKt.registerElement(button, element);
    }

    @Override
    protected void uninstallDefaults(AbstractButton button) {
        ComponentKt.deregisterElement(button);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        super.paint(graphics, component);
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(component, g);
    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

        g.setColor(b.getForeground());
        SwingUtilities2
                .drawStringUnderlineCharAt(c, g, text, mnemonicIndex, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
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
