/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render.filter

import java.awt.Color
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import java.awt.image.RGBImageFilter

private const val RGB_MASK = 0x00ffffff
private const val ALPHA_MASK = 0xff shl 24

class TintFilter(private val color: Color) : RGBImageFilter() {

    init {
        canFilterIndexColorModel = true
    }

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        return (color.rgb and RGB_MASK) or (rgb and ALPHA_MASK)
    }
}

fun Image.createTintedImage(color: Color): Image {
    val filter = TintFilter(color)
    val prod = FilteredImageSource(this.source, filter)
    return Toolkit.getDefaultToolkit().createImage(prod)
}