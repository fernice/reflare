/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.element.EditorPaneElement
import org.fernice.reflare.element.StyleTreeElementLookup
import java.awt.Component
import java.awt.Graphics
import javax.swing.JEditorPane
import javax.swing.plaf.basic.BasicEditorPaneUI

open class FlareEditorPaneUI(editorPane: JEditorPane) : BasicEditorPaneUI(), FlareUI {

    override val element = EditorPaneElement(editorPane)

    override fun installDefaults() {
        super.installDefaults()

        installDefaults(component)

        component.addPropertyChangeListener("enabled") { component.repaint() }
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)

        StyleTreeElementLookup.registerElement(component, this)
    }

    override fun uninstallDefaults() {
        super.uninstallDefaults()

        StyleTreeElementLookup.deregisterElement(component)
    }

    override fun paintSafely(graphics: Graphics) {
        element.paintBackground(component, graphics)

        super.paintSafely(graphics)
    }

    override fun paintBackground(g: Graphics?) {
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }
}