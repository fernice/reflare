/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.element.EditorPaneElement
import org.fernice.reflare.element.TextPaneElement
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JTextPane
import javax.swing.plaf.ComponentUI

@Suppress("ACCIDENTAL_OVERRIDE")
class FlareTextPaneUI(textPane: JTextPane) : FlareEditorPaneUI(textPane), FlareUI {

    override fun createElement(editorPane: JEditorPane): EditorPaneElement = TextPaneElement(editorPane as JTextPane)

    companion object {

        @JvmStatic
        fun createUI(component: JComponent): ComponentUI {
            return FlareTextPaneUI(component as JTextPane)
        }
    }
}