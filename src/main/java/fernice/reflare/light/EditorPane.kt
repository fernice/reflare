/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareEditorPaneUI
import java.net.URL
import javax.swing.JEditorPane

@Suppress("UNUSED")
open class EditorPane : JEditorPane {

    constructor()
    constructor(text: String) : super(text)
    constructor(type: String, text: String) : super(type, text)
    constructor(url: URL) : super(url)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareEditorPaneUI(this) })
    }
}