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
import java.awt.Window
import java.util.WeakHashMap
import javax.swing.CellRendererPane
import javax.swing.JLayeredPane
import javax.swing.JRootPane

object StyleTreeElementLookup {

    @JvmStatic
    private val elements: MutableMap<Component, FlareUI> = WeakHashMap()

    @JvmStatic
    fun registerElement(component: Component, ui: FlareUI) {
        elements[component] = ui
    }

    @JvmStatic
    fun deregisterElement(component: Component) {
        elements.remove(component)
    }

    @JvmStatic
    internal fun ensureElement(component: Component): FlareUI {
        require(component !is Window) { "windows cannot be elements" }
        return elements.getOrPut(component) {
            val new = when (component) {
                is CellRendererPane -> CellRendererPaneElement(component)
                is org.fernice.reflare.render.CellRendererPane -> ModernCellRendererPaneElement(component)
                is JLayeredPane -> LayeredPaneElement(component)
                is JRootPane -> RootPaneElement(component)
                is Container -> AWTContainerElement(component, artificial = component::class != Container::class)
                else -> throw IllegalArgumentException("unsupported component ${component.javaClass.name}")
            }

            ComponentUIWrapper(new)
        }
    }
}

private class ComponentUIWrapper(override val element: AWTComponentElement) : FlareUI {

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
    }
}

@Deprecated(message = "Element is now a extension attribute", level = DeprecationLevel.HIDDEN, replaceWith = ReplaceWith("element"))
fun Component.into(): AWTComponentElement {
    return StyleTreeElementLookup.ensureElement(this).element
}

val Component.element: AWTComponentElement
    get() = StyleTreeElementLookup.ensureElement(this).element

val Component.ui: FlareUI
    get() = StyleTreeElementLookup.ensureElement(this)