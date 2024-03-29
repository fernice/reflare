/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareScrollBarUI
import java.awt.Adjustable
import javax.swing.JScrollBar
import javax.swing.JToolTip
import javax.swing.JViewport
import javax.swing.Scrollable

@Suppress("UNUSED")
open class FScrollBar : JScrollBar {

    constructor()
    constructor(orientation: Int) : super(orientation)
    constructor(orientation: Int, value: Int, extent: Int, min: Int, max: Int) : super(orientation, value, extent, min, max)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareScrollBarUI(this) })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}