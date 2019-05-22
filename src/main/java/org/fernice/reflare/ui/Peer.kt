/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JScrollBar
import javax.swing.JTable
import javax.swing.JTextPane
import javax.swing.JToolTip
import javax.swing.plaf.ComponentUI
import javax.swing.table.JTableHeader

internal object FlareKotlinUIPeer {

    @JvmStatic
    fun createUI(component: JComponent): ComponentUI {
        return when (component) {
            is JScrollBar -> FlareScrollBarUI(component)
            is JTable -> FlareTableUI(component)
            is JTableHeader -> FlareTableHeaderUI(component)
            is JToolTip -> FlareToolTipUI(component)
            is JTextPane -> FlareTextPaneUI(component)
            is JEditorPane -> FlareEditorPaneUI(component)
            else -> throw IllegalArgumentException("unknown component type ${component::class.qualifiedName}")
        }
    }
}