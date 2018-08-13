package modern.reflare.shape

import de.krall.flare.style.properties.longhand.Clip
import modern.reflare.geom.Bounds
import modern.reflare.geom.Insets
import modern.reflare.geom.Radii
import java.awt.Dimension
import java.awt.Shape
import java.awt.geom.Path2D

class BackgroundShape( val shape: Shape) {

    companion object {

        fun from(clip: Clip,
                 size: Dimension,
                 borderWidth: Insets,
                 borderRadius: Radii,
                 margin: Insets,
                 padding: Insets): BackgroundShape {
            return BackgroundShape(
                    computeBackgroundClip(clip, size, borderWidth, borderRadius, margin, padding)
            )
        }
    }
}

internal fun computeBackgroundClip(clip: Clip,
                                   size: Dimension,
                                   borderWidth: Insets,
                                   borderRadius: Radii,
                                   margin: Insets,
                                   padding: Insets): Shape {

    val (width, bounds) = when (clip) {
        Clip.CONTENT_BOX -> {
            Pair(
                    padding + borderWidth,
                    Bounds.fromDimension(size) - padding - borderWidth - margin
            )
        }
        Clip.PADDING_BOX -> {
            Pair(
                    borderWidth,
                    Bounds.fromDimension(size) - borderWidth - margin
            )
        }
        Clip.BORDER_BOX -> {
            Pair(
                    Insets.empty(),
                    Bounds.fromDimension(size) - margin
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
private fun computeRoundedRectangle(rect: Bounds, radii: Radii, width: Insets): Path2D {
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