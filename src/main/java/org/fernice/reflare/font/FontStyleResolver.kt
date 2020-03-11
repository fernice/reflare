/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.font

import org.fernice.flare.style.properties.stylestruct.Font as FontStyle
import org.fernice.flare.style.value.computed.SingleFontFamily
import org.fernice.reflare.internal.SunFontHelper
import org.fernice.reflare.platform.Platform
import java.awt.Font

object FontStyleResolver {

    @JvmStatic
    fun resolve(fontStyle: FontStyle): Font {
        val fontSize = fontStyle.fontSize.size().toPx().toInt()
        val fontWeight = fontStyle.fontWeight.value

        var awtFont: Font? = null

        loop@
        for (fontFamily in fontStyle.fontFamily.values) {
            awtFont = when (fontFamily) {
                is SingleFontFamily.Generic -> {
                    when (fontFamily.name) {
                        "serif" -> Platform.SystemSerifFont
                        "sans-serif" -> Platform.SystemSansSerifFont
                        "monospace" -> Platform.SystemMonospaceFont
                        else -> Platform.SystemSerifFont
                    }
                }
                is SingleFontFamily.FamilyName -> {
                    SunFontHelper.findFont(fontFamily.name.value, fontWeight.toInt(), false) ?: continue@loop
                }
            }
            break
        }

        if (awtFont == null) {
            awtFont = Platform.SystemSerifFont
        }

        return awtFont.deriveFont(fontSize.toFloat())

//        if (awtFont != null) {
//            awtFont = awtFont.deriveFont(fontSize.toFloat())
//            component.font = awtFont
//        }
    }
}