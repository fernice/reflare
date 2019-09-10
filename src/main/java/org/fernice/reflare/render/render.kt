package org.fernice.reflare.render

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Attachment
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.flare.style.properties.stylestruct.Border
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.Style
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.Point
import org.fernice.reflare.geom.toColors
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.shape.BorderShape
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Rectangle
import javax.swing.SwingUtilities
import java.awt.Color as AWTColor

fun renderBackground(g: Graphics, component: Component, element: AWTComponentElement, style: ComputedValues?) {
    if (style != null) {
        g.use { g2 ->
            paintBackground(g2, component, element, style)
        }
    } else {
        val bounds = component.bounds

        g.color = AWTColor.WHITE
        g.fillRect(0, 0, bounds.width, bounds.height)
    }
}

fun renderBorder(g: Graphics, component: Component, element: AWTComponentElement, style: ComputedValues?) {
    if (style != null) {
        g.use { g2 ->
            paintBorder(g2, component, element, style)
        }
    } else {
        val bounds = component.bounds

        g.color = AWTColor.LIGHT_GRAY
        g.drawRect(0, 0, bounds.width, bounds.height)
    }
}

fun paintBackground(
    g2: Graphics2D,
    component: Component,
    element: AWTComponentElement,
    computedValues: ComputedValues
) {
    val borderInsets = computedValues.border.toInsets()
    val backgroundShape = element.backgroundShape
    val backgroundLayers = element.backgroundLayers

    g2.color = backgroundLayers.color
    g2.fill(backgroundShape.shape)

    for (layer in backgroundLayers.layers) {
        when (layer) {
            is BackgroundLayer.Image -> {
                paintBackgroundImage(g2, component, layer, backgroundLayers.clip, element.padding, borderInsets, element.margin)
            }
            is BackgroundLayer.Gradient -> {
                g2.paint = layer.gradient
                g2.fill(backgroundShape.shape)
            }
        }
    }
}

private fun paintBackgroundImage(
    g2: Graphics2D,
    component: Component,
    layer: BackgroundLayer.Image,
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
    val image = layer.image

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
    val positionY = layer.positionY.toPixelLength(Au.fromPx(componentBounds.height - height)).px()

    val clip = g2.clip
    val bounds = component.bounds
    bounds.location = java.awt.Point(0, 0)

    g2.clip(bounds.reduce(backgroundClip, padding, borderInsets, margin))

    g2.drawImage(layer.image, correction.x.toInt() + positionX.toInt(), correction.y.toInt() + positionY.toInt(), width.toInt(), height.toInt(), null)

    g2.clip = clip
}

fun paintBorder(g2: Graphics2D, component: Component, element: AWTComponentElement, computedValues: ComputedValues) {
    val border = computedValues.border

    val borderWidth = border.toInsets()

    if (borderWidth.isZero() || border.isNone()) {
        return
    }

    val borderColor = border.toColors(computedValues.color.color)

    when (val borderShape = element.borderShape) {
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

private fun Border.isNone(): Boolean {
    return topStyle == Style.None && rightStyle == Style.None && bottomStyle == Style.None && leftStyle == Style.None
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

internal val Image.height: Int
    get() = this.getHeight(null)


internal val Image.width: Int
    get() = this.getWidth(null)