/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareTableUI
import java.util.Vector
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JToolTip
import javax.swing.JViewport
import javax.swing.ListSelectionModel
import javax.swing.SwingUtilities
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel

@Suppress("UNUSED")
open class FTable : JTable {

    constructor()
    constructor(model: TableModel) : super(model)
    constructor(model: TableModel, columnModel: TableColumnModel) : super(model, columnModel)
    constructor(model: TableModel, columnModel: TableColumnModel, selectionModel: ListSelectionModel) : super(model, columnModel, selectionModel)
    constructor(rows: Int, columns: Int) : super(rows, columns)
    constructor(rowData: Vector<*>, columnNames: Vector<*>) : super(rowData, columnNames)
    constructor(rowData: Array<out Array<out Any>>, columnNames: Array<out Any>) : super(rowData, columnNames)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareTableUI(this) })
    }

    override fun createDefaultTableHeader(): JTableHeader {
        return FTableHeader(columnModel)
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }

    override fun configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane()

        val parent = SwingUtilities.getUnwrappedParent(this)
        if (parent is JViewport) {
            val scrollPane = parent.parent
            if (scrollPane is JScrollPane) {
                scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, FTableScrollPaneCorner())
            }
        }
    }
}