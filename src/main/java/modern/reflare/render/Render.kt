package modern.reflare.render

import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.style.ComputedValues
import de.krall.flare.style.properties.longhand.Attachment
import de.krall.flare.style.properties.stylestruct.ImageLayer
import de.krall.flare.style.value.computed.Au
import de.krall.flare.style.value.computed.BackgroundSize
import de.krall.flare.style.value.computed.ComputedUrl
import de.krall.flare.style.value.computed.Gradient
import de.krall.flare.style.value.computed.Image
import modern.reflare.cache.ImageCache
import modern.reflare.element.AWTComponentElement
import modern.reflare.geom.toColors
import modern.reflare.geom.toInsets
import modern.reflare.geom.Insets
import modern.reflare.shape.BorderShape
import modern.reflare.toAWTColor
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets as AWTInsets
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities
import java.awt.Color as AWTColor

fun renderBackground(g: Graphics, component: Component, element: AWTComponentElement, style: Option<ComputedValues>) {
    if (style is Some) {
        paintBackground(g, component, element, style.value, element.cache)
    } else {
        val bounds = component.bounds

        g.color = AWTColor.RED
        g.fillRect(0, 0, bounds.width, bounds.height)
    }
}

fun renderBorder(g: Graphics, component: Component, element: AWTComponentElement, style: Option<ComputedValues>) {
    if (style is Some) {
        paintBorder(g, component, style.value, element.cache)
    } else {
        val bounds = component.bounds

        g.color = AWTColor.BLACK
        g.drawRect(0, 0, bounds.width, bounds.height)
    }
}

fun getGraphics(g: Graphics): Graphics2D {
    val g2 = g as Graphics2D
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    return g2
}


fun paintBackground(g: Graphics,
                    component: Component,
                    element: AWTComponentElement,
                    computedValues: ComputedValues,
                    renderCache: Cache) {
    val g2 = getGraphics(g)

    val background = computedValues.background

    g2.color = background.color.toAWTColor()
    g2.fill(renderCache.backgroundShape.shape)

    loop@
    for (layer in background.reversedImageLayerIterator()) {
        val image = layer.image
        when (image) {
            is Image.Url -> {
                val url = when (image.url) {
                    is ComputedUrl.Valid -> (image.url as ComputedUrl.Valid).url
                    is ComputedUrl.Invalid -> continue@loop
                }

                val future = ImageCache.image(url)

                if (future.isDone || future.isCompletedExceptionally) {
                    paintBackgroundImage(
                            g2,
                            component,
                            future.get(),
                            layer,
                            element.padding,
                            element.margin
                    )
                }
            }
            is Image.Gradient -> {
                paintBackgroundGradient(g2, image.gradient)
            }
        }
    }
}

fun paintBackgroundImage(g2: Graphics2D,
                         component: Component,
                         image: BufferedImage,
                         layer: ImageLayer,
                         padding: Insets,
                         margin: Insets) {

    val componentBounds = when (layer.attachment) {
        is Attachment.Scroll,
        is Attachment.Local -> {
            component.size
        }
        is Attachment.Fixed -> {
            SwingUtilities.getRoot(component).size
        }
    }

    val backgroundSize = layer.size

    val (width, height) = when (backgroundSize) {
        is BackgroundSize.Cover -> {
            val max = Math.max(componentBounds.width, componentBounds.height).toDouble()

            if (image.height < image.width) {
                Pair(
                        (image.width * (max / image.height)).toFloat(),
                        max.toFloat()
                )
            } else {
                Pair(
                        max.toFloat(),
                        (image.height * (max / image.width)).toFloat()
                )
            }
        }
        is BackgroundSize.Contain -> {
            val min = Math.min(componentBounds.width, componentBounds.height).toDouble()

            if (image.height < image.width) {
                Pair(
                        min.toFloat(),
                        (image.width * (min / image.height)).toFloat()
                )
            } else {
                Pair(
                        (image.height * (min / image.width)).toFloat(),
                        min.toFloat()
                )
            }
        }
        is BackgroundSize.Explicit -> {
            Pair(
                    backgroundSize.width.toPixelLength(Au.fromPx(componentBounds.width)).px(),
                    backgroundSize.height.toPixelLength(Au.fromPx(componentBounds.height)).px()
            )
        }
    }

}

fun paintBackgroundGradient(g2: Graphics2D, imageGradient: Gradient) {
    throw UnsupportedOperationException()
}

fun paintBorder(g: Graphics, component: Component, computedValues: ComputedValues, renderCache: Cache) {
    val border = computedValues.border

    val borderWidth = border.toInsets()

    if (borderWidth.isZero()) {
        return
    }

    val g2 = getGraphics(g)
    val borderColor = border.toColors(computedValues.color.color)

    val borderShape = renderCache.borderShape

    when (borderShape) {
        is BorderShape.Simple -> {
            g2.color = borderColor.top
            g2.fill(borderShape.shape)
        }
        is BorderShape.Complex -> {
            g2.color = borderColor.top
            g2.fill(borderShape.top)

            g2.color = borderColor.right
            g2.fill(borderShape.right)

            g2.color = borderColor.bottom
            g2.fill(borderShape.bottom)

            g2.color = borderColor.left
            g2.fill(borderShape.left)
        }
    }
}