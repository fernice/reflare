/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("StyleTreeHelper")

package org.fernice.reflare.element

import org.fernice.reflare.ui.FlareUI
import java.awt.Component
import java.awt.Container
import java.awt.Graphics
import java.util.WeakHashMap
import javax.swing.CellRendererPane
import javax.swing.JLayeredPane

object StyleTreeElementLookup {

    private val elements: MutableMap<Component, FlareUI> = WeakHashMap()

    @JvmStatic
    fun registerElement(component: Component, ui: FlareUI) {
        elements[component] = ui
    }

    @JvmStatic
    fun deregisterElement(component: Component) {
        elements.remove(component)
    }

    internal fun ensureElement(component: Component): FlareUI {
        val element = elements[component]

        return if (element == null) {
            val new = when (component) {
                is CellRendererPane -> CellRendererPaneElement(component)
                is org.fernice.reflare.render.CellRendererPane -> ModernCellRendererPaneElement(component)
                is JLayeredPane -> LayeredPaneElement(component)
                is Container -> AWTContainerElement(component)
                else -> throw IllegalArgumentException("unsupported component ${component.javaClass.name}")
            }

            val ui = ComponentUIWrapper(new)

            elements[component] = ui

            ui
        } else {
            element
        }
    }
}

private class ComponentUIWrapper(
    override val element: AWTComponentElement
) : FlareUI {

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
    }
}

@Deprecated(message = "Element is now a extension attribute", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("element"))
fun Component.into(): AWTComponentElement {
    return StyleTreeElementLookup.ensureElement(this).element
}

val Component.element: AWTComponentElement
    get() = StyleTreeElementLookup.ensureElement(this).element

val Component.ui: FlareUI
    get() = StyleTreeElementLookup.ensureElement(this)