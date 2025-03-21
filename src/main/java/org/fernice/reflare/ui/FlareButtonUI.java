/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import fernice.reflare.StyledIcon;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

import org.fernice.reflare.element.ButtonElement;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.internal.SwingUtilitiesHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

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

        UIDefaultsHelper.installDefaultProperties(this, button);

        button.setBorderPainted(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        StyleTreeElementLookup.registerElement(button, this);
    }

    @Override
    protected void uninstallDefaults(AbstractButton button) {
        StyleTreeElementLookup.deregisterElement(button);
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        element.pulseForComputation();
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMaximumSize(c);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        super.paint(graphics, component);
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(g);
    }

    @Override
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Icon icon = b.getIcon();

        if (icon == null) {
            return;
        }

        if (!b.isEnabled()) {
            Icon disabledIcon = model.isSelected() ? b.getDisabledSelectedIcon() : b.getDisabledIcon();
            if (disabledIcon != null && (!(disabledIcon instanceof UIResource) || !(icon instanceof StyledIcon))) {
                icon = disabledIcon;
            }
        } else if (model.isPressed() && model.isArmed()) {
            Icon pressedIcon = b.getPressedIcon();
            if (pressedIcon != null) {
                icon = pressedIcon;
            }
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            Icon rolloverIcon = model.isSelected() ? b.getRolloverSelectedIcon() : b.getRolloverIcon();
            if (rolloverIcon != null) {
                icon = rolloverIcon;
            }
        } else if (model.isSelected()) {
            Icon selectedIcon = b.getSelectedIcon();
            if (selectedIcon != null) {
                icon = selectedIcon;
            }
        }

        icon.paintIcon(c, g, iconRect.x, iconRect.y);
    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        FontMetrics fm = SwingUtilitiesHelper.getFontMetrics(c, g);
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

        g.setColor(b.getForeground());
        SwingUtilitiesHelper.drawStringUnderlineCharAt(c, g, text, mnemonicIndex, textRect.x + getTextShiftOffset(),
                textRect.y + fm.getAscent() + getTextShiftOffset());
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
