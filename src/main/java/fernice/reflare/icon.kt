/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare

import org.fernice.flare.style.value.computed.Fill
import org.fernice.flare.url.Url
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.element.element
import org.fernice.reflare.render.filter.createTintedImage
import org.fernice.reflare.toAWTColor
import java.awt.Component
import java.awt.Graphics
import java.awt.Image
import java.util.WeakHashMap
import javax.swing.Icon

class StyledImageIcon private constructor(resource: String) : Icon {

    private val imageFuture = ImageCache.image(Url(resource)) {}
    private val image by lazy { imageFuture.get() }

    private val processedImages = WeakHashMap<Component, StyledImage>()

    override fun paintIcon(component: Component, g: Graphics, x: Int, y: Int) {
        val imageAndStyle = processedImages.computeIfAbsent(component) { StyledImage(component) }

        g.drawImage(imageAndStyle.getStyledImage(), x, y, null)
    }

    override fun getIconHeight(): Int = image.getHeight(null)
    override fun getIconWidth(): Int = image.getWidth(null)

    private inner class StyledImage(private val component: Component) {

        private var fill: Fill = Fill.None
        private var styledImage = image

        fun getStyledImage(): Image {
            val fill = component.element.getStyle()?.color?.fill ?: Fill.None
            if (fill != this.fill) {
                styledImage = when (fill) {
                    is Fill.None -> image
                    is Fill.Color -> image.createTintedImage(fill.rgba.toAWTColor())
                }
                this.fill = fill
            }

            return styledImage
        }
    }

    companion object {

        @JvmStatic
        fun fromResource(resource: String): StyledImageIcon {
            return StyledImageIcon(resource)
        }

        @JvmStatic
        fun fromUrl(url: String): StyledImageIcon {
            return StyledImageIcon(url);
        }
    }
}
