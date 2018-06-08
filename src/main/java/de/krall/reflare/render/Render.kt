package de.krall.reflare.render

import de.krall.flare.std.None
import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.style.ComputedValues
import de.krall.flare.style.properties.longhand.Clip
import de.krall.flare.style.properties.stylestruct.Border
import de.krall.flare.style.properties.stylestruct.Margin
import de.krall.flare.style.properties.stylestruct.Padding
import de.krall.flare.style.value.computed.Au
import de.krall.reflare.t.TBounds
import de.krall.reflare.t.TColor
import de.krall.reflare.t.TInsets
import de.krall.reflare.t.TRadius
import de.krall.reflare.toAWTColor
import sun.nio.cs.ext.TIS_620
import java.awt.*
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.geom.Path2D

fun renderBackground(g: Graphics, component: Component, style: Option<ComputedValues>) {
    val values = when (style) {
        is Some -> style.value
        is None -> {
            val bounds = component.bounds

            g.color = Color.RED
            g.fillRect(0, 0, bounds.width, bounds.height)
            return
        }
    }

    paintBackground(g, component, values)
}

fun renderBorder(g: Graphics, component: Component, style: Option<ComputedValues>) {
    if (style is Some) {
        paintBorder(g, component, style.value)
    }

}

fun setRenderingHints(g: Graphics) {
    val g2 = g as Graphics2D
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
}

private fun Border.toWidth(): TInsets {
    return TInsets(
            this.topWidth.length.px(),
            this.rightWidth.length.px(),
            this.bottomWidth.length.px(),
            this.leftWidth.length.px()
    )
}

