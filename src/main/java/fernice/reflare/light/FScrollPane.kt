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
import javax.swing.JToolTip
import javax.swing.JViewport
import javax.swing.Scrollable

@Suppress("UNUSED")
open class FScrollPane : JScrollPane {

    constructor()
    constructor(component: Component?) : super(component)
    constructor(component: Component?, vsbPolicy: Int, hsbPolicy: Int) : super(component, vsbPolicy, hsbPolicy)
    constructor(vsbPolicy: Int, hsbPolicy: Int) : super(vsbPolicy, hsbPolicy)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareScrollPaneUI(this) })
    }

    override fun createVerticalScrollBar(): JScrollBar {
        return ScrollBar(JScrollBar.VERTICAL)
    }

    override fun createHorizontalScrollBar(): JScrollBar {
        return ScrollBar(JScrollBar.HORIZONTAL)
    }

    override fun createViewport(): JViewport {
        return FViewport()
    }

    override fun createToolTip(): JToolTip {
        val toolTip = FToolTip()
        toolTip.component = this
        return toolTip
    }

    protected inner class ScrollBar(orientation: Int) : FScrollBar(orientation) {

        init {
            this.putClientProperty("JScrollBar.fastWheelScrolling", true)
        }

        override fun updateUI() {
            super.setUI(integrationDependent(this) { FlareScrollBarUI(this) })
        }

        /**
         * Set to true when the unit increment has been explicitly set.
         * If this is false the viewport's view is obtained and if it
         * is an instance of `Scrollable` the unit increment
         * from it is used.
         */
        private var unitIncrementSet = false

        /**
         * Set to true when the block increment has been explicitly set.
         * If this is false the viewport's view is obtained and if it
         * is an instance of `Scrollable` the block increment
         * from it is used.
         */
        private var blockIncrementSet = false

        /**
         * Messages super to set the value, and resets the
         * `unitIncrementSet` instance variable to true.
         *
         * @param unitIncrement the new unit increment value, in pixels
         */
        override fun setUnitIncrement(unitIncrement: Int) {
            unitIncrementSet = true
            this.putClientProperty("JScrollBar.fastWheelScrolling", null)
            super.setUnitIncrement(unitIncrement)
        }

        /**
         * Computes the unit increment for scrolling if the viewport's
         * view is a `Scrollable` object.
         * Otherwise return `super.getUnitIncrement`.
         *
         * @param direction less than zero to scroll up/left,
         * greater than zero for down/right
         * @return an integer, in pixels, containing the unit increment
         * @see Scrollable.getScrollableUnitIncrement
         */
        override fun getUnitIncrement(direction: Int): Int {
            val vp = getViewport()
            return if (!unitIncrementSet && vp != null &&
                vp.view is Scrollable
            ) {
                val view = vp.view as Scrollable
                val vr = vp.viewRect
                view.getScrollableUnitIncrement(vr, getOrientation(), direction)
            } else {
                super.getUnitIncrement(direction)
            }
        }

        /**
         * Messages super to set the value, and resets the
         * `blockIncrementSet` instance variable to true.
         *
         * @param blockIncrement the new block increment value, in pixels
         */
        override fun setBlockIncrement(blockIncrement: Int) {
            blockIncrementSet = true
            this.putClientProperty("JScrollBar.fastWheelScrolling", null)
            super.setBlockIncrement(blockIncrement)
        }

        /**
         * Computes the block increment for scrolling if the viewport's
         * view is a `Scrollable` object.  Otherwise
         * the `blockIncrement` equals the viewport's width
         * or height.  If there's no viewport return
         * `super.getBlockIncrement`.
         *
         * @param direction less than zero to scroll up/left,
         * greater than zero for down/right
         * @return an integer, in pixels, containing the block increment
         * @see Scrollable.getScrollableBlockIncrement
         */
        override fun getBlockIncrement(direction: Int): Int {
            val vp = getViewport()
            return if (blockIncrementSet || vp == null) {
                super.getBlockIncrement(direction)
            } else if (vp.view is Scrollable) {
                val view = vp.view as Scrollable
                val vr = vp.viewRect
                view.getScrollableBlockIncrement(vr, getOrientation(), direction)
            } else if (getOrientation() == VERTICAL) {
                vp.extentSize.height
            } else {
                vp.extentSize.width
            }
        }
    }
}