/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JScrollBar
import javax.swing.JToolTip
import javax.swing.plaf.ComponentUI

object FlareKotlinUIPeer {

    @JvmStatic
    fun createUI(component: JComponent): ComponentUI {
        return when (component) {
            is JScrollBar -> FlareScrollBarUI(component)
            is JToolTip -> FlareToolTipUI(component)
            is JEditorPane -> FlareEditorPaneUI(component)
            else -> throw IllegalArgumentException("unknown component type ${component::class.qualifiedName}")
        }
    }
}