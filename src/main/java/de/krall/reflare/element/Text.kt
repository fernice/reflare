package de.krall.reflare.element

import de.krall.flare.std.None
import de.krall.flare.std.Some
import de.krall.reflare.render.RenderCacheStrategy
import de.krall.reflare.toAWTColor
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.text.JTextComponent

class LabelElement(label: JLabel): ComponentElement(label){

    override fun localName(): String {
        return "label"
    }
}

abstract class TextElement(textComponent: JTextComponent) : ComponentElement(textComponent) {

    init {
        renderCacheStrategy = RenderCacheStrategy.CacheAll()
    }

    override fun reapplyFont() {
        super.reapplyFont()

        val component = component as JTextComponent


        val style = getStyle()

        val values = when (style) {
            is Some -> style.value
            is None -> return
        }

        component.caretColor = values.color.color.toAWTColor()
    }
}

class TextFieldElement(textField: JTextField) : TextElement(textField) {
    override fun localName(): String {
        return "textfield"
    }
}
