/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareViewportUI
import javax.swing.JToolTip
import javax.swing.JViewport

@Suppress("UNUSED")
open class FViewport : JViewport() {

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareViewportUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}