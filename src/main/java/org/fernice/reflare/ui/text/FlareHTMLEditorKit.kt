/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui.text

import org.fernice.reflare.internal.SunFontHelper
import java.awt.Font
import javax.swing.text.AttributeSet
import javax.swing.text.Document
import javax.swing.text.StyleConstants
import javax.swing.text.html.CSS
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet

class FlareHTMLEditorKit : HTMLEditorKit() {

    override fun createDefaultDocument(): Document {
        val styles = styleSheet
        val ss = StyleSheet()

        ss.addStyleSheet(styles)

        val doc = FlareHTMLDocument(ss)
        doc.parser = parser
        doc.asynchronousLoadPriority = 4
        doc.tokenThreshold = 100
        return doc
    }

    class FlareHTMLDocument(styleSheet: StyleSheet) : HTMLDocument(styleSheet) {

        override fun getFont(attr: AttributeSet): Font? {
            val fontFamily = StyleConstants.getFontFamily(attr)
            val fontWeight = getFontWeight(attr)
            val fontItalic = StyleConstants.isItalic(attr)
            val font = SunFontHelper.findFont(fontFamily, fontWeight, fontItalic)
            if (font != null) {
                val fontSize = StyleConstants.getFontSize(attr)
                return font.deriveFont(fontSize.toFloat())
            }
            return super.getFont(attr)
        }

        private fun getFontWeight(attr: AttributeSet): Int {
            val fontWeight = attr.getAttribute(CSS.Attribute.FONT_WEIGHT)
            if (fontWeight != null) {
                return when (val value = fontWeight.toString()) {
                    "bold" -> 700
                    "normal" -> 400
                    else -> try {
                        value.toInt()
                    } catch (nfe: NumberFormatException) {
                        400
                    }
                }
            }
            return 400
        }
    }
}