/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.element.StyleTreeElementLookup
import org.fernice.reflare.element.ToolTipElement
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JToolTip
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicToolTipUI

@Suppress("ACCIDENTAL_OVERRIDE")
class FlareToolTipUI(tooltip: JToolTip) : BasicToolTipUI(), FlareUI {

    override val element = ToolTipElement(tooltip)

    override fun installDefaults(component: JComponent) {
        super.installDefaults(component)

        installDefaultProperties(component)

        StyleTreeElementLookup.registerElement(component, this)
    }

    override fun uninstallDefaults(component: JComponent) {
        super.uninstallDefaults(component)

        StyleTreeElementLookup.deregisterElement(component)
    }

    override fun getMinimumSize(c: JComponent): Dimension {
        applyStyle()
        return super.getMinimumSize(c)
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        applyStyle()
        return super.getPreferredSize(c)
    }

    override fun getMaximumSize(c: JComponent): Dimension {
        applyStyle()
        return super.getMaximumSize(c)
    }

    private fun applyStyle() {
        element.applyCSS(origin = "tooltip:sizing")
    }

    override fun paint(g: Graphics, component: JComponent) {
        paintBackground(component, g)

        super.paint(g, component)
    }

    private fun paintBackground(component: JComponent, g: Graphics) {
        element.paintBackground(component, g)
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }

    companion object {

        @JvmStatic
        fun createUI(component: JComponent): ComponentUI {
            return FlareToolTipUI(component as JToolTip)
        }
    }
}