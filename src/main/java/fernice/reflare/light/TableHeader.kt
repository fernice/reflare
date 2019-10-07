/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareTableHeaderUI
import javax.swing.JToolTip
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumnModel

@Suppress("UNUSED")
class TableHeader : JTableHeader {

    constructor() : super()
    constructor(model: TableColumnModel) : super(model)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareTableHeaderUI(this) })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = ToolTip()
        toolTip.component = this
        return toolTip
    }
}