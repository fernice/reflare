package org.fernice.reflare.ui

import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.resource.RequiresResourceContext
import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.plus
import org.fernice.reflare.resource.toAWTInsets
import org.fernice.reflare.resource.toTInsets
import org.fernice.reflare.resource.withResourceContext
import java.awt.Component
import java.awt.Graphics
import java.awt.Rectangle
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

    override fun getBorderInsets(c: Component?, insets: AWTInsets?): AWTInsets {
        return withResourceContext { computeInsets { bounds -> margin.toTInsets(bounds) + border.toTInsets() + padding.toTInsets(bounds) }.toAWTInsets(insets) }
    }

    @RequiresResourceContext
    internal fun getMarginAndBorderTInsets(): TInsets {
        return computeInsets { bounds -> margin.toTInsets(bounds) + border.toTInsets() }
    }

    fun getMarginAndBorderInsets(): AWTInsets = withResourceContext { getMarginAndBorderTInsets().toAWTInsets() }
    fun getMarginInsets(): AWTInsets = withResourceContext { computeInsets { bounds -> margin.toTInsets(bounds) }.toAWTInsets() }
    fun getPaddingInsets(): AWTInsets = withResourceContext { computeInsets { bounds -> padding.toTInsets(bounds) }.toAWTInsets() }

    @RequiresResourceContext
    private inline fun computeInsets(block: ComputedValues.(Rectangle) -> TInsets): TInsets {
        val style = ui.element.getStyle()
        return if (style != null) {
            val bounds = ui.element.component.getBounds(ResourceContext.Rectangle())

            style.block(bounds)
        } else {
            ResourceContext.TInsets()
        }
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }
}
