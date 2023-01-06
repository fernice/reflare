/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render.merlin

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Attachment
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.flare.style.properties.longhand.background.Origin
import org.fernice.flare.style.properties.stylestruct.Border
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.BackgroundSize
import org.fernice.flare.style.value.computed.BorderStyle
import org.fernice.reflare.awt.alpha
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.render.BackgroundLayer
import org.fernice.reflare.render.BackgroundLayers
import org.fernice.reflare.render.Renderer
import org.fernice.reflare.render.computeBackgroundLayers
import org.fernice.reflare.render.height
import org.fernice.reflare.render.use
import org.fernice.reflare.render.width
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TBounds
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.minus
import org.fernice.reflare.resource.toTColors
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.withResourceContext
import org.fernice.reflare.shape.BackgroundShape
import org.fernice.reflare.shape.BorderShape
import org.fernice.reflare.awt.toAWTColor
import org.fernice.reflare.util.VacatingRef
import org.fernice.std.Second
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.SwingUtilities

class MerlinRenderer(componentReference: VacatingRef<Component>, private val element: AWTComponentElement) : Renderer {

    private val component by componentReference

    override fun renderBackground(g: Graphics, style: ComputedValues?) = withResourceContext {
        if (style == null) {
            val bounds = component.getBounds(ResourceContext.Rectangle())

            g.color = Color.WHITE
            g.fillRect(0, 0, bounds.width, bounds.height)
            return
        }

        g.use { g2 -> paintBackground(g2, style) }
    }

    override fun renderBorder(g: Graphics, style: ComputedValues?) = withResourceContext {
        if (style == null) {
            val bounds = component.getBounds(ResourceContext.Rectangle())

            g.color = Color.LIGHT_GRAY
            g.drawRect(0, 0, bounds.width, bounds.height)
            return
        }

        g.use { g2 -> paintBorder(g2, style) }
    }

    // ***************************** Caching ***************************** //

    private var backgroundShapes = arrayOfNulls<BackgroundShape?>(3)
    private var backgroundLayers: BackgroundLayers? = null

    private var borderShape: BorderShape? = null

    fun invalidateShapes() {
        val backgroundShapes = backgroundShapes
        for (index in backgroundShapes.indices) {
            backgroundShapes[index] = null
        }
        borderShape = null
    }

    fun invalidateLayers() {
        backgroundLayers = null
    }

    private val validatedSize = Dimension()

    private fun validate() {
        val validatedSize = validatedSize
        val width = validatedSize.width
        val height = validatedSize.height
        component.getSize(validatedSize)

        if (width != validatedSize.width || height != validatedSize.height) {
            invalidateShapes()
            invalidateLayers()
        }
    }

    private fun getBackgroundShape(computedValues: ComputedValues, clip: Clip): BackgroundShape {
        val backgroundShapes = backgroundShapes
        var backgroundShape = backgroundShapes[clip.ordinal]
        if (backgroundShape == null) {
            backgroundShape = BackgroundShape.computeBackgroundShape(computedValues, component, clip)
            backgroundShapes[clip.ordinal] = backgroundShape
        }
        return backgroundShape
    }

    private fun getBackgroundLayers(computedValues: ComputedValues): BackgroundLayers {
        var backgroundLayers = this.backgroundLayers
        if (backgroundLayers == null) {
            backgroundLayers = BackgroundLayers.computeBackgroundLayers(component, computedValues)
            this.backgroundLayers = backgroundLayers
        }
        return backgroundLayers
    }

    private fun getBorderShape(computedValues: ComputedValues): BorderShape {
        var borderShape = this.borderShape
        if (borderShape == null) {
            borderShape = BorderShape.computeBorderShape(computedValues, component)
            this.borderShape = borderShape
        }
        return borderShape
    }

    // ***************************** Background ***************************** //

    fun paintBackground(g2: Graphics2D, computedValues: ComputedValues) {
        validate()

        val background = computedValues.background
        if (background.color.alpha == 0f && background.image.none { it is Second }) return

        val backgroundShape = getBackgroundShape(computedValues, computedValues.background.clip)
        val backgroundLayers = getBackgroundLayers(computedValues)

        g2.color = backgroundLayers.color
        g2.fill(backgroundShape.shape)

        if (backgroundLayers.layers.isNotEmpty()) {
            val bounds = component.getBounds(ResourceContext.Rectangle())

            val margin = computedValues.margin.toTInsets(bounds)
            val borderInsets = computedValues.border.toTInsets()
            val padding = computedValues.padding.toTInsets(bounds)

            for (layer in backgroundLayers.layers) {
                when (layer) {
                    is BackgroundLayer.Image -> {
                        paintBackgroundImage(g2, component, computedValues, layer, backgroundLayers.clip, layer.origin, padding, borderInsets, margin)
                    }

                    is BackgroundLayer.Gradient -> {
                        g2.paint = layer.gradient
                        g2.fill(backgroundShape.shape)
                    }
                }
            }
        }
    }

    private fun paintBackgroundImage(
        g2: Graphics2D,
        component: Component,
        computedValues: ComputedValues,
        layer: BackgroundLayer.Image,
        backgroundClip: Clip,
        backgroundOrigin: Origin,
        padding: TInsets,
        borderInsets: TInsets,
        margin: TInsets,
    ) {
        if (!layer.image.isDone || layer.image.isCompletedExceptionally) return

        val image = layer.image.get()

        val componentBounds = when (layer.attachment) {
            is Attachment.Scroll,
            is Attachment.Local,
            -> {
                val bounds = TBounds.fromDimension(component.getSize(ResourceContext.Dimension()))

                when (backgroundOrigin) {
                    is Origin.BorderBox -> bounds - margin
                    is Origin.PaddingBox -> bounds - margin - borderInsets
                    is Origin.ContentBox -> bounds - margin - borderInsets - padding
                }
            }

            is Attachment.Fixed -> {
                val root = SwingUtilities.getRoot(component)

                val rootLocation = root.locationOnScreen
                val componentLocation = component.locationOnScreen

                val size = root.getSize(ResourceContext.Dimension())

                ResourceContext.TBounds(
                    (rootLocation.x - componentLocation.x).toFloat(),
                    (rootLocation.y - componentLocation.y).toFloat(),
                    size.width.toFloat(),
                    size.height.toFloat()
                )
            }
        }

        val backgroundSize = layer.size

        val (width, height) = when (backgroundSize) {
            is BackgroundSize.Cover -> {
                val componentRatio = componentBounds.height / componentBounds.width
                val imageRatio = image.height.toFloat() / image.width.toFloat()

                if (componentRatio < imageRatio) {
                    Pair(
                        componentBounds.width,
                        (image.height * (componentBounds.width / image.width))
                    )
                } else {
                    Pair(
                        (image.width * (componentBounds.height / image.height)),
                        componentBounds.height
                    )
                }
            }

            is BackgroundSize.Contain -> {
                val componentRatio = componentBounds.height / componentBounds.width
                val imageRatio = image.height.toFloat() / image.width.toFloat()

                if (componentRatio < imageRatio) {
                    Pair(
                        (image.width * (componentBounds.height / image.height)),
                        componentBounds.height
                    )
                } else {
                    Pair(
                        componentBounds.width,
                        (image.height * (componentBounds.width / image.width))
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
        val bounds = component.getBounds(ResourceContext.Rectangle())
        bounds.x = 0
        bounds.y = 0

        g2.clip = getBackgroundShape(computedValues, backgroundClip).shape

        g2.drawImage(image, componentBounds.x.toInt() + positionX.toInt(), componentBounds.y.toInt() + positionY.toInt(), width.toInt(), height.toInt(), null)

        g2.clip = clip
    }

    // ***************************** Border ***************************** //

    private fun paintBorder(g2: Graphics2D, computedValues: ComputedValues) {
        validate()

        val border = computedValues.border
        if (border.isNone()) {
            return
        }

        val borderWidth = border.toTInsets()
        if (borderWidth.isAllZero()) {
            return
        }

        when (val borderShape = getBorderShape(computedValues)) {
            is BorderShape.Simple -> {
                g2.color = border.topColor.toAWTColor()
                g2.fill(borderShape.shape)
            }

            is BorderShape.Complex -> {
                val borderColor = border.toTColors(computedValues.color.color)

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
        return topStyle == BorderStyle.None && rightStyle == BorderStyle.None && bottomStyle == BorderStyle.None && leftStyle == BorderStyle.None
    }

    private fun TInsets.isAllZero(): Boolean {
        return top == 0f && right == 0f && bottom == 0f && left == 0f
    }
}