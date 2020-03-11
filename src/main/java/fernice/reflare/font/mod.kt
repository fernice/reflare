@file:JvmName("FontUtil")
/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.font

import org.fernice.reflare.internal.SunFontHelper
import java.awt.Font

val Font.weight: Int
    get() = SunFontHelper.getFontWeight(this)

val Font.italic: Boolean
    get() = SunFontHelper.isFontItalic(this)

fun Font.derive(size: Int = this.size, weight: Int = this.weight, italic: Boolean = this.italic): Font {
    return SunFontHelper.findFont(family, weight, italic)?.deriveFont(size.toFloat()) ?: this
}