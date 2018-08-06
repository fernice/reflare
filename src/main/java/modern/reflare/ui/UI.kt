package modern.reflare.ui

import de.krall.flare.std.Some
import de.krall.flare.style.properties.stylestruct.Border
import de.krall.flare.style.properties.stylestruct.Margin
import de.krall.flare.style.properties.stylestruct.Padding
import de.krall.flare.style.value.computed.Au
import modern.reflare.element.AWTComponentElement
import modern.reflare.t.TInsets
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import java.awt.Rectangle
import javax.swing.border.AbstractBorder

interface FlareUI {

    val element: AWTComponentElement

    fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int)
}

private fun Margin.into(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px().toInt()
    )
}

private fun Padding.into(bounds: Rectangle): Insets {
    return Insets(
            this.top.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.right.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.bottom.toPixelLength(Au.fromPx(bounds.width)).px().toInt(),
            this.left.toPixelLength(Au.fromPx(bounds.width)).px().toInt()
    )
}

private fun Border.toWidth(): Insets {
    return Insets(
            this.topWidth.length.px().toInt(),
            this.rightWidth.length.px().toInt(),
            this.bottomWidth.length.px().toInt(),
            this.leftWidth.length.px().toInt()
    )
}

class FlareBorder(private val ui: FlareUI) : AbstractBorder() {

    override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        ui.paintBorder(c!!, g!!, x, y, width, height)
    }

    override fun getBorderInsets(c: Component, insets: Insets?): Insets {
        var insets = insets
        if (insets == null) {
            insets = Insets(0, 0, 0, 0)
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

            combine(insets, values.margin.into(bounds))
            combine(insets, values.padding.into(bounds))
            combine(insets, values.border.toWidth())
        }

        return insets
    }

    private fun combine(insets: Insets, addition: Insets?) {
        if (addition != null) {
            insets.top += addition.top
            insets.right += addition.right
            insets.bottom += addition.bottom
            insets.left += addition.left
        }
    }

    private fun combine(insets: Insets, addition: TInsets?) {
        if (addition != null) {
            insets.top += addition.top.toInt()
            insets.right += addition.right.toInt()
            insets.bottom += addition.bottom.toInt()
            insets.left += addition.left.toInt()
        }
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }
}