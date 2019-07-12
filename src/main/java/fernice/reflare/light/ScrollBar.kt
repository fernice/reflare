/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareScrollBarUI
import javax.swing.JScrollBar

@Suppress("UNUSED")
class ScrollBar : JScrollBar {

    constructor()
    constructor(orientation: Int) : super(orientation)
    constructor(orientation: Int, value: Int, extent: Int, min: Int, max: Int) : super(orientation, value, extent, min, max)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareScrollBarUI(this) })
    }
}