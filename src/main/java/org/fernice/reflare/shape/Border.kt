package org.fernice.reflare.shape

import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.properties.longhand.background.Clip
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TBounds
import org.fernice.reflare.resource.TColors
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.TRadii
import org.fernice.reflare.resource.minus
import org.fernice.reflare.resource.toTColors
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.toTRadii
import java.awt.Dimension
import java.awt.Shape
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.geom.Path2D
import kotlin.math.max

private fun hasOnlyOneColor(color: TColors): Boolean {
    return color.top == color.right && color.right == color.bottom && color.bottom == color.left
}

internal sealed class BorderShape {

    class Simple(val shape: Area) : BorderShape()

    class Complex(
        val top: Shape,
        val right: Shape,
        val bottom: Shape,
        val left: Shape
    ) : BorderShape()

    companion object {

        fun computeBorderShape(computedValues: ComputedValues, element: AWTComponentElement): BorderShape {
            val border = computedValues.border

            val borderWidth = border.toTInsets()

            val component = element.component

            val bounds = component.getBounds(ResourceContext.Rectangle())
            val size = component.size

            val borderRadius = border.toTRadii(bounds)
            val borderColor = border.toTColors(computedValues.color.color)

            val margin = computedValues.margin.toTInsets(bounds)
            val padding = computedValues.padding.toTInsets(bounds)

            return if (hasOnlyOneColor(borderColor)) {
                simple(size, borderWidth, borderRadius, margin, padding)
            } else {
                complex(size, borderWidth, borderRadius, margin)
            }
        }

        fun simple(
            size: Dimension,
            borderWidth: TInsets,
            borderRadius: TRadii,
            margin: TInsets,
            padding: TInsets
        ): BorderShape {
            val borderClip = computeBackgroundClip(Clip.BorderBox, size, borderWidth, borderRadius, margin, padding)
            val paddingClip = computeBackgroundClip(Clip.PaddingBox, size, borderWidth, borderRadius, margin, padding)

            val clip = Area(borderClip)
            clip.subtract(Area(paddingClip))

            return BorderShape.Simple(clip)
        }

        fun complex(
            size: Dimension,
            borderWidth: TInsets,
            borderRadius: TRadii,
            margin: TInsets
        ): BorderShape {
            val bounds = TBounds.fromDimension(size) - margin

            return BorderShape.Complex(
                computeTopBorder(bounds, borderRadius, borderWidth),
                computeRightBorder(bounds, borderRadius, borderWidth),
                computeBottomBorder(bounds, borderRadius, borderWidth),
                computeLeftBorder(bounds, borderRadius, borderWidth)
            )
        }
    }
}


private fun computeTopBorder(rect: TBounds, radii: TRadii, width: TInsets): Path2D {
    val path = Path2D.Float()

    val tlt = if (width.top < radii.topLeftHeight) radii.topLeftHeight - width.top else 0f
    val tls = if (width.left < radii.topLeftWidth) radii.topLeftWidth - width.left else 0f
    val trt = if (width.top < radii.topRightHeight) radii.topRightHeight - width.top else 0f
    val trs = if (width.right < radii.topRightWidth) radii.topRightWidth - width.right else 0f

    path.moveTo((rect.x + radii.topLeftWidth).toDouble(), rect.y.toDouble())
    path.lineTo((rect.x + rect.width - radii.topRightWidth).toDouble(), rect.y.toDouble())

    path.append(
        Arc2D.Float(rect.x + rect.width - radii.topRightWidth * 2, rect.y, radii.topRightWidth * 2, radii.topRightHeight * 2, 90f, -45f, Arc2D.OPEN),
        true
    )
    path.append(
        Arc2D.Float(rect.x + rect.width - max(radii.topRightWidth, width.right) - trs, rect.y + width.top, trs * 2, trt * 2, 45f, 45f, Arc2D.OPEN),
        true
    )

    path.lineTo((rect.x + max(radii.topLeftWidth, width.left)).toDouble(), (rect.y + width.top).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + max(radii.topLeftWidth, width.left) - tls, rect.y + max(radii.topRightHeight, width.top) - tlt, tls * 2, tlt * 2, 90f, 45f,
            Arc2D.OPEN
        ), true
    )
    path.append(Arc2D.Float(rect.x, rect.y, radii.topLeftWidth * 2, radii.topLeftHeight * 2, 135f, -45f, Arc2D.OPEN), true)

    return path
}

