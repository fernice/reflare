/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareListUI
import java.util.Vector
import javax.swing.JList
import javax.swing.JToolTip
import javax.swing.ListModel

@Suppress("UNUSED")
open class FList<E> : JList<E> {

    constructor()
    constructor(elements: Array<E>) : super(elements)
    constructor(model: ListModel<E>) : super(model)
    constructor(elements: Vector<E>) : super(elements)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareListUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}