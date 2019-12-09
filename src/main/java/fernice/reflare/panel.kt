/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("PanelHelper")
package fernice.reflare

import org.fernice.reflare.ui.FlarePanelUI
import javax.swing.JPanel


var JPanel.isFocusDismissible: Boolean
    get() = FlarePanelUI.isFocusDismissHandlingInstalled(this)
    set(value) {
        if (value) {
            FlarePanelUI.installFocusDismissHandling(this)
        } else {
            FlarePanelUI.uninstallFocusDismissHandling(this)
        }
    }