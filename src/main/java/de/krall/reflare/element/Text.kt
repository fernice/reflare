package de.krall.reflare.element

import de.krall.flare.selector.NonTSPseudoClass
import de.krall.flare.std.None
import de.krall.flare.std.Some
import de.krall.reflare.render.RenderCacheStrategy
import de.krall.reflare.toAWTColor
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.text.JTextComponent

class LabelElement(label: JLabel) : ComponentElement(label) {

    fun hoverHint(hover: Boolean): Boolean {
        val old = this.hover
        this.hover = hover

        if (old != hover) {
            invalidateStyle()
        }

        return old
    }

    fun focusHint(focus: Boolean): Boolean {
        val old = this.focus
        this.focus = focus

        if (old != focus) {
            invalidateStyle()
        }

        return old
    }

    private var active: Boolean = false

    fun activeHint(active: Boolean): Boolean {
        val old = this.active
        this.active = active

        if (old != active) {
            invalidateStyle()
        }

        return old
    }

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            is NonTSPseudoClass.Active -> active
            else -> super.matchNonTSPseudoClass(pseudoClass)
        }
    }

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
        return "text"
    }
}

class PasswordFieldElement(textField: JTextField) : TextElement(textField) {
    override fun localName(): String {
        return "password"
    }
}

class TextAreaElement(textArea: JTextArea) : TextElement(textArea) {
    override fun localName(): String {
        return "textarea"
    }
}
