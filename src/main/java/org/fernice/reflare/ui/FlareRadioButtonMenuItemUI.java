package org.fernice.reflare.ui;

import fernice.reflare.StyledImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.MenuItemElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareRadioButtonMenuItemUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {

        if (element == null) {
            element = new MenuItemElement(menuItem);
        }

        UIDefaultsHelper.installDefaultProperties(this, menuItem);

        menuItem.setMargin(Defaults.INSETS_EMPTY);
        acceleratorFont = Defaults.FONT_SERIF;

        defaultTextIconGap = 2;
        acceleratorDelimiter = "+";

        checkIcon = new FlareRadioMenuItemIcon();

        uninstallKeyboardActions();
        installKeyboardActions();

        StyleTreeElementLookup.registerElement(menuItem, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(menuItem);
        super.uninstallDefaults();
    }

    @Override
    protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap) {
        // StyleTreeHelper.getElement(c).applyCSS();

        return super.getPreferredMenuItemSize(c, checkIcon, arrowIcon, defaultTextIconGap);
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        element.paintBackground(g);
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        element.restyleIfNecessary();
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        element.restyleIfNecessary();
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        element.restyleIfNecessary();
        return super.getMaximumSize(c);
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        FontMetrics fm = SwingUtilitiesHelper.getFontMetrics(menuItem, g);
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        g.setColor(menuItem.getForeground());
        SwingUtilitiesHelper.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g) {
        element.paintBorder(g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }

    private static final class FlareRadioMenuItemIcon implements Icon, UIResource {

        private final Icon icon = StyledImageIcon.fromResource("/reflare/icons/radio-button-menu-item.png");

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isSelected()) {
                icon.paintIcon(c, g, x, y);
            }
        }

        @Override
        public int getIconWidth() {
            return icon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return icon.getIconHeight();
        }
    }
}
