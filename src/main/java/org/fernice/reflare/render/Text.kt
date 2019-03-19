/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render

import org.fernice.reflare.accommodation.Accommodation
import java.awt.Rectangle
import javax.swing.JTextField

object TextAdjustment {

    const val ADJUST_MARGIN = 1
    const val ADJUST_INNER_MARGIN = 2
    const val ADJUST_NOTHING = 3
    const val ADJUST_AUTOMATICALLY = 4
}

/**
 * Adjusts the text for rending by storing an recomputed bounding rectangle as a client property.
 * The property is retrieved later in the rending process.
 */
fun JTextField.adjustTextToFitSpace(text: String, local: Boolean, mode: Int) {
    if (local && (mode == TextAdjustment.ADJUST_MARGIN
                || mode == TextAdjustment.ADJUST_INNER_MARGIN)
    ) {
        throw IllegalArgumentException("Margin cannot be adjusted locally (Mode $mode)")
    }

    val font = font
    val fontMetrics = getFontMetrics(font)

    val leading = if (fontMetrics.leading == 0) {
        1.0
    } else {
        fontMetrics.leading.toDouble()
    }

    val margin = fontMetrics.ascent + fontMetrics.descent
    val mean = (margin + fontMetrics.height) / leading

    val width = fontMetrics.stringWidth(text)

    val computedMargin = when (mode) {
        TextAdjustment.ADJUST_MARGIN -> margin.toDouble()
        TextAdjustment.ADJUST_INNER_MARGIN -> margin - mean
        TextAdjustment.ADJUST_AUTOMATICALLY -> if (local) margin.toDouble() else margin - mean
        TextAdjustment.ADJUST_NOTHING -> 0.0
        else -> error("unknown adjust mode (Mode $mode)")
    }

    val recyclableRectangle = this.getClientProperty(TextAdjustment) as? Rectangle

    val rectangle = Accommodation.adjustFraming(this, text, local, width, computedMargin, recyclableRectangle)

    this.putClientProperty(TextAdjustment, rectangle)
}