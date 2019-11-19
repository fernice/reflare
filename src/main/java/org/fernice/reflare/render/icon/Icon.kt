/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
@file:JvmName("IconHelper")

package org.fernice.reflare.render.icon

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.stylestruct.Background
import org.fernice.flare.style.properties.stylestruct.Color
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.ComputedUrl
import org.fernice.flare.style.value.computed.Fill
import org.fernice.flare.style.value.computed.Image
import org.fernice.flare.url.Url
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.internal.ImageHelper
import org.fernice.reflare.render.filter.createTintedImage
import org.fernice.reflare.render.height
import org.fernice.reflare.render.width
import org.fernice.reflare.toAWTColor
import java.awt.Component
import java.awt.Graphics
import javax.swing.AbstractButton
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.plaf.UIResource
import java.awt.Image as AWTImage

class FlareImageIcon(val image: AWTImage) : Icon, UIResource {

    override fun getIconWidth(): Int = image.getWidth(null)
    override fun getIconHeight(): Int = image.getHeight(null)

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        // The standard implementation namely ImageIcon uses the given
        // component as the image observe, but this causes severe
        // problems when rendering under JDK 8 in combination with
        // internal multi resolution images.
        g.drawImage(image, x, y, null)
    }
}

fun AbstractButton.setIcon(style: ColorAndBackground, completion: () -> Unit = {}) {
    val (icon, loading) = IconPseudoElementHelper.getIcon(style, completion)

    // prevent manually set icons from being overridden if no
    // icon is specified via css
    if (!loading && (icon != null || this.icon is UIResource)) {
        this.icon = icon
    }
}

fun JLabel.setIcon(style: ColorAndBackground, completion: () -> Unit = {}) {
    val (icon, loading) = IconPseudoElementHelper.getIcon(style, completion)

    // prevent manually set icons from being overridden if no
    // icon is specified via css
    if (!loading && (icon != null || this.icon is UIResource)) {
        this.icon = icon
    }
}

fun getIcon(resource: String): Icon {
    val image = ImageHelper.getMultiResolutionImageResource(resource)

    return FlareImageIcon(image)
}

data class ColorAndBackground(val color: Color, val background: Background) {

    companion object {

        val Initial by lazy { ColorAndBackground(Color.Initial, Background.Initial) }

        fun from(style: ComputedValues): ColorAndBackground {
            return ColorAndBackground(style.color, style.background)
        }
    }
}

object IconPseudoElementHelper {

    fun getIcon(style: ColorAndBackground, completion: () -> Unit): Pair<Icon?, Boolean> {
        val (url, size) = selectImage(style.background) ?: return null to false

        val iconFuture = ImageCache.image(url) { completion() }

        if (!iconFuture.isDone) {
            return null to true
        }

        if (iconFuture.isDone && !iconFuture.isCompletedExceptionally) {
            val icon = iconFuture.get()

            val imageSize = when (size) {
                is BackgroundSize.Contain,
                is BackgroundSize.Cover -> null
                is BackgroundSize.Explicit -> {
                    size.width.toPixelLength(Au.fromPx(20), Au.fromPx(icon.width.toFloat())) to
                            size.height.toPixelLength(Au.fromPx(20), Au.fromPx(icon.height.toFloat()))

                }
            }

            val processedIcon = when (val fill = style.color.fill) {
                is Fill.None -> icon
                is Fill.Color -> icon.createTintedImage(fill.rgba.toAWTColor())
            }

            return if (imageSize != null) {
                val (width, height) = imageSize

                FlareImageIcon(ImageHelper.getScaledInstance(processedIcon, width.px().toInt(), height.px().toInt(), AWTImage.SCALE_SMOOTH)) to false
            } else {
                FlareImageIcon(processedIcon) to false
            }
        }

        return null to false
    }

    private fun selectImage(background: Background): Pair<Url, BackgroundSize>? {
        loop@
        for (layer in background.imageLayerIterator()) {
            val image = layer.image as? Image.Url ?: continue@loop

            val url = when (val unvalidatedUrl = image.url) {
                is ComputedUrl.Valid -> unvalidatedUrl.url
                is ComputedUrl.Invalid -> continue@loop
            }

            return url to layer.size
        }

        return null
    }
}