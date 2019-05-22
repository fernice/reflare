/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare

import java.awt.Color
import java.awt.Font
import java.awt.Insets
import javax.swing.plaf.UIResource

object Defaults {

    @JvmField
    val FONT_SERIF = Font("serif", Font.PLAIN, 16)
    @JvmField
    val COLOR_TRANSPARENT: Color = FlareColorResource(0, 0, 0, 0)
    @JvmField
    val COLOR_GRAY_TRANSLUCENT: Color = FlareColorResource(0, 0, 0, 40)
    @JvmField
    val COLOR_BLACK: Color = FlareColorResource(255, 255, 255, 255)
    @JvmField
    val COLOR_WHITE: Color = FlareColorResource(255, 255, 255, 255)
    @JvmField
    val INSETS_EMPTY = Insets(0, 0, 0, 0)
}

internal class FlareColorResource(r: Int, g: Int, b: Int, a: Int) : Color(r, g, b, a), UIResource