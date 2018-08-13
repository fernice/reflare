package modern.reflare.geom

import de.krall.flare.cssparser.RGBA
import de.krall.flare.style.properties.stylestruct.Border
import de.krall.flare.style.properties.stylestruct.Margin
import de.krall.flare.style.properties.stylestruct.Padding
import de.krall.flare.style.value.computed.Au
import modern.reflare.toAWTColor
import java.awt.Rectangle

fun Border.toInsets(): Insets {
    return Insets(
            this.topWidth.length.px(),
            this.rightWidth.length.px(),
            this.bottomWidth.length.px(),
            this.leftWidth.length.px()
    )
}

fun Border.toRadii(bounds: Rectangle): Radii {
    return Radii(
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

fun Border.toColors(currentColor: RGBA): Colors {
    return Colors(
            this.topColor.toAWTColor(currentColor),
            this.rightColor.toAWTColor(currentColor),
            this.bottomColor.toAWTColor(currentColor),
            this.leftColor.toAWTColor(currentColor)
    )
}

fun Margin.toInsets(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

fun Padding.toInsets(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}