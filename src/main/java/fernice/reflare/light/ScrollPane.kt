/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareScrollBarUI
import org.fernice.reflare.ui.FlareScrollPaneUI
import java.awt.Component
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.JViewport

@Suppress("UNUSED")
open class ScrollPane : JScrollPane {

    constructor()
    constructor(component: Component?) : super(component)
    constructor(component: Component?, vsbPolicy: Int, hsbPolicy: Int) : super(component, vsbPolicy, hsbPolicy)
    constructor(vsbPolicy: Int, hsbPolicy: Int) : super(vsbPolicy, hsbPolicy)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareScrollPaneUI() })
    }

    override fun createVerticalScrollBar(): JScrollBar {
        return ScrollBar(JScrollBar.VERTICAL)
    }

    override fun createHorizontalScrollBar(): JScrollBar {
        return ScrollBar(JScrollBar.HORIZONTAL)
    }

    override fun createViewport(): JViewport {
        return Viewport()
    }

    protected inner class ScrollBar(orientation: Int) : JScrollPane.ScrollBar(orientation) {

        override fun updateUI() {
            super.setUI(integrationDependent(this) { FlareScrollBarUI(this) })
        }
    }
}