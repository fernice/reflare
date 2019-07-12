/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareTextPaneUI
import javax.swing.JTextPane
import javax.swing.text.StyledDocument

@Suppress("UNUSED")
class TextPane : JTextPane {

    constructor()
    constructor(document: StyledDocument) : super(document)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareTextPaneUI(this) })
    }
}