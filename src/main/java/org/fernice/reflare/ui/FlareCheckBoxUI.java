/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import fernice.reflare.StyledImageIcon;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.fernice.reflare.element.ButtonElement;
import org.fernice.reflare.element.CheckBoxElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
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
    protected void installDefaults(AbstractButton button) {
        super.installDefaults(button);

        button.setIcon(StyledImageIcon.fromResource("/reflare/icons/checkbox.png"));
        button.setSelectedIcon(StyledImageIcon.fromResource("/reflare/icons/checkbox-checked.png"));
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
        super.paint(graphics, component);
    }

    @Override
    public void paintBorder(@NotNull Component c, @NotNull Graphics g) {
        super.paintBorder(c, g);
    }
}
