/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.ui

import fernice.reflare.style
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class InnerTextfield : JPanel() {

    private val textfield: PlaceholderTextfield
    val info: JLabel

    init {
        layout = BorderLayout()

        textfield = PlaceholderTextfield()
        textfield.columns = 30
        add(textfield, BorderLayout.CENTER)

        info = JLabel()
        info.style = "font-size:10px"
        add(info, BorderLayout.SOUTH)
    }

    override fun getBaseline(width: Int, height: Int): Int {
        val baseline = textfield.getBaseline(width, height)

        return textfield.y + baseline
    }
}