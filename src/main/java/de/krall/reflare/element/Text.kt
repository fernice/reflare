package de.krall.reflare.element

import javax.swing.JTextField
import javax.swing.text.JTextComponent

abstract class TextElement(textComponent: JTextComponent) : ComponentElement(textComponent)

class TextFieldElement(textField: JTextField) : TextElement(textField) {
    override fun localName(): String {
        return "textfield"
    }
}
