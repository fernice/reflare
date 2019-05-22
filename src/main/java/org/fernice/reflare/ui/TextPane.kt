/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.element.TextPaneElement
import javax.swing.JTextPane

class FlareTextPaneUI(textPane: JTextPane) : FlareEditorPaneUI(textPane), FlareUI {

    override val element = TextPaneElement(textPane)
}