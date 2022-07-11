package org.fernice.reflare.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import org.fernice.reflare.Defaults;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.MenuItemElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.platform.Platform;
import org.jetbrains.annotations.NotNull;
import sun.swing.MenuItemLayoutHelper;

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

        UIDefaultsHelper.installDefaultProperties(this, menuItem);

        menuItem.setMargin(Defaults.INSETS_EMPTY);
        acceleratorFont = Defaults.FONT_SERIF;

        defaultTextIconGap = 5;
        acceleratorDelimiter = Platform.isMac() ? "" : "+";

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
        // The method also determines the preferred width of the
        // parent popup menu (through DefaultMenuLayout class).
        // The menu width equals to the maximal width
        // among child menu items.

        // Menu item width will be a sum of the widest check icon, label,
        // arrow icon and accelerator text among neighbor menu items.
        // For the latest menu item we will know the maximal widths exactly.
        // It will be the widest menu item and it will determine
        // the width of the parent popup menu.

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // There is a conceptual problem: if user sets preferred size manually
        // for a menu item, this method won't be called for it
        // (see JComponent.getPreferredSize()),
        // maximal widths won't be calculated, other menu items won't be able
        // to take them into account and will be layouted in such a way,
        // as there is no the item with manual preferred size.
        // But after the first paint() method call, all maximal widths
        // will be correctly calculated and layout of some menu items
        // can be changed. For example, it can cause a shift of
        // the icon and text when user points a menu item by mouse.

        JMenuItem mi = (JMenuItem) c;
        MenuItemLayoutHelper lh = new FlareMenuItemLayoutHelper(mi, checkIcon, arrowIcon, MenuItemLayoutHelper.createMaxRect(), defaultTextIconGap,
                acceleratorDelimiter, mi.getComponentOrientation().isLeftToRight(), mi.getFont(), mi.getFont(), MenuItemLayoutHelper.useCheckAndArrow(menuItem),
                getPropertyPrefix());

        Dimension result = new Dimension();

        // Calculate the result width
        result.width = lh.getLeadingGap();
        MenuItemLayoutHelper.addMaxWidth(lh.getCheckSize(), lh.getAfterCheckIconGap(), result);
        // Take into account mimimal text offset.
        if ((!lh.isTopLevelMenu()) && (lh.getMinTextOffset() > 0) && (result.width < lh.getMinTextOffset())) {
            result.width = lh.getMinTextOffset();
        }
        MenuItemLayoutHelper.addMaxWidth(lh.getLabelSize(), lh.getGap(), result);
        MenuItemLayoutHelper.addMaxWidth(lh.getAccSize(), lh.getGap(), result);
        MenuItemLayoutHelper.addMaxWidth(lh.getArrowSize(), lh.getGap(), result);

        // Calculate the result height
        result.height = MenuItemLayoutHelper.max(lh.getCheckSize().getHeight(), lh.getLabelSize().getHeight(), lh.getAccSize().getHeight(),
                lh.getArrowSize().getHeight());

        // Take into account menu item insets
        Insets insets = lh.getMenuItem().getInsets();
        if (insets != null) {
            result.width += insets.left + insets.right;
            result.height += insets.top + insets.bottom;
        }

        // if the width is even, bump it up one. This is critical
        // for the focus dash line to draw properly
        if (result.width % 2 == 0) {
            result.width++;
        }

        // if the height is even, bump it up one. This is critical
        // for the text to center properly
        if (result.height % 2 == 0 && Boolean.TRUE != UIManager.get(getPropertyPrefix() + ".evenHeight")) {
            result.height++;
        }

        return result;
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        return super.getMaximumSize(c);
    }

    protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap) {
        // Save original graphics font and color
        Font holdf = g.getFont();
        Color holdc = g.getColor();

        JMenuItem mi = (JMenuItem) c;
        g.setFont(mi.getFont());

        Rectangle viewRect = new Rectangle(0, 0, mi.getWidth(), mi.getHeight());
        applyInsets(viewRect, mi.getInsets());

        MenuItemLayoutHelper lh = new FlareMenuItemLayoutHelper(mi, checkIcon, arrowIcon, viewRect, defaultTextIconGap, acceleratorDelimiter,
                mi.getComponentOrientation().isLeftToRight(), mi.getFont(), mi.getFont(), MenuItemLayoutHelper.useCheckAndArrow(menuItem), getPropertyPrefix());
        MenuItemLayoutHelper.LayoutResult lr = lh.layoutMenuItem();

        paintBackground(g, mi, background);
        paintCheckIcon(g, lh, lr, holdc, foreground);
        paintIcon(g, lh, lr, holdc);
        paintText(g, lh, lr);
        paintAccText(g, lh, lr);
        paintArrowIcon(g, lh, lr, foreground);

        // Restore original graphics font and color
        g.setColor(holdc);
        g.setFont(holdf);
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        element.paintBackground(g);
    }

    private void paintIcon(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color holdc) {
        if (lh.getIcon() != null) {
            Icon icon;
            ButtonModel model = lh.getMenuItem().getModel();
            if (!model.isEnabled()) {
                icon = lh.getMenuItem().getDisabledIcon();
            } else if (model.isPressed() && model.isArmed()) {
                icon = lh.getMenuItem().getPressedIcon();
                if (icon == null) {
                    // Use default icon
                    icon = lh.getMenuItem().getIcon();
                }
            } else {
                icon = lh.getMenuItem().getIcon();
            }

            if (icon != null) {
                icon.paintIcon(lh.getMenuItem(), g, lr.getIconRect().x, lr.getIconRect().y);
                g.setColor(holdc);
            }
        }
    }

    private void paintCheckIcon(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color holdc, Color foreground) {
        if (lh.getCheckIcon() != null) {
            ButtonModel model = lh.getMenuItem().getModel();
            if (model.isArmed() || (lh.getMenuItem() instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            } else {
                g.setColor(holdc);
            }
            if (lh.useCheckAndArrow()) {
                lh.getCheckIcon().paintIcon(lh.getMenuItem(), g, lr.getCheckRect().x, lr.getCheckRect().y);
            }
            g.setColor(holdc);
        }
    }

    private void paintAccText(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr) {
        if (!lh.getAccText().equals("")) {
            ButtonModel model = lh.getMenuItem().getModel();
            g.setFont(lh.getAccFontMetrics().getFont());
            g.setColor(lh.getMenuItem().getForeground());
            SwingUtilitiesHelper.drawString(lh.getMenuItem(), g, lh.getAccText(), lr.getAccRect().x, lr.getAccRect().y + lh.getAccFontMetrics().getAscent());
        }
    }

    private void paintText(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr) {
        if (!lh.getText().equals("")) {
            if (lh.getHtmlView() != null) {
                // Text is HTML
                lh.getHtmlView().paint(g, lr.getTextRect());
            } else {
                // Text isn't HTML
                paintText(g, lh.getMenuItem(), lr.getTextRect(), lh.getText());
            }
        }
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        FontMetrics fm = SwingUtilitiesHelper.getFontMetrics(menuItem, g);
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        g.setColor(menuItem.getForeground());
        SwingUtilitiesHelper.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }

    private void paintArrowIcon(Graphics g, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color foreground) {
        if (lh.getArrowIcon() != null) {
            ButtonModel model = lh.getMenuItem().getModel();
            if (model.isArmed() || (lh.getMenuItem() instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            }
            if (lh.useCheckAndArrow()) {
                lh.getArrowIcon().paintIcon(lh.getMenuItem(), g, lr.getArrowRect().x, lr.getArrowRect().y);
            }
        }
    }

    private void applyInsets(Rectangle rect, Insets insets) {
        if (insets != null) {
            rect.x += insets.left;
            rect.y += insets.top;
            rect.width -= (insets.right + rect.x);
            rect.height -= (insets.bottom + rect.y);
        }
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
}
