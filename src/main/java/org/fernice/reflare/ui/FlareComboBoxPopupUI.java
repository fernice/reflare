/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import org.fernice.reflare.element.ComboBoxPopupElement;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareComboBoxPopupUI extends BasicPopupMenuUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareComboBoxPopupUI();
    }

    private ComponentElement element;

    @Override
    public void installDefaults() {
        super.installDefaults();

        if (element == null) {
            element = new ComboBoxPopupElement((FlareComboBoxPopup) popupMenu);
        }

        UIDefaultsHelper.installDefaultProperties(this, popupMenu);

        StyleTreeElementLookup.registerElement(popupMenu, this);
    }

    @Override
    public void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(popupMenu);

        super.uninstallDefaults();
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
