package modern.reflare.render

import de.krall.flare.cssparser.RGBA
import de.krall.flare.style.properties.stylestruct.Border
import de.krall.flare.style.value.computed.Au
import modern.reflare.t.TColor
import modern.reflare.t.TInsets
import modern.reflare.t.TRadius
import modern.reflare.toAWTColor
import java.awt.Rectangle
import java.awt.Shape

sealed class RenderCacheStrategy {

    abstract fun invalidateSizeDependant()

    abstract fun invalidate()

    abstract fun computedBorderWidth(border: Border): TInsets

    abstract fun computedBorderRadius(border: Border, bounds: Rectangle): TRadius

    abstract fun computedBorderColor(border: Border, currentColor: RGBA): TColor

    class NoCache : RenderCacheStrategy() {
        override fun invalidateSizeDependant() {
        }

        override fun invalidate() {
        }

        override fun computedBorderWidth(border: Border): TInsets {
            return border.toInsets(TInsets())
        }

        override fun computedBorderRadius(border: Border, bounds: Rectangle): TRadius {
            return border.toRadius(bounds, TRadius())
        }

        override fun computedBorderColor(border: Border, currentColor: RGBA): TColor {
            return border.toColor(currentColor, TColor())
        }
    }

    class CacheAll : RenderCacheStrategy() {
        override fun invalidateSizeDependant() {
            borderRadiusInvalid = true
        }

        override fun invalidate() {
            borderWidthInvalid = true
            borderRadiusInvalid = true
            borderColorInvalid = true
        }

        private var borderWidthInvalid = true
        private val borderWidth: TInsets = TInsets()

        override fun computedBorderWidth(border: Border): TInsets {
            if (borderWidthInvalid) {
                border.toInsets(borderWidth)
                borderWidthInvalid = false
            }
            return borderWidth
        }

        private var borderRadiusInvalid = true
        private val borderRadius: TRadius = TRadius()

        override fun computedBorderRadius(border: Border, bounds: Rectangle): TRadius {
            if (borderRadiusInvalid) {
                border.toRadius(bounds, borderRadius)
                borderRadiusInvalid = false
            }
            return borderRadius
        }

        private var borderColorInvalid = true
        private val borderColor: TColor = TColor()

        override fun computedBorderColor(border: Border, currentColor: RGBA): TColor {
            if (borderColorInvalid) {
                border.toColor(currentColor, borderColor)
                borderColorInvalid = false
            }
            return borderColor
        }


    }
}

sealed class BorderShape {

    class Simple(val shape: Shape) : BorderShape()

    class Complex(val top: Shape, val right: Shape, val bottom: Shape, val left: Shape) : BorderShape()
}

private fun Border.toInsets(insets: TInsets): TInsets {
    return insets.set(
            this.topWidth.length.px(),
            this.rightWidth.length.px(),
            this.bottomWidth.length.px(),
            this.leftWidth.length.px()
    )
}

private fun Border.toRadius(bounds: Rectangle, radius: TRadius): TRadius {
    return radius.set(
            this.topLeftRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.topLeftRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.topRightRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.topRightRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomRightRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomRightRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomLeftRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottomLeftRadius.height.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

private fun Border.toColor(currentColor: RGBA, color: TColor): TColor {
    return color.set(
            this.topColor.toAWTColor(currentColor),
            this.rightColor.toAWTColor(currentColor),
            this.bottomColor.toAWTColor(currentColor),
            this.leftColor.toAWTColor(currentColor)
    )
}