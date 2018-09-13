package org.fernice.reflare.render

import org.fernice.flare.panic
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.geom.Colors
import org.fernice.reflare.geom.toColors
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.geom.toRadii
import org.fernice.reflare.shape.BackgroundShape
import org.fernice.reflare.shape.BorderShape
import fernice.std.None
import fernice.std.Some

class Cache(private val element: AWTComponentElement) {

    fun invalidate() {
        borderCacheInvalid = true
        backgroundCacheInvalid = true
    }

    internal fun invalidateBounds() {
        borderCacheInvalid = true
        backgroundCacheInvalid = true
    }

    fun setUncachable() {
        uncachable = true
    }

    private var uncachable = false

    private var cachedBorderHash: Int = -1
    private var borderCacheInvalid: Boolean = true
    private var cachedBorderShape: BorderShape? = null

    val borderShape: BorderShape
        get() {
            recomputeBorderShape()
            return cachedBorderShape!!
        }

    private fun recomputeBorderShape() {
        val styleResult = element.getStyle()

        val style = when (styleResult) {
            is Some -> styleResult.value
            is None -> panic("missing style")
        }

        val borderHash = style.borderShapeHash()

        if (uncachable || borderCacheInvalid || cachedBorderHash != borderHash) {
            computeBorderShape(style)
            cachedBorderHash = borderHash
            borderCacheInvalid = false
        }
    }

    private fun computeBorderShape(computedValues: ComputedValues) {
        val border = computedValues.border

        val borderWidth = border.toInsets()

        if (borderWidth.isZero()) {
            return
        }

        val component = element.component

        val bounds = component.bounds
        val size = component.size

        val borderRadius = border.toRadii(bounds)
        val borderColor = border.toColors(computedValues.color.color)

        val margin = element.margin
        val padding = element.padding

        cachedBorderShape = if (hasOnlyOneColor(borderColor)) {
            BorderShape.new(size, borderWidth, borderRadius, margin, padding)
        } else {
            BorderShape.from(size, borderWidth, borderRadius, margin)
        }
    }

    private var cachedBackgroundHash: Int = -1
    private var backgroundCacheInvalid: Boolean = true
    private var cachedBackgroundShape: BackgroundShape? = null

    val backgroundShape: BackgroundShape
        get() {
            recomputeBackgroundShape()
            return cachedBackgroundShape!!
        }

    private fun recomputeBackgroundShape() {
        val styleResult = element.getStyle()

        val style = when (styleResult) {
            is Some -> styleResult.value
            is None -> panic("missing style")
        }

        val backgroundHash = style.backgroundShapeHash()

        if (uncachable || backgroundCacheInvalid || cachedBackgroundHash != backgroundHash) {
            computeBackgroundShape(style)
            cachedBackgroundHash = backgroundHash
            backgroundCacheInvalid = false
        }
    }

    private fun computeBackgroundShape(computedValues: ComputedValues) {
        val background = computedValues.background
        val border = computedValues.border

        val component = element.component

        val bounds = component.bounds
        val size = component.size

        val borderWidth = border.toInsets()
        val borderRadius = border.toRadii(bounds)

        val margin = element.margin
        val padding = element.padding

        val backgroundClip = background.clip

        cachedBackgroundShape = BackgroundShape.from(backgroundClip, size, borderWidth, borderRadius, margin, padding)
    }
}

private fun hasOnlyOneColor(color: Colors): Boolean {
    return color.top == color.right && color.right == color.bottom && color.bottom == color.left
}