/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.flare.style.ComputedValues
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
import javax.swing.text.JTextComponent
import java.awt.Insets as AWTInsets

private val None = AWTInsets(0, 0, 0, 0)

open class FlareBorder protected constructor(private val ui: FlareUI) : AbstractBorder() {

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        ui.paintBorder(c ?: ui.element.component, g)
    }

    override fun getBorderInsets(c: Component?, insets: AWTInsets?): AWTInsets {
        val componentInsets = if (c != null) getComponentInsets(c) else None
        return withResourceContext {
            computeInsets { bounds ->
                margin.toTInsets(bounds) + border.toTInsets() + padding.toTInsets(bounds) + componentInsets
            }.toAWTInsets(insets)
        }
    }

    protected open fun getComponentInsets(c: Component): AWTInsets = None

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

    companion object {

        @JvmStatic
        fun create(ui: FlareUI): FlareBorder {
            return when (ui.element.component) {
                is JTextComponent -> TextComponentFlareBorder(ui)
                else -> FlareBorder(ui)
            }
        }
    }
}

class TextComponentFlareBorder(ui: FlareUI) : FlareBorder(ui) {

    override fun getComponentInsets(c: Component): AWTInsets {
        return (c as JTextComponent).margin ?: None
    }
}