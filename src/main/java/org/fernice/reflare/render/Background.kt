/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.Attachment
import org.fernice.flare.style.properties.longhand.Clip
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.ComputedUrl
import org.fernice.flare.style.value.computed.HorizontalPosition
import org.fernice.flare.style.value.computed.Image
import org.fernice.flare.style.value.computed.VerticalPosition
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.element.element
import org.fernice.reflare.element.invalidate
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.toAWTColor
import java.awt.Color
import java.awt.Component
import java.awt.MultipleGradientPaint
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.Image as AWTImage

class BackgroundLayers(
    val color: Color,
    val clip: Clip,
    val layers: List<BackgroundLayer>
) {
    companion object
}

sealed class BackgroundLayer {

    data class Image(
        val image: BufferedImage,
        val attachment: Attachment,
        val size: BackgroundSize,
        val positionX: HorizontalPosition,
        val positionY: VerticalPosition
    ) : BackgroundLayer() {
        companion object
    }

    data class Gradient(val gradient: MultipleGradientPaint) : BackgroundLayer() {
        companion object
    }
}

fun BackgroundLayers.Companion.computeBackgroundLayers(
    component: Component,
    computedValues: ComputedValues
): BackgroundLayers {
    val background = computedValues.background
    val layers: MutableList<BackgroundLayer> = mutableListOf()

    loop@
    for (layer in background.reversedImageLayerIterator()) {
        val image = layer.image
        when (image) {
            is Image.Url -> {
                val url = when (image.url) {
                    is ComputedUrl.Valid -> (image.url as ComputedUrl.Valid).url
                    is ComputedUrl.Invalid -> continue@loop
                }

                val future = ImageCache.image(url) {
                    component.element::backgroundLayers.invalidate()
                    component.repaint()
                }

                if (future.isDone && !future.isCompletedExceptionally) {
                    layers.add(
                        BackgroundLayer.Image(future.get(), layer.attachment, layer.size, layer.positionX, layer.positionY)
                    )
                }
            }
            is Image.Gradient -> {
                layers.add(
                    BackgroundLayer.Gradient.computeGradient(image.gradient, component.size)
                )
            }
        }
    }

    val color = background.color.toAWTColor()

    return BackgroundLayers(color, background.clip, layers)
}

