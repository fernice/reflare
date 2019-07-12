/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareMenuUI
import javax.swing.Action
import javax.swing.JMenu

@Suppress("UNUSED")
class Menu : JMenu {

    constructor()
    constructor(action: Action) : super(action)
    constructor(text: String) : super(text)
    constructor(text: String, b: Boolean) : super(text, b)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareMenuUI() })
    }
}