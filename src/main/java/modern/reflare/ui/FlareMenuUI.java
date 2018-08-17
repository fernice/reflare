package modern.reflare.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import kotlin.Suppress;
import modern.reflare.FlareLookAndFeel;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.element.MenuElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;
import sun.swing.SwingUtilities2;

@SuppressWarnings("unused")
public class FlareMenuUI extends BasicMenuUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareMenuUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        JMenu menu = (JMenu) menuItem;

        if (element == null) {
            element = new MenuElement(menu);
        }

        menu.setOpaque(false);
        menu.setBorder(new FlareBorder(this));
        menu.setFont(FlareLookAndFeel.DEFAULT_FONT);
        menu.setMargin(new Insets(0, 0, 0, 0));

        acceleratorFont = FlareLookAndFeel.DEFAULT_FONT;

        // todo  these could either be non standard property or pseudo classes
        // todo  for the icon making it possible for them to have both a paddin
        // todo  g and a margin ultimately rendering the gap unnecessary
        defaultTextIconGap = 4;
        acceleratorDelimiter = "+";
        menu.setDelay(200);

        uninstallKeyboardActions();
        installKeyboardActions();

        ComponentKt.registerElement(menu, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(menuItem);
        super.uninstallDefaults();
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        JMenu menu = (JMenu) menuItem;

        if (!menu.isTopLevelMenu() || menu.isSelected()) {
            element.paintBackground(menuItem, g);
        }
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        FontMetrics fm = SwingUtilities2.getFontMetrics(menuItem, g);
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        g.setColor(menuItem.getForeground());
        SwingUtilities2.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
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
