/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("StyledImageIconHelper")

package fernice.reflare

import org.fernice.flare.style.value.computed.Fill
import org.fernice.flare.url.Url
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.element.element
import org.fernice.reflare.render.filter.createTintedImage
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.util.VacatingRef
import org.fernice.reflare.util.weakReferenceHashMap
import java.awt.Component
import java.awt.Graphics
import java.awt.Image
import javax.swing.Icon

interface ImageIcon : Icon {

    val image: Image

    companion object {
        @JvmStatic
        fun getImage(icon: Icon): Image = findImage(icon) ?: error("cannot retrieve image from icon: $icon")

        @JvmStatic
        fun findImage(icon: Icon): Image? {
            return when (icon) {
                is ImageIcon -> icon.image
                is javax.swing.ImageIcon -> icon.image
                else -> null
            }
        }
    }
}

interface StyledIcon : Icon

open class StyledImageIcon private constructor(internal val imageProvider: Lazy<Image>) : StyledIcon {

    private val image by imageProvider

    private var styledImageProvider: StyledImageProvider? = null

    override fun paintIcon(component: Component, g: Graphics, x: Int, y: Int) {
        var styledImageProvider = styledImageProvider
        if (styledImageProvider == null) {
            styledImageProvider = ComponentStyledImageProvider(component)

            this.styledImageProvider = styledImageProvider
        }

        val styleImage = styledImageProvider.getStyledImage(component)

        g.drawImage(styleImage, x, y, null)
    }

    override fun getIconHeight(): Int = image.getHeight(null)
    override fun getIconWidth(): Int = image.getWidth(null)

    private interface StyledImageProvider {

        fun getStyledImage(component: Component): Image
    }

    private inner class ComponentStyledImageProvider(componentInstance: Component) : StyledImageProvider {

        private val componentReference = VacatingRef(componentInstance)
        private val component by componentReference

        private var fill: Fill = Fill.None
        private var styledImage = image

        override fun getStyledImage(component: Component): Image {
            if (component !== this.component) {
                val styledImageProvider = MultiComponentStyledImageProvider(this.component to this)

                this@StyledImageIcon.styledImageProvider = styledImageProvider

                return styledImageProvider.getStyledImage(component)
            }

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

    private inner class MultiComponentStyledImageProvider(initial: Pair<Component, StyledImageProvider>) : StyledImageProvider {

        private val processedImages = weakReferenceHashMap(initial)

        override fun getStyledImage(component: Component): Image {
            val styledImageProvider = processedImages.computeIfAbsent(component) { ComponentStyledImageProvider(component) }

            return styledImageProvider.getStyledImage(component)
        }
    }

    internal class UIResource(imageProvider: Lazy<Image>) : StyledImageIcon(imageProvider)

    companion object {

        @JvmStatic
        fun fromResource(resource: String): StyledImageIcon {
            val imageFuture = ImageCache.fetch(Url(resource))
            val imageProvider = lazy { imageFuture.get() }
            return StyledImageIcon(imageProvider)
        }

        @JvmStatic
        fun fromUrl(url: String): StyledImageIcon {
            val imageFuture = ImageCache.fetch(Url(url))
            val imageProvider = lazy { imageFuture.get() }
            return StyledImageIcon(imageProvider)
        }

        @JvmStatic
        fun fromImage(image: Image): StyledImageIcon {
            val imageProvider = lazy { image }
            return StyledImageIcon(imageProvider)
        }
    }
}

@JvmName("asUIResource")
internal fun StyledImageIcon.asUIResource(): StyledImageIcon.UIResource {
    return StyledImageIcon.UIResource(imageProvider)
}
