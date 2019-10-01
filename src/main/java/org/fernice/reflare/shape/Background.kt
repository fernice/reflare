package org.fernice.reflare.shape

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TBounds
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.TRadii
import org.fernice.reflare.resource.minus
import org.fernice.reflare.resource.plus
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.toTRadii
import java.awt.Component
import java.awt.Dimension
import java.awt.Shape
import java.awt.geom.Path2D

internal class BackgroundShape(val shape: Shape) {

    companion object {

        internal fun from(
            clip: Clip,
            size: Dimension,
            borderWidth: TInsets,
            borderRadius: TRadii,
            margin: TInsets,
            padding: TInsets
        ): BackgroundShape {
            return BackgroundShape(
                computeBackgroundClip(clip, size, borderWidth, borderRadius, margin, padding)
            )
        }

        fun computeBackgroundShape(
            computedValues: ComputedValues,
            component: Component,
            backgroundClip: Clip = computedValues.background.clip
        ): BackgroundShape {
            val border = computedValues.border

            val bounds = component.getBounds(ResourceContext.Rectangle())
            val size = component.getSize(ResourceContext.Dimension())

            val borderWidth = border.toTInsets()
            val borderRadius = border.toTRadii(bounds)

            val margin = computedValues.margin.toTInsets(bounds)
            val padding = computedValues.padding.toTInsets(bounds)

            return from(backgroundClip, size, borderWidth, borderRadius, margin, padding)
        }
    }
}

internal fun computeBackgroundClip(
    clip: Clip,
    size: Dimension,
    borderWidth: TInsets,
    borderRadius: TRadii,
    margin: TInsets,
    padding: TInsets
): Shape {

    val (width, bounds) = when (clip) {
        Clip.ContentBox -> {
            Pair(
                padding + borderWidth,
                TBounds.fromDimension(size) - (padding + borderWidth + margin)
            )
        }
        Clip.PaddingBox -> {
            Pair(
                borderWidth,
                TBounds.fromDimension(size) - (borderWidth + margin)
            )
        }
        Clip.BorderBox -> {
            Pair(
                ResourceContext.TInsets(),
                TBounds.fromDimension(size) - (margin)
            )
        }
    }

    val radii = borderRadius - width
    val diminishedRadii = radii diminish bounds

    return computeRoundedRectangle(bounds, diminishedRadii)
}

/**
 * Computes a rounded rectangle shape using the bounds as a basic basic rectangle an refining it by adding round corners
 * accordingly. The corner radius shrinks with the specified "border" (width).
 *
 * @param rect  the basic rectangle
 * @param radii the corner radius at the corner of the virtual space
 * @return the rounded rectangle
 */
private fun computeRoundedRectangle(rect: TBounds, radii: TRadii): Path2D {
    val path = Path2D.Float()

    path.moveTo(rect.x + radii.topLeftWidth, rect.y)
    path.lineTo(rect.x + rect.width - radii.topRightWidth, rect.y)
    path.quadTo(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + radii.topRightHeight)
    path.lineTo(rect.x + rect.width, rect.y + rect.height - radii.bottomRightHeight)
    path.quadTo(rect.x + rect.width, rect.y + rect.height, rect.x + rect.width - radii.bottomRightWidth, rect.y + rect.height)
    path.lineTo((rect.x + radii.bottomLeftWidth).toDouble(), (rect.y + rect.height).toDouble())
    path.quadTo(rect.x.toDouble(), (rect.y + rect.height).toDouble(), rect.x.toDouble(), (rect.y + rect.height - radii.bottomLeftHeight).toDouble())
    path.lineTo(rect.x.toDouble(), (rect.y + radii.topLeftHeight).toDouble())
    path.quadTo(rect.x.toDouble(), rect.y.toDouble(), (rect.x + radii.topLeftWidth).toDouble(), rect.y.toDouble())
    path.closePath()

    return path
}