package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.render.icon.ColorAndBackground
import org.fernice.reflare.render.icon.setIcon
import org.fernice.reflare.util.Observables
import javax.swing.AbstractButton
import javax.swing.ButtonModel
import javax.swing.JCheckBox
import javax.swing.JRadioButton
import javax.swing.JToggleButton
import javax.swing.event.ChangeListener

open class ButtonElement(button: AbstractButton) : ComponentElement(button) {

    init {
        modelChanged(null, button.model)

        button.addPropertyChangeListener("model") { modelChanged(it.oldValue as ButtonModel?, it.newValue as ButtonModel?) }
    }

    private val modelChangeListener = ChangeListener {
        reapplyCSS(origin = "active")
    }

    private fun modelChanged(old: ButtonModel?, new: ButtonModel?) {
        old?.removeChangeListener(modelChangeListener)
        new?.addChangeListener(modelChangeListener)
    }

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            is NonTSPseudoClass.Active -> {
                val button = component as AbstractButton

                button.model.isArmed || active
            }
            is NonTSPseudoClass.Checked -> {
                val button = component as AbstractButton

                button.isSelected
            }
            else -> super.matchNonTSPseudoClass(pseudoClass)
        }
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            is PseudoElement.Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    private var iconStyle by Observables.observable(ColorAndBackground.Initial) { _, _, iconStyle ->
        val component = component as AbstractButton

        component.setIcon(iconStyle) {
            component.setIcon(iconStyle)
        }
    }

    override fun updateStyle(style: ComputedValues) {
        super.updateStyle(style)

        iconStyle
    }

    override fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {
        when (pseudoElement) {
            is PseudoElement.Icon -> {
                iconStyle = ColorAndBackground.from(style)
            }
            else -> super.updatePseudoElement(pseudoElement, style)
        }
    }

    override fun localName(): String {
        return "button"
    }
}

open class ToggleButtonElement(toggleButton: JToggleButton) : ButtonElement(toggleButton) {

    override fun localName(): String {
        return "toggle"
    }
}

class RadioButtonElement(radioButton: JRadioButton) : ToggleButtonElement(radioButton) {

    override fun localName(): String {
        return "radio"
    }
}

class CheckBoxElement(checkbox: JCheckBox) : ToggleButtonElement(checkbox) {

    override fun localName(): String {
        return "checkbox"
    }
}