package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.toAWTColor
import javax.swing.JEditorPane
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.JTextPane
import javax.swing.text.JTextComponent

class LabelElement(label: JLabel) : ComponentElement(label) {

    override val localName get() = "label"

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }
}

abstract class TextElement(textComponent: JTextComponent) : ComponentElement(textComponent) {

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            is NonTSPseudoClass.ReadWrite -> (component as JTextComponent).isEditable
            is NonTSPseudoClass.ReadOnly -> !(component as JTextComponent).isEditable
            else -> super.matchNonTSPseudoClass(pseudoClass)
        }
    }

    override fun updateStyle(style: ComputedValues) {
        val component = component as JTextComponent

        component.caretColor = style.color.color.toAWTColor()
    }

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Selection -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Selection -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {
        when (pseudoElement) {
            is PseudoElement.Selection -> {
                val component = component as JTextComponent

                component.selectedTextColor = style.color.color.toAWTColor()
                component.selectionColor = style.background.color.toAWTColor()
            }

            else -> super.updatePseudoElement(pseudoElement, style)
        }
    }
}

open class TextFieldElement(textField: JTextField) : TextElement(textField) {
    override val localName get() = "text"
}

class FormattedTextFieldElement(textField: JFormattedTextField) : TextFieldElement(textField)

class PasswordFieldElement(textField: JPasswordField) : TextFieldElement(textField)

class TextAreaElement(textArea: JTextArea) : TextElement(textArea) {
    override val localName get() = "textarea"
}

open class EditorPaneElement(editorPane: JEditorPane) : TextElement(editorPane) {

    override val localName get() = "text"
}

open class TextPaneElement(textPane: JTextPane) : EditorPaneElement(textPane) {

    override val localName get() = "textpane"
}