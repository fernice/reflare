/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareMenuUI
import org.fernice.reflare.ui.FlarePopupMenuUI
import java.beans.PropertyChangeListener
import javax.swing.Action
import javax.swing.JButton
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JToolTip

@Suppress("UNUSED")
open class FMenu : JMenu {

    constructor()
    constructor(action: Action) : super(action)
    constructor(text: String) : super(text)
    constructor(text: String, b: Boolean) : super(text, b)

    private var popupMenuUIInitialized = false

    override fun getPopupMenu(): JPopupMenu {
        val popupMenu = super.getPopupMenu()
        if (!popupMenuUIInitialized) {
            computeIntegrationDependent {
                popupMenu.ui = FlarePopupMenuUI()
            }
            popupMenuUIInitialized = true
        }
        return popupMenu
    }

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareMenuUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }

    override fun createActionComponent(a: Action): JMenuItem {
        val mi = object : FMenuItem() {
            override fun createActionPropertyChangeListener(a: Action): PropertyChangeListener {
                return createActionChangeListener(this) ?: super.createActionPropertyChangeListener(a)
            }
        }
        mi.horizontalTextPosition = JButton.TRAILING
        mi.verticalTextPosition = JButton.CENTER
        return mi
    }
}