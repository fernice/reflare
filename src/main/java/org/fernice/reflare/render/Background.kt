/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Attachment
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.flare.style.properties.longhand.background.Origin
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.ComputedUrl
import org.fernice.flare.style.value.computed.HorizontalPosition
import org.fernice.flare.style.value.computed.Image
import org.fernice.flare.style.value.computed.VerticalPosition
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.toAWTColor
import java.awt.Color
import java.awt.Component
import java.awt.MultipleGradientPaint
import java.util.concurrent.CompletableFuture
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
        val image: CompletableFuture<out AWTImage>,
        val attachment: Attachment,
        val size: BackgroundSize,
        val positionX: HorizontalPosition,
        val positionY: VerticalPosition,
        val origin: Origin
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
        when (val image = layer.image) {
            is Image.Url -> {
                val url = when (image.url) {
                    is ComputedUrl.Valid -> (image.url as ComputedUrl.Valid).url
                    is ComputedUrl.Invalid -> continue@loop
                }

                val future = ImageCache.fetch(url) {
                    component.repaint()
                }

                layers.add(
                    BackgroundLayer.Image(future, layer.attachment, layer.size, layer.positionX, layer.positionY, layer.origin)
                )
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

