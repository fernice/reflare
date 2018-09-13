package org.fernice.reflare

import org.fernice.flare.cssparser.RGBA
import org.fernice.flare.style.value.computed.Color
import org.fernice.flare.style.value.computed.RGBAColor
import java.awt.Color as AWTColor

fun Color.toAWTColor(currentColor: RGBA): AWTColor {
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

fun RGBAColor.toAWTColor(): AWTColor {
    return AWTColor(this.red, this.green, this.blue, this.alpha)
}