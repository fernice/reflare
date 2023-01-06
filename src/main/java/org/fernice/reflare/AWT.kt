package org.fernice.reflare

import org.fernice.flare.cssparser.RGBA
import org.fernice.flare.style.value.computed.Color
import org.fernice.flare.style.value.computed.RGBAColor
import java.awt.Color as AWTColor

val Color.alpha: Int
    get() = when (this) {
        is Color.RGBA -> this.rgba.alpha
        is Color.CurrentColor -> 255
    }

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
        is Color.CurrentColor -> error("current color is not supported")
        is Color.RGBA -> {
            val rgba = this.rgba

            AWTColor(rgba.red, rgba.green, rgba.blue, rgba.alpha)
        }
    }
}

fun Color.toOpaqueAWTColor(): AWTColor {
    return when (this) {
        is Color.CurrentColor -> error("current color is not supported")
        is Color.RGBA -> {
            val rgba = this.rgba

            AWTColor(opaque(rgba.red, rgba.alpha), opaque(rgba.green, rgba.alpha), opaque(rgba.blue, rgba.alpha), 255)
        }
    }
}

private fun opaque(c: Int, a: Int): Int {
    return (opaque(c / 255f, a / 255f) * 255f).toInt()
}

private fun opaque(c: Float, a: Float): Float {
    return (c * a) + (1f - a)
}

fun RGBAColor.toAWTColor(): AWTColor {
    return AWTColor(this.red, this.green, this.blue, this.alpha)
}