/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareRootPaneUI
import javax.swing.JRootPane

@Suppress("UNUSED")
class RootPane : JRootPane() {

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareRootPaneUI() })
    }
}