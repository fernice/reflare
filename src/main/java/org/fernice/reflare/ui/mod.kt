/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("FlareUIHelper")

package org.fernice.reflare.ui

import org.fernice.reflare.Defaults
import org.fernice.reflare.element.ui
import java.awt.Component
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.LookAndFeel

private val enforceBorder = System.getProperty("reflare.enforce_border", "true")!!.toBoolean()

private val propertyChangeListener = PropertyChangeListener { event ->
    if (event.newValue !is FlareBorder) {
        val component = event.source as JComponent

        val border = when (val value = event.oldValue) {
            is FlareBorder -> value
            else -> FlareBorder(component.ui)
        }

        component.border = border
    }
}

fun FlareUI.installDefaults(component: Component) {
    if (component is JComponent) {
        LookAndFeel.installProperty(component, "opaque", false)

        component.border = FlareBorder(this)

        if (enforceBorder) {
            component.addPropertyChangeListener("border", propertyChangeListener)
        }
    }
    component.font = Defaults.FONT_SERIF
    component.background = Defaults.COLOR_TRANSPARENT
    component.foreground = Defaults.COLOR_BLACK
}
