package de.krall.reflare.element

import de.krall.flare.style.ComputedValues
import de.krall.reflare.render.RenderCacheStrategy
import de.krall.reflare.toAWTColor
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.text.JTextComponent

class LabelElement(label: JLabel) : ComponentElement(label) {

    override fun localName(): String {
        return "label"
    }
}

abstract class TextElement(textComponent: JTextComponent) : ComponentElement(textComponent) {

    init {
        renderCacheStrategy = RenderCacheStrategy.CacheAll()
    }

    override fun updateStyle(style: ComputedValues) {
        val component = component as JTextComponent

        component.caretColor = style.color.color.toAWTColor()
    }
}

open class TextFieldElement(textField: JTextField) : TextElement(textField) {
    override fun localName(): String {
        return "text"
    }
}

class FormattedTextFieldElement(textField: JFormattedTextField) : TextFieldElement(textField) {
    override fun localName(): String {
        return "formatted"
    }
}

class PasswordFieldElement(textField: JPasswordField) : TextFieldElement(textField) {
    override fun localName(): String {
        return "password"
    }
}

class TextAreaElement(textArea: JTextArea) : TextElement(textArea) {
    override fun localName(): String {
        return "textarea"
    }
}