private fun Border.toRadius(bounds: Rectangle): TRadius {
    return TRadius(
            this.topLeftRadius.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.topRightRadius.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomRightRadius.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomLeftRadius.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

private fun Border.toColor(): TColor {
    return TColor(
            this.topColor.toAWTColor(),
            this.rightColor.toAWTColor(),
            this.bottomColor.toAWTColor(),
            this.leftColor.toAWTColor()
    )
}

private fun Margin.toInsets(bounds: Rectangle): TInsets {
    return TInsets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

private fun Padding.toInsets(bounds: Rectangle): TInsets {
    return TInsets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

fun paintBackground(g: Graphics, component: Component, computedValues: ComputedValues) {
    val g2 = g as Graphics2D

    setRenderingHints(g)

    val background = computedValues.background
    val border = computedValues.border

    val bounds = component.bounds
    val size = component.size

    val borderWidth = border.toWidth()
    val borderRadius = border.toRadius(bounds)

    val margin = computedValues.margin.toInsets(bounds)
    val padding = computedValues.padding.toInsets(bounds)

    val backgroundClip = background.clip

    val clip = computeBackgroundClip(backgroundClip, size, borderWidth, borderRadius, margin, padding)

    g2.clip = clip

    g2.color = background.color.toAWTColor()
    g2.fill(clip)
}

fun paintBorder(g: Graphics, component: Component, computedValues: ComputedValues) {
    val g2 = g as Graphics2D

    setRenderingHints(g)

    val border = computedValues.border

    val bounds = component.bounds
    val size = component.size

    val borderWidth = border.toWidth()
    val borderRadius = border.toRadius(bounds)
    val borderColor = border.toColor()

    val margin = computedValues.margin.toInsets(bounds)
    val padding = computedValues.padding.toInsets(bounds)

    if (hasOnlyOneColor(borderColor)) {
        val borderClip = computeBackgroundClip(Clip.BORDER_BOX, size, borderWidth, borderRadius, margin, padding)
        val paddingClip = computeBackgroundClip(Clip.PADDING_BOX, size, borderWidth, borderRadius, margin, padding)

        val clip = Area(borderClip)
        clip.subtract(Area(paddingClip))

        g2.color = borderColor.top
        g2.fill(clip)
    } else {
        val edges = computeBorderEdges(size, borderWidth, borderRadius, margin)

        g2.color = borderColor.top
        g2.fill(edges[0])

        g2.color = borderColor.right
        g2.fill(edges[1])

        g2.color = borderColor.bottom
        g2.fill(edges[2])

        g2.color = borderColor.left
        g2.fill(edges[3])
    }
}

private fun hasOnlyOneColor(color: TColor): Boolean {
    return color.top == color.right && color.right == color.bottom && color.bottom == color.left
}

private fun computeBackgroundClip(clip: Clip, size: Dimension, borderWidth: TInsets, borderRadius: TRadius,
                                  margin: TInsets, padding: TInsets): Shape {
    val width = TInsets()
    val bounds = TBounds(size)

    when (clip) {
        Clip.CONTENT_BOX -> {
            width.increase(padding)
            bounds.reduce(padding)
            width.increase(borderWidth)
            bounds.reduce(borderWidth)
            bounds.reduce(margin)
        }
        Clip.PADDING_BOX -> {
            width.increase(borderWidth)
            bounds.reduce(borderWidth)
            bounds.reduce(margin)
        }
        Clip.BORDER_BOX -> bounds.reduce(margin)
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
private fun computeRoundedRectangle(rect: TBounds, radii: TRadius, width: TInsets): Path2D {
    val path = Path2D.Float()

    val tls = if (width.top < radii.topLeft) radii.topLeft - width.top else 0f
    val tlt = if (width.left < radii.topLeft) radii.topLeft - width.left else 0f
    val trs = if (width.top < radii.topRight) radii.topRight - width.top else 0f
    val trt = if (width.right < radii.topRight) radii.topRight - width.right else 0f
    val brs = if (width.bottom < radii.bottomRight) radii.bottomRight - width.bottom else 0f
    val brt = if (width.right < radii.bottomRight) radii.bottomRight - width.right else 0f
    val bls = if (width.bottom < radii.bottomLeft) radii.bottomLeft - width.bottom else 0f
    val blt = if (width.left < radii.bottomLeft) radii.bottomLeft - width.left else 0f

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

private fun computeBorderEdges(size: Dimension, borderWidth: TInsets, borderRadius: TRadius, margin: TInsets): Array<Shape?> {
    val bounds = TBounds(size)

    bounds.reduce(margin)

    val edges = arrayOfNulls<Shape>(4)

    edges[0] = computeTopBorder(bounds, borderRadius, borderWidth)
    edges[1] = computeRightBorder(bounds, borderRadius, borderWidth)
    edges[2] = computeBottomBorder(bounds, borderRadius, borderWidth)
    edges[3] = computeLeftBorder(bounds, borderRadius, borderWidth)

    return edges
}

private fun computeTopBorder(rect: TBounds, radii: TRadius, width: TInsets): Path2D {
    val path = Path2D.Float()

    val tlt = if (width.top < radii.topLeft) radii.topLeft - width.top else 0f
    val tls = if (width.left < radii.topLeft) radii.topLeft - width.left else 0f
    val trt = if (width.top < radii.topRight) radii.topRight - width.top else 0f
    val trs = if (width.right < radii.topRight) radii.topRight - width.right else 0f

    path.moveTo((rect.x + radii.topLeft).toDouble(), rect.y.toDouble())
    path.lineTo((rect.x + rect.width - radii.topRight).toDouble(), rect.y.toDouble())

    path.append(Arc2D.Float(rect.x + rect.width - radii.topRight * 2, rect.y, radii.topRight * 2, radii.topRight * 2, 90f, -45f, Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x + rect.width - Math.max(radii.topRight, width.right) - trs, rect.y + width.top, trs * 2, trt * 2, 45f, 45f, Arc2D.OPEN),
            true)

    path.lineTo((rect.x + Math.max(radii.topLeft, width.left)).toDouble(), (rect.y + width.top).toDouble())

    path.append(Arc2D.Float(rect.x + Math.max(radii.topLeft, width.left) - tls, rect.y + Math.max(radii.topRight, width.top) - tlt, tls * 2, tlt * 2, 90f, 45f,
            Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x, rect.y, radii.topLeft * 2, radii.topLeft * 2, 135f, -45f, Arc2D.OPEN), true)

    return path
}

private fun computeRightBorder(rect: TBounds, radii: TRadius, width: TInsets): Path2D {
    val path = Path2D.Float()

    val brt = if (width.bottom < radii.bottomRight) radii.bottomRight - width.bottom else 0f
    val brs = if (width.right < radii.bottomRight) radii.bottomRight - width.right else 0f
    val trt = if (width.top < radii.topRight) radii.topRight - width.top else 0f
    val trs = if (width.right < radii.topRight) radii.topRight - width.right else 0f

    path.moveTo((rect.x + rect.width).toDouble(), (rect.y + radii.topRight).toDouble())
    path.lineTo((rect.x + rect.width).toDouble(), (rect.y + rect.height - radii.bottomRight).toDouble())

    path.append(Arc2D.Float(rect.x + rect.width - radii.bottomRight * 2, rect.y + rect.height - radii.bottomRight * 2, radii.bottomRight * 2,
            radii.bottomRight * 2, 0f, -45f, Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x + rect.width - Math.max(radii.bottomRight, width.right) - brs,
            rect.y + rect.height - Math.max(radii.bottomRight, width.bottom) - brt, brs * 2, brt * 2, -45f, 45f, Arc2D.OPEN), true)

    path.lineTo((rect.x + rect.width - width.right).toDouble(), (rect.y + Math.max(radii.topRight, width.bottom)).toDouble())

    path.append(Arc2D.Float(rect.x + rect.width - Math.max(radii.topRight, width.right) - trs, rect.y + Math.max(radii.topRight, width.top) - trt, trs * 2,
            trt * 2, 0f, 45f, Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x + rect.width - radii.topRight * 2, rect.y, radii.topRight * 2, radii.topRight * 2, 45f, -45f, Arc2D.OPEN), true)

    return path
}

private fun computeBottomBorder(rect: TBounds, radii: TRadius, width: TInsets): Path2D {
    val path = Path2D.Float()

    val blt = if (width.bottom < radii.bottomLeft) radii.bottomLeft - width.bottom else 0f
    val bls = if (width.left < radii.bottomLeft) radii.bottomLeft - width.left else 0f
    val brt = if (width.bottom < radii.bottomRight) radii.bottomRight - width.bottom else 0f
    val brs = if (width.right < radii.bottomRight) radii.bottomRight - width.right else 0f

    path.moveTo((rect.x + radii.topLeft).toDouble(), (rect.y + rect.height).toDouble())
    path.lineTo((rect.x + rect.width - radii.bottomRight).toDouble(), (rect.y + rect.height).toDouble())

    path.append(Arc2D.Float(rect.x + rect.width - radii.bottomRight * 2, rect.y + rect.height - radii.bottomRight * 2, radii.bottomRight * 2,
            radii.bottomRight * 2, -90f, 45f, Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x + rect.width - Math.max(radii.bottomRight, width.right) - brs,
            rect.y + rect.height - Math.max(radii.bottomRight, width.bottom) - brt, brs * 2, brt * 2, -45f, -45f, Arc2D.OPEN), true)

    path.lineTo((rect.x + Math.max(radii.topLeft, width.left)).toDouble(), (rect.y + rect.height - width.bottom).toDouble())

    path.append(
            Arc2D.Float(rect.x + Math.max(radii.bottomLeft, width.left) - bls, rect.y + rect.height - Math.max(radii.bottomLeft, width.bottom) - blt, bls * 2,
                    blt * 2, -90f, -45f, Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x, rect.y + rect.height - radii.bottomLeft * 2, radii.bottomLeft * 2, radii.bottomLeft * 2, -135f, 45f, Arc2D.OPEN), true)

    return path
}

private fun computeLeftBorder(rect: TBounds, radii: TRadius, width: TInsets): Path2D {
    val path = Path2D.Float()

    val blt = if (width.bottom < radii.bottomLeft) radii.bottomLeft - width.bottom else 0f
    val bls = if (width.left < radii.bottomLeft) radii.bottomLeft - width.left else 0f
    val tlt = if (width.top < radii.topLeft) radii.topLeft - width.top else 0f
    val tls = if (width.left < radii.topLeft) radii.topLeft - width.left else 0f

    path.moveTo(rect.x.toDouble(), (rect.y + radii.topLeft).toDouble())
    path.lineTo(rect.x.toDouble(), (rect.y + rect.height - radii.bottomLeft).toDouble())

    path.append(Arc2D.Float(rect.x, rect.y + rect.height - radii.bottomLeft * 2, radii.bottomLeft * 2, radii.bottomLeft * 2, 180f, 45f, Arc2D.OPEN), true)
    path.append(
            Arc2D.Float(rect.x + Math.max(radii.bottomLeft, width.left) - bls, rect.y + rect.height - Math.max(radii.bottomLeft, width.bottom) - blt, bls * 2,
                    blt * 2, 225f, -45f, Arc2D.OPEN), true)

    path.lineTo((rect.x + width.left).toDouble(), (rect.y + Math.max(radii.topLeft, width.top)).toDouble())

    path.append(Arc2D.Float(rect.x + Math.max(radii.topLeft, width.left) - tls, rect.y + Math.max(radii.topLeft, width.top) - tlt, tls * 2, tlt * 2, 180f, -45f,
            Arc2D.OPEN), true)
    path.append(Arc2D.Float(rect.x, rect.y, radii.topLeft * 2, radii.topLeft * 2, 135f, 45f, Arc2D.OPEN), true)

    return path
}