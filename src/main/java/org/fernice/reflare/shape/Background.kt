package org.fernice.reflare.shape

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.geom.Bounds
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.Radii
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TBounds
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.TRadii
import org.fernice.reflare.resource.minus
import org.fernice.reflare.resource.plus
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.toTRadii
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

        fun computeBackgroundShape(computedValues: ComputedValues, element: AWTComponentElement): BackgroundShape {
            val background = computedValues.background
            val border = computedValues.border

            val component = element.component

            val bounds = component.getBounds(ResourceContext.Rectangle())
            val size = component.size

            val borderWidth = border.toTInsets()
            val borderRadius = border.toTRadii(bounds)

            val margin = computedValues.margin.toTInsets(bounds)
            val padding = computedValues.padding.toTInsets(bounds)

            val backgroundClip = background.clip

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

    return computeRoundedRectangle(bounds, borderRadius, width)
}

/**
 * Computes a rounded rectangle shape using the bounds as a basic basic rectangle an refining it by adding round corners
 * accordingly. The corner radius shrinks with the specified "border" (width).
 *
 * @param rect  the basic rectangle
 * @param radii the corner radius at the corner of the virtual space
 * @param width the width that define the virtual space together with the rectangle
 * @return the rounded rectangle
 */
private fun computeRoundedRectangle(rect: TBounds, radii: TRadii, width: TInsets): Path2D {
    val path = Path2D.Float()

    val tls = if (width.top < radii.topLeftHeight) radii.topLeftHeight - width.top else 0f
    val tlt = if (width.left < radii.topLeftWidth) radii.topLeftWidth - width.left else 0f
    val trs = if (width.top < radii.topRightHeight) radii.topRightHeight - width.top else 0f
    val trt = if (width.right < radii.topRightWidth) radii.topRightWidth - width.right else 0f
    val brs = if (width.bottom < radii.bottomRightHeight) radii.bottomRightHeight - width.bottom else 0f
    val brt = if (width.right < radii.bottomRightWidth) radii.bottomRightWidth - width.right else 0f
    val bls = if (width.bottom < radii.bottomLeftHeight) radii.bottomLeftHeight - width.bottom else 0f
    val blt = if (width.left < radii.bottomLeftWidth) radii.bottomLeftWidth - width.left else 0f

    path.moveTo((rect.x + tlt).toDouble(), rect.y.toDouble())
    path.lineTo((rect.x + rect.width - trt).toDouble(), rect.y.toDouble())
    path.quadTo((rect.x + rect.width).toDouble(), rect.y.toDouble(), (rect.x + rect.width).toDouble(), (rect.y + trs).toDouble())
    path.lineTo((rect.x + rect.width).toDouble(), (rect.y + rect.height - brs).toDouble())
    path.quadTo((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble(), (rect.x + rect.width - brt).toDouble(), (rect.y + rect.height).toDouble())
    path.lineTo((rect.x + blt).toDouble(), (rect.y + rect.height).toDouble())
    path.quadTo(rect.x.toDouble(), (rect.y + rect.height).toDouble(), rect.x.toDouble(), (rect.y + rect.height - bls).toDouble())
    path.lineTo(rect.x.toDouble(), (rect.y + tls).toDouble())
    path.quadTo(rect.x.toDouble(), rect.y.toDouble(), (rect.x + tlt).toDouble(), rect.y.toDouble())

    return path
}