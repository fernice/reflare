package modern.reflare.ui

import de.krall.flare.std.Some
import de.krall.flare.style.properties.stylestruct.Border
import de.krall.flare.style.properties.stylestruct.Margin
import de.krall.flare.style.properties.stylestruct.Padding
import de.krall.flare.style.value.computed.Au
import modern.reflare.element.AWTComponentElement
import modern.reflare.geom.Insets
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets as AWTInsets
import java.awt.Rectangle
import javax.swing.border.AbstractBorder

interface FlareUI {

    val element: AWTComponentElement

    fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int)
}

private fun Margin.into(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

private fun Padding.into(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

private fun Border.toWidth(): Insets {
    return Insets(
            this.topWidth.length.px(),
            this.rightWidth.length.px(),
            this.bottomWidth.length.px(),
            this.leftWidth.length.px()
    )
}

class FlareBorder(private val ui: FlareUI) : AbstractBorder() {

    override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        ui.paintBorder(c!!, g!!, x, y, width, height)
    }

    override fun getBorderInsets(c: Component, insets: AWTInsets?): AWTInsets {
        var insets = insets
        if (insets == null) {
            insets = AWTInsets(0, 0, 0, 0)
        } else {
            insets.top = 0
            insets.right = 0
            insets.bottom = 0
            insets.left = 0
        }

        val style = ui.element.getStyle()

        if (style is Some) {
            val values = style.value
            val bounds = c.bounds

            insets = insets + values.margin.into(bounds) + values.padding.into(bounds) + values.border.toWidth()
        }

        return insets
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }
}

operator fun AWTInsets.plus(insets: Insets): AWTInsets {
    this.top += insets.top.toInt()
    this.right += insets.right.toInt()
    this.bottom += insets.bottom.toInt()
    this.left += insets.left.toInt()

    return this
}
