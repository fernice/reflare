/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareProgressBarUI
import javax.swing.BoundedRangeModel
import javax.swing.JProgressBar
import javax.swing.JToolTip

@Suppress("UNUSED")
open class ProgressBar : JProgressBar {

    constructor()
    constructor(model: BoundedRangeModel) : super(model)
    constructor(orientation: Int) : super(orientation)
    constructor(min: Int, max: Int) : super(min, max)
    constructor(orientation: Int, min: Int, max: Int) : super(orientation, min, max)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareProgressBarUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = ToolTip()
        toolTip.component = this
        return toolTip
    }
}