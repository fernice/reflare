/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareRadioButtonUI
import javax.swing.Action
import javax.swing.Icon
import javax.swing.JRadioButton
import javax.swing.JToolTip

@Suppress("UNUSED")
open class FRadioButton : JRadioButton {

    constructor()
    constructor(action: Action?) : super(action)
    constructor(icon: Icon?) : super(icon)
    constructor(icon: Icon?, selected: Boolean) : super(icon, selected)
    constructor(text: String?) : super(text)
    constructor(text: String?, selected: Boolean) : super(text, selected)
    constructor(text: String?, icon: Icon?) : super(text, icon)
    constructor(text: String?, icon: Icon?, selected: Boolean) : super(text, icon, selected)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareRadioButtonUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}