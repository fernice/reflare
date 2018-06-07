package de.krall.reflare.render

import de.krall.flare.std.None
import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.style.ComputedValues
import de.krall.reflare.toAWTColor
import java.awt.Color
import java.awt.Component
import java.awt.Graphics

fun renderBackground(g: Graphics, component: Component, style: Option<ComputedValues>) {
    val values = when (style) {
        is Some -> style.value
        is None -> {
            val bounds = component.bounds

            g.color = Color.RED
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)
            return
        }
    }

    g.color = values.background.color.toAWTColor()

    val bounds = component.bounds
    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)
}

fun renderBorder(g: Graphics, component: Component, style: Option<ComputedValues>) {

}