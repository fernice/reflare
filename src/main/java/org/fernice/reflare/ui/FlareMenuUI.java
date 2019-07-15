package org.fernice.reflare.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.MenuElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

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

        UIDefaultsHelper.installDefaultProperties(this, menu);

        menu.setMargin(Defaults.INSETS_EMPTY);
        acceleratorFont = Defaults.FONT_SERIF;

        defaultTextIconGap = 4;
        acceleratorDelimiter = "+";
        menu.setDelay(200);

        uninstallKeyboardActions();
        installKeyboardActions();

        StyleTreeElementLookup.registerElement(menu, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(menuItem);
        super.uninstallDefaults();
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        // element.pulseForComputation();
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        // element.pulseForComputation();
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        // element.pulseForComputation();
        return super.getMaximumSize(c);
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
        FontMetrics fm = SwingUtilitiesHelper.getFontMetrics(menuItem, g);
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        g.setColor(menuItem.getForeground());
        SwingUtilitiesHelper.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g, final int x, final int y, final int width, final int height) {
        element.paintBorder(c, g);
    }

    @Override
    protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap) {
        // StyleTreeHelper.getElement(c).applyCSS();

        return super.getPreferredMenuItemSize(c, checkIcon, arrowIcon, defaultTextIconGap);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
