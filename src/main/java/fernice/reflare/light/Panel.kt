/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlarePanelUI
import java.awt.LayoutManager
import javax.swing.JPanel

@Suppress("UNUSED")
class Panel : JPanel {

    constructor()
    constructor(doubleBuffered: Boolean) : super(doubleBuffered)
    constructor(layout: LayoutManager) : super(layout)
    constructor(layout: LayoutManager, doubleBuffered: Boolean) : super(layout, doubleBuffered)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlarePanelUI() })
    }
}