private fun computeRightBorder(rect: TBounds, radii: TRadii, width: TInsets): Path2D {
    val path = Path2D.Float()

    val brt = if (width.bottom < radii.bottomRightHeight) radii.bottomRightHeight - width.bottom else 0f
    val brs = if (width.right < radii.bottomRightWidth) radii.bottomRightWidth - width.right else 0f
    val trt = if (width.top < radii.topRightHeight) radii.topRightWidth - width.top else 0f
    val trs = if (width.right < radii.topRightWidth) radii.topRightWidth - width.right else 0f

    path.moveTo((rect.x + rect.width).toDouble(), (rect.y + radii.topRightHeight).toDouble())
    path.lineTo((rect.x + rect.width).toDouble(), (rect.y + rect.height - radii.bottomRightHeight).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + rect.width - radii.bottomRightWidth * 2, rect.y + rect.height - radii.bottomRightHeight * 2, radii.bottomRightWidth * 2,
            radii.bottomRightHeight * 2, 0f, -45f, Arc2D.OPEN
        ), true
    )
    path.append(
        Arc2D.Float(
            rect.x + rect.width - max(radii.bottomRightWidth, width.right) - brs,
            rect.y + rect.height - max(radii.bottomRightHeight, width.bottom) - brt, brs * 2, brt * 2, -45f, 45f, Arc2D.OPEN
        ), true
    )

    path.lineTo((rect.x + rect.width - width.right).toDouble(), (rect.y + max(radii.topRightHeight, width.bottom)).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + rect.width - max(radii.topRightWidth, width.right) - trs, rect.y + max(radii.topRightHeight, width.top) - trt, trs * 2,
            trt * 2, 0f, 45f, Arc2D.OPEN
        ), true
    )
    path.append(
        Arc2D.Float(rect.x + rect.width - radii.topRightWidth * 2, rect.y, radii.topRightWidth * 2, radii.topRightHeight * 2, 45f, -45f, Arc2D.OPEN),
        true
    )

    return path
}

private fun computeBottomBorder(rect: TBounds, radii: TRadii, width: TInsets): Path2D {
    val path = Path2D.Float()

    val blt = if (width.bottom < radii.bottomLeftHeight) radii.bottomLeftHeight - width.bottom else 0f
    val bls = if (width.left < radii.bottomLeftWidth) radii.bottomLeftWidth - width.left else 0f
    val brt = if (width.bottom < radii.bottomRightHeight) radii.bottomRightHeight - width.bottom else 0f
    val brs = if (width.right < radii.bottomRightWidth) radii.bottomRightWidth - width.right else 0f

    path.moveTo((rect.x + radii.topLeftWidth).toDouble(), (rect.y + rect.height).toDouble())
    path.lineTo((rect.x + rect.width - radii.bottomRightWidth).toDouble(), (rect.y + rect.height).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + rect.width - radii.bottomRightWidth * 2, rect.y + rect.height - radii.bottomRightHeight * 2, radii.bottomRightWidth * 2,
            radii.bottomRightHeight * 2, -90f, 45f, Arc2D.OPEN
        ), true
    )
    path.append(
        Arc2D.Float(
            rect.x + rect.width - max(radii.bottomRightWidth, width.right) - brs,
            rect.y + rect.height - max(radii.bottomRightHeight, width.bottom) - brt, brs * 2, brt * 2, -45f, -45f, Arc2D.OPEN
        ), true
    )

    path.lineTo((rect.x + max(radii.topLeftWidth, width.left)).toDouble(), (rect.y + rect.height - width.bottom).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + max(radii.bottomLeftWidth, width.left) - bls, rect.y + rect.height - max(radii.bottomLeftHeight, width.bottom) - blt, bls * 2,
            blt * 2, -90f, -45f, Arc2D.OPEN
        ), true
    )
    path.append(
        Arc2D.Float(
            rect.x,
            rect.y + rect.height - radii.bottomLeftHeight * 2,
            radii.bottomLeftWidth * 2,
            radii.bottomLeftHeight * 2,
            -135f,
            45f,
            Arc2D.OPEN
        ), true
    )

    return path
}

private fun computeLeftBorder(rect: TBounds, radii: TRadii, width: TInsets): Path2D {
    val path = Path2D.Float()

    val blt = if (width.bottom < radii.bottomLeftHeight) radii.bottomLeftHeight - width.bottom else 0f
    val bls = if (width.left < radii.bottomLeftWidth) radii.bottomLeftWidth - width.left else 0f
    val tlt = if (width.top < radii.topLeftHeight) radii.topLeftHeight - width.top else 0f
    val tls = if (width.left < radii.topLeftWidth) radii.topLeftWidth - width.left else 0f

    path.moveTo(rect.x.toDouble(), (rect.y + radii.topLeftHeight).toDouble())
    path.lineTo(rect.x.toDouble(), (rect.y + rect.height - radii.bottomLeftHeight).toDouble())

    path.append(
        Arc2D.Float(
            rect.x,
            rect.y + rect.height - radii.bottomLeftHeight * 2,
            radii.bottomLeftWidth * 2,
            radii.bottomLeftHeight * 2,
            180f,
            45f,
            Arc2D.OPEN
        ), true
    )
    path.append(
        Arc2D.Float(
            rect.x + max(radii.bottomLeftWidth, width.left) - bls, rect.y + rect.height - max(radii.bottomLeftHeight, width.bottom) - blt, bls * 2,
            blt * 2, 225f, -45f, Arc2D.OPEN
        ), true
    )

    path.lineTo((rect.x + width.left).toDouble(), (rect.y + max(radii.topLeftHeight, width.top)).toDouble())

    path.append(
        Arc2D.Float(
            rect.x + max(radii.topLeftWidth, width.left) - tls, rect.y + max(radii.topLeftHeight, width.top) - tlt, tls * 2, tlt * 2, 180f, -45f,
            Arc2D.OPEN
        ), true
    )
    path.append(Arc2D.Float(rect.x, rect.y, radii.topLeftWidth * 2, radii.topLeftHeight * 2, 135f, 45f, Arc2D.OPEN), true)

    return path
}