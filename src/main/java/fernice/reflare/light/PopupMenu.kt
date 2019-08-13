/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlarePopupMenuUI
import javax.swing.JPopupMenu
import javax.swing.plaf.PopupMenuUI

@Suppress("UNUSED")
open class PopupMenu : JPopupMenu {

    constructor()
    constructor(label: String) : super(label)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlarePopupMenuUI() })
    }

    companion object {

        internal fun setUI(popupMenu: JPopupMenu, ui: PopupMenuUI) {
            popupMenu.ui = ui
        }
    }
}