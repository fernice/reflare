package de.krall.reflare

import de.krall.flare.style.value.computed.Color
import java.awt.Color as AWTColor

fun Color.toAWTColor(currentColor: Color): AWTColor {
    return when (this) {
        is Color.CurrentColor -> currentColor.toAWTColor()
        is Color.RGBA -> {
            val rgba = this.rgba

            AWTColor(rgba.red, rgba.green, rgba.blue, rgba.alpha)
        }
    }
}

fun Color.toAWTColor(): AWTColor {
    return when (this) {
        is Color.CurrentColor -> throw IllegalArgumentException()
        is Color.RGBA -> {
            val rgba = this.rgba

            AWTColor(rgba.red, rgba.green, rgba.blue, rgba.alpha)
        }
    }
}