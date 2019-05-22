/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element

import javax.swing.JTable
import javax.swing.table.JTableHeader

open class TableElement(table: JTable) : ComponentElement(table) {

    override fun localName(): String {
        return "table"
    }
}

open class TableHeaderElement(header: JTableHeader) : ComponentElement(header) {

    override fun localName(): String {
        return "th"
    }
}