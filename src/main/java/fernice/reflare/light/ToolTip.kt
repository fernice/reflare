/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareToolTipUI
import javax.swing.JToolTip

@Suppress("UNUSED")
open class ToolTip : JToolTip() {

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareToolTipUI(this) })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = ToolTip()
        toolTip.component = this
        return toolTip
    }
}