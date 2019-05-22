package org.fernice.reflare.ui

import fernice.std.Some
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.toInsets
import java.awt.Component
import java.awt.Graphics
import javax.swing.border.AbstractBorder
import java.awt.Insets as AWTInsets

interface FlareUI {

    val element: AWTComponentElement

    fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int)
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

            insets = insets + values.margin.toInsets(bounds) + values.border.toInsets() + values.padding.toInsets(bounds)
        }

        return insets
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }

    fun getMarginAndBorderInsets(): AWTInsets {
        val style = ui.element.getStyle()

        return if (style is Some) {
            val values = style.value
            val bounds = ui.element.component.bounds

            AWTInsets(0, 0, 0, 0) + values.margin.toInsets(bounds) + values.border.toInsets()
        } else {
            AWTInsets(0, 0, 0, 0)
        }
    }

    fun getMarginInsets(): AWTInsets {
        val style = ui.element.getStyle()

        return if (style is Some) {
            val values = style.value
            val bounds = ui.element.component.bounds

            AWTInsets(0, 0, 0, 0) + values.margin.toInsets(bounds)
        } else {
            AWTInsets(0, 0, 0, 0)
        }
    }

    fun getPaddingInsets(): AWTInsets {
        val style = ui.element.getStyle()

        return if (style is Some) {
            val values = style.value
            val bounds = ui.element.component.bounds

            AWTInsets(0, 0, 0, 0) + values.padding.toInsets(bounds)
        } else {
            AWTInsets(0, 0, 0, 0)
        }
    }
}

operator fun AWTInsets.plus(insets: Insets): AWTInsets {
    this.top += insets.top.toInt()
    this.right += insets.right.toInt()
    this.bottom += insets.bottom.toInt()
    this.left += insets.left.toInt()

    return this
}
