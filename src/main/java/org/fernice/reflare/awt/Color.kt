/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.awt

import org.fernice.flare.cssparser.RGBA
import org.fernice.flare.style.value.computed.Color
import org.fernice.flare.style.value.computed.RGBAColor
import fernice.reflare.awt.FlareColor as AWTColor

val Color.alpha: Float
    get() = when (this) {
        is Color.RGBA -> this.rgba.alpha
        is Color.CurrentColor -> 1f
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

            val r = opaque(rgba.red, rgba.alpha)
            val g = opaque(rgba.green, rgba.alpha)
            val b = opaque(rgba.blue, rgba.alpha)

            AWTColor(r, g, b, 1f)
        }
    }
}

private fun opaque(c: Float, a: Float): Float {
    return (c * a) + (1f - a)
}

fun RGBAColor.toAWTColor(): AWTColor {
    return AWTColor(this.red, this.green, this.blue, this.alpha)
}