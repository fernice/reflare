/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareLabelUI
import javax.swing.Icon
import javax.swing.JLabel

@Suppress("UNUSED")
open class Label : JLabel {

    constructor()
    constructor(icon: Icon) : super(icon)
    constructor(icon: Icon, horizontalAlignment: Int) : super(icon, horizontalAlignment)
    constructor(text: String) : super(text)
    constructor(text: String, horizontalAlignment: Int) : super(text, horizontalAlignment)
    constructor(text: String, icon: Icon, horizontalAlignment: Int) : super(text, icon, horizontalAlignment)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareLabelUI() })
    }
}