/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareComboBoxUI
import java.util.Vector
import javax.swing.ComboBoxModel
import javax.swing.JComboBox
import javax.swing.JToolTip

@Suppress("UNUSED")
open class FComboBox<E> : JComboBox<E> {

    constructor()
    constructor(elements: Array<E>) : super(elements)
    constructor(model: ComboBoxModel<E>) : super(model)
    constructor(elements: Vector<E>) : super(elements)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareComboBoxUI() })
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }
}