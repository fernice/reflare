package de.krall.reflare.ui

import de.krall.reflare.element.ComponentElement
import java.awt.Component
import java.awt.Graphics
import javax.swing.JComponent

private class UIKey

interface FlareUI {

    val element: ComponentElement

    fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int)
}

private const val FLARE_UI_KEY = "flare.ui"

private val uis: MutableMap<UIKey, FlareUI> = mutableMapOf()

fun hasUI(component: JComponent): Boolean {
    return component.getClientProperty(FLARE_UI_KEY) != null
}

fun lookupUI(component: JComponent): FlareUI {
    val key = component.getClientProperty(FLARE_UI_KEY)!! as UIKey

    return uis[key]!!
}

fun registerUI(component: JComponent, ui: FlareUI) {
    val key = UIKey()

    component.putClientProperty(FLARE_UI_KEY, key)
    uis[key] = ui
}

fun deregisterUI(component: JComponent) {
    val rawKey = component.getClientProperty(FLARE_UI_KEY)

    if (rawKey != null) {
        val key = rawKey as UIKey

        component.putClientProperty(FLARE_UI_KEY, null)
        uis.remove(key)
    }
}