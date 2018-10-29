package org.fernice.reflare.render

import fernice.std.Option
import fernice.std.Some
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.Attachment
import org.fernice.flare.style.properties.longhand.Clip
import org.fernice.flare.style.properties.stylestruct.ImageLayer
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.ComputedUrl
import org.fernice.flare.style.value.computed.Gradient
import org.fernice.flare.style.value.computed.Image
import org.fernice.reflare.cache.ImageCache
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.Point
import org.fernice.reflare.geom.toColors
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.shape.BorderShape
import org.fernice.reflare.toAWTColor
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities
import java.awt.Color as AWTColor
import java.awt.Insets as AWTInsets

fun renderBackground(g: Graphics, component: Component, element: AWTComponentElement, style: Option<ComputedValues>) {
    if (style is Some) {
        g.use { g2 ->
            paintBackground(g2, component, element, style.value, element.cache)
        }
    } else {
        val bounds = component.bounds

        g.color = AWTColor.WHITE
        g.fillRect(0, 0, bounds.width, bounds.height)
    }
}

fun renderBorder(g: Graphics, component: Component, element: AWTComponentElement, style: Option<ComputedValues>) {
    if (style is Some) {
        g.use { g2 ->
            paintBorder(g2, component, style.value, element.cache)
        }
    } else {
        val bounds = component.bounds

        g.color = AWTColor.LIGHT_GRAY
        g.drawRect(0, 0, bounds.width, bounds.height)
    }
}

fun Graphics.use(renderer: (Graphics2D) -> Unit) {
    val g2 = this.create() as Graphics2D
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    try {
        renderer(g2)
    } finally {
        g2.dispose()
    }
}

fun paintBackground(
    g2: Graphics2D,
    component: Component,
    element: AWTComponentElement,
    computedValues: ComputedValues,
    renderCache: Cache
) {
    val background = computedValues.background

    g2.color = background.color.toAWTColor()
    g2.fill(renderCache.backgroundShape.shape)

    val borderInsets = computedValues.border.toInsets()

    val clip = background.clip

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
                    component.repaint()
                }

                if (future.isDone || future.isCompletedExceptionally) {
                    paintBackgroundImage(
                        g2,
                        component,
                        future.get(),
                        layer,
                        clip,
                        element.padding,
                        borderInsets,
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

fun paintBackgroundImage(
    g2: Graphics2D,
    component: Component,
    image: BufferedImage,
    layer: ImageLayer,
    backgroundClip: Clip,
    padding: Insets,
    borderInsets: Insets,
    margin: Insets
) {

    val (componentBounds, correction) = when (layer.attachment) {
        is Attachment.Scroll,
        is Attachment.Local -> {
            component.size to Point.zero()
        }
        is Attachment.Fixed -> {
            val root = SwingUtilities.getRoot(component)

            val rootLocation = root.locationOnScreen
            val componentLocation = component.locationOnScreen

            root.size to Point((rootLocation.x - componentLocation.x).toFloat(), (rootLocation.y - componentLocation.y).toFloat())
        }
    }

    val backgroundSize = layer.size

    val (width, height) = when (backgroundSize) {
        is BackgroundSize.Cover -> {
            val componentRatio = componentBounds.height.toFloat() / componentBounds.width.toFloat()
            val imageRatio = image.height.toFloat() / image.width.toFloat()

            if (componentRatio < imageRatio) {
                Pair(
                    componentBounds.width.toFloat(),
                    (image.height * (componentBounds.width.toFloat() / image.width))
                )
            } else {
                Pair(
                    (image.width * (componentBounds.height.toFloat() / image.height)),
                    componentBounds.height.toFloat()
                )
            }
        }
        is BackgroundSize.Contain -> {
            val componentRatio = componentBounds.height.toFloat() / componentBounds.width.toFloat()
            val imageRatio = image.height.toFloat() / image.width.toFloat()

            if (componentRatio < imageRatio) {
                Pair(
                    (image.width * (componentBounds.height.toFloat() / image.height)),
                    componentBounds.height.toFloat()
                )
            } else {
                Pair(
                    componentBounds.width.toFloat(),
                    (image.height * (componentBounds.width.toFloat() / image.width))
                )
            }
        }
        is BackgroundSize.Explicit -> {
            Pair(
                backgroundSize.width.toPixelLength(Au.fromPx(componentBounds.width), Au.fromPx(image.width)).px(),
                backgroundSize.height.toPixelLength(Au.fromPx(componentBounds.height), Au.fromPx(image.height)).px()
            )
        }
    }

    val positionX = layer.positionX.toPixelLength(Au.fromPx(componentBounds.width - width)).px()
    val positionY = layer.positionX.toPixelLength(Au.fromPx(componentBounds.height - height)).px()

    val root = SwingUtilities.getRoot(component)

    val rootLocation = root.locationOnScreen
    val componentLocation = component.locationOnScreen

    val location = java.awt.Point(componentLocation.x - rootLocation.x, componentLocation.y - componentLocation.y)

    val clip = g2.clipBounds
    val bounds = component.bounds
    bounds.location = location

    g2.clip = bounds.reduce(backgroundClip, padding, borderInsets, margin)

    g2.drawImage(image, correction.x.toInt() + positionX.toInt(), correction.y.toInt() + positionY.toInt(), width.toInt(), height.toInt(), null)

    g2.clip = clip
}

fun Rectangle.reduce(clip: Clip, padding: Insets, borderInsets: Insets, margin: Insets): Rectangle {
    val rectangle = Rectangle(this)

    when (clip) {
        is Clip.BorderBox -> {
            rectangle -= margin
        }
        is Clip.PaddingBox -> {
            rectangle -= margin
            rectangle -= borderInsets
        }
        is Clip.ContentBox -> {
            rectangle -= margin
            rectangle -= borderInsets
            rectangle -= padding
        }
    }

    return rectangle
}

operator fun Rectangle.minusAssign(insets: Insets) {
    this.x += insets.left.toInt()
    this.y += insets.top.toInt()
    this.width -= (insets.left + insets.right).toInt()
    this.height -= (insets.top + insets.bottom).toInt()
}

fun paintBackgroundGradient(g2: Graphics2D, imageGradient: Gradient) {
    TODO()
}

fun paintBorder(g2: Graphics2D, component: Component, computedValues: ComputedValues, renderCache: Cache) {
    val border = computedValues.border

    val borderWidth = border.toInsets()

    if (borderWidth.isZero()) {
        return
    }

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