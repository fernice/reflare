/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import fernice.reflare.FlareLookAndFeel
import org.fernice.reflare.element.EditorPaneElement
import org.fernice.reflare.element.deregisterElement
import org.fernice.reflare.element.registerElement
import java.awt.Component
import java.awt.Graphics
import javax.swing.JEditorPane
import javax.swing.plaf.basic.BasicEditorPaneUI

class FlareEditorPaneUI(editorPane: JEditorPane) : BasicEditorPaneUI(), FlareUI {

    override val element = EditorPaneElement(editorPane)

    override fun installDefaults() {
        super.installDefaults()

        component.isOpaque = false
        component.border = FlareBorder(this)
        component.font = FlareLookAndFeel.DEFAULT_FONT
        component.addPropertyChangeListener("enabled") { component.repaint() }
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)

        registerElement(component, element)
    }

    override fun uninstallDefaults() {
        super.uninstallDefaults()

        deregisterElement(component)
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