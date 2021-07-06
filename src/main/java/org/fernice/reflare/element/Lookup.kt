/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("StyleTreeHelper")

package org.fernice.reflare.element

import org.fernice.reflare.render.CellRendererPane
import org.fernice.reflare.ui.FlareUI
import org.fernice.reflare.util.ConcurrentReferenceHashMap
import org.fernice.reflare.util.fullyWeakReferenceHashMap
import org.fernice.reflare.util.weakReferenceHashMap
import java.awt.Component
import java.awt.Container
import java.awt.Graphics
import java.awt.Window
import javax.swing.JLayeredPane
import javax.swing.JRootPane

object StyleTreeElementLookup {

    @JvmStatic
    private val elements: MutableMap<Component, AWTComponentElement> = weakReferenceHashMap()

    @JvmStatic
    private val componentUis: MutableMap<Component, FlareUI> = fullyWeakReferenceHashMap()

    @JvmStatic
    fun registerElement(component: Component, ui: FlareUI) {
        elements[component] = ui.element
        componentUis[component] = ui
    }

    @JvmStatic
    fun deregisterElement(component: Component) {
        elements.remove(component)
        componentUis.remove(component)
    }

    @JvmStatic
    internal fun ensureElement(component: Component): AWTComponentElement {
        require(component !is Window) { "windows cannot be elements" }
        return elements.computeIfAbsent(component) { component ->
            when (component) {
                is CellRendererPane -> CellRendererPaneElement(component)
                is JLayeredPane -> LayeredPaneElement(component)
                is JRootPane -> RootPaneElement(component)
                is Container -> ArtificialContainerElement(component)
                else -> ArtificialComponentElement(component)
            }
        }
    }

    @JvmStatic
    internal fun ensureComponentUI(component: Component): FlareUI {
        return componentUis.computeIfAbsent(component) { component -> ComponentUIWrapper(ensureElement(component)) }
    }

    @JvmStatic
    fun removeReferences() {
        (elements as ConcurrentReferenceHashMap).removeStale()
        (componentUis as ConcurrentReferenceHashMap).removeStale()
    }
}

private class ComponentUIWrapper(override val element: AWTComponentElement) : FlareUI {

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
    }
}

val Component.element: AWTComponentElement
    get() = StyleTreeElementLookup.ensureElement(this)

val Component.ui: FlareUI
    get() = StyleTreeElementLookup.ensureComponentUI(this)