/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import fernice.reflare.FlareLookAndFeel
import javax.swing.JComponent
import javax.swing.UIManager
import javax.swing.plaf.ComponentUI

internal inline fun integrationDependent(component: JComponent, block: () -> ComponentUI): ComponentUI {
    FlareLookAndFeel.installIntegration()

    return if (UIManager.getLookAndFeel() is FlareLookAndFeel) {
        UIManager.getUI(component)
    } else {
        block()
    }
}