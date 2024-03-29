/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.element.EditorPaneElement
import org.fernice.reflare.element.StyleTreeElementLookup
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicEditorPaneUI

@Suppress("ACCIDENTAL_OVERRIDE")
open class FlareEditorPaneUI(editorPane: JEditorPane) : BasicEditorPaneUI(), FlareUI {

    final override val element = createElement(editorPane)

    protected open fun createElement(editorPane: JEditorPane): EditorPaneElement = EditorPaneElement(editorPane)

    override fun installDefaults() {
        super.installDefaults()

        installDefaultProperties(component)

        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)

        StyleTreeElementLookup.registerElement(component, this)
    }

    override fun uninstallDefaults() {
        super.uninstallDefaults()

        StyleTreeElementLookup.deregisterElement(component)
    }

    override fun getMinimumSize(c: JComponent): Dimension {
        element.pulseForComputation()
        return super.getMinimumSize(c)
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        element.pulseForComputation()
        return super.getPreferredSize(c)
    }

    override fun getMaximumSize(c: JComponent): Dimension {
        element.pulseForComputation()
        return super.getMaximumSize(c)
    }

    override fun paintSafely(graphics: Graphics) {
        element.paintBackground(graphics)

        super.paintSafely(graphics)
    }

    override fun paintBackground(g: Graphics?) {
    }

    override fun paintBorder(c: Component, g: Graphics) {
        element.paintBorder(g)
    }

    companion object {

        @JvmStatic
        fun createUI(component: JComponent): ComponentUI {
            return FlareEditorPaneUI(component as JEditorPane)
        }
    }
}