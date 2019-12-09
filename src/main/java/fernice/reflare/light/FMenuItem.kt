/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareMenuItemUI
import javax.swing.Action
import javax.swing.Icon
import javax.swing.JMenuItem
import javax.swing.JToolTip

@Suppress("UNUSED")
open class FMenuItem : JMenuItem {

    constructor()
    constructor(action: Action?) : super(action)
    constructor(text: String?) : super(text)
    constructor(icon: Icon?) : super(icon)
    constructor(text: String?, icon: Icon?) : super(text, icon)
    constructor(text: String?, mnemonic: Int) : super(text, mnemonic)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareMenuItemUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}