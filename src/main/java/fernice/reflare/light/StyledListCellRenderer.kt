/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.element.element
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

interface StyledListCellRenderer<T> : ListCellRenderer<T> {

    override fun getListCellRendererComponent(list: JList<out T>, value: T, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val component = getStyledListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        val element = component.element

        element.activeHint(isSelected)
        element.focusHint(cellHasFocus)

        return component
    }

    fun getStyledListCellRendererComponent(list: JList<out T>, value: T, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component
}