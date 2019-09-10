/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render.merlin

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.stylestruct.Border
import org.fernice.flare.style.value.computed.Style
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.render.Renderer
import org.fernice.reflare.render.paintBackground
import org.fernice.reflare.render.use
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.toTColors
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.withResourceContext
import org.fernice.reflare.shape.BorderShape
import org.fernice.reflare.toAWTColor
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D

class MerlinRenderer(private val component: Component, private val element: AWTComponentElement) : Renderer {

    override fun renderBackground(g: Graphics, style: ComputedValues?) = withResourceContext {
        if (style == null) {
            val bounds = component.getBounds(ResourceContext.Rectangle())

            g.color = Color.WHITE
            g.fillRect(0, 0, bounds.width, bounds.height)
            return
        }

        g.use { g2 -> paintBackground(g2, component, element, style) }
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

    // ***************************** Background ***************************** //

    // ***************************** Border ***************************** //

    private var borderShapeCache: BorderShape? = null
    private var borderShapeHash: Int = 0

    private fun getBorderShape(computedValues: ComputedValues): BorderShape {
        val hash = borderShapeHash(computedValues)

        val borderShapeCache = borderShapeCache
        if (borderShapeCache != null && borderShapeHash == hash) {
            return borderShapeCache
        }

        val borderShape = BorderShape.computeBorderShape(computedValues, element)
        this.borderShapeCache = borderShape
        this.borderShapeHash = hash
        return borderShape
    }

    private fun borderShapeHash(computedValues: ComputedValues): Int {
        var hash = computedValues.border.hashCode() * 31
        hash += computedValues.margin.hashCode() * 31
        hash += computedValues.padding.hashCode() * 31
        hash += component.getBounds(ResourceContext.Rectangle()).hashCode()
        return hash
    }

    private fun paintBorder(g2: Graphics2D, computedValues: ComputedValues) {
        val border = computedValues.border
        if (border.isNone()) {
            return
        }

        val borderWidth = border.toTInsets()
        if (borderWidth.isAllZero()) {
            return
        }

        when (val borderShape = element.borderShape) {
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
        return topStyle == Style.None && rightStyle == Style.None && bottomStyle == Style.None && leftStyle == Style.None
    }

    private fun TInsets.isAllZero(): Boolean {
        return top == 0f && right == 0f && bottom == 0f && left == 0f
    }
}