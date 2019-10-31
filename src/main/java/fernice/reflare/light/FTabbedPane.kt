/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareTabbedPaneUI
import javax.swing.JTabbedPane
import javax.swing.JToolTip

@Suppress("UNUSED")
class FTabbedPane : JTabbedPane {

    constructor() : super()
    constructor(tabPlacement: Int) : super(tabPlacement)
    constructor(tabPlacement: Int, tabLayoutPolicy: Int) : super(tabPlacement, tabLayoutPolicy)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareTabbedPaneUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}