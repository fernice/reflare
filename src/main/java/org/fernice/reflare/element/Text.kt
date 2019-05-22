package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.render.icon.ColorAndBackground
import org.fernice.reflare.render.icon.setIcon
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.util.Observables
import javax.swing.JEditorPane
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.JTextPane
import javax.swing.text.JTextComponent

class LabelElement(label: JLabel) : ComponentElement(label) {

    override fun localName(): String {
        return "label"
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {
        when (pseudoElement) {
            is PseudoElement.Icon -> {
                iconStyle = ColorAndBackground.from(style)
            }
            else -> super.updatePseudoElement(pseudoElement, style)
        }
    }

    private var iconStyle by Observables.observable(ColorAndBackground.Initial) { _, _, iconStyle ->
        val component = component as JLabel

        component.setIcon(iconStyle) {
            component.setIcon(iconStyle)
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

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Selection -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun updateStyle(style: ComputedValues) {
        val component = component as JTextComponent

        component.caretColor = style.color.color.toAWTColor()
    }

    override fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {
        super.updatePseudoElement(pseudoElement, style)

        val component = component as JTextComponent

        when (pseudoElement) {
            is PseudoElement.Selection -> {
                component.selectedTextColor = style.color.color.toAWTColor()
                component.selectionColor = style.background.color.toAWTColor()
            }
            else -> {
            }
        }
    }
}

open class TextFieldElement(textField: JTextField) : TextElement(textField) {
    override fun localName(): String {
        return "text"
    }
}

class FormattedTextFieldElement(textField: JFormattedTextField) : TextFieldElement(textField)

class PasswordFieldElement(textField: JPasswordField) : TextFieldElement(textField)

class TextAreaElement(textArea: JTextArea) : TextElement(textArea) {
    override fun localName(): String {
        return "textarea"
    }
}

open class EditorPaneElement(editorPane: JEditorPane) : TextElement(editorPane) {

    override fun localName(): String {
        return "text"
    }
}

open class TextPaneElement(textPane: JTextPane) : EditorPaneElement(textPane) {

    override fun localName(): String {
        return "textpane"
    }
}