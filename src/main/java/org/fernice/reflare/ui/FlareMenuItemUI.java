package org.fernice.reflare.ui;

import fernice.reflare.FlareLookAndFeel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ComponentKt;
import org.fernice.reflare.element.MenuItemElement;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareMenuItemUI extends BasicMenuItemUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareMenuItemUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {

        if (element == null) {
            element = new MenuItemElement(menuItem);
        }

        menuItem.setOpaque(false);
        menuItem.setBorder(new FlareBorder(this));
        menuItem.setFont(FlareLookAndFeel.DEFAULT_FONT);
        menuItem.setMargin(new Insets(0, 0, 0, 0));

        acceleratorFont = FlareLookAndFeel.DEFAULT_FONT;

        defaultTextIconGap = 4;
        acceleratorDelimiter = "+";

        uninstallKeyboardActions();
        installKeyboardActions();

        ComponentKt.registerElement(menuItem, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(menuItem);
        super.uninstallDefaults();
    }

    @Override
    protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap) {
        if (element.getStyle().isNone()) {
            element.applyCSS();
        }

        return super.getPreferredMenuItemSize(c, checkIcon, arrowIcon, defaultTextIconGap);
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        element.paintBackground(menuItem, g);
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

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
