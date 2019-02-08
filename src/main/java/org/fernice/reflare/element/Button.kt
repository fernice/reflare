package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.render.icon.IconPseudoElementHelper
import javax.swing.AbstractButton
import javax.swing.JCheckBox
import javax.swing.JRadioButton
import javax.swing.JToggleButton
import javax.swing.plaf.UIResource

open class ButtonElement(button: AbstractButton) : ComponentElement(button) {

    init {
        button.model.addChangeListener {
            invalidateStyle()
        }
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

    override fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {
        when (pseudoElement) {
            is PseudoElement.Icon -> {
                val component = component as AbstractButton
                val icon = IconPseudoElementHelper.getIcon(style) { restyle() }

                // prevent manually set icons from being overridden if no
                // icon is specified via css
                if (icon != null || component.icon is UIResource) {
                    component.icon = icon
                }
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
        return "toggle-button"
    }
}

class RadioButtonElement(radioButton: JRadioButton) : ToggleButtonElement(radioButton) {

    override fun localName(): String {
        return "radio-button"
    }
}

class CheckBoxElement(checkbox: JCheckBox) : ToggleButtonElement(checkbox) {

    override fun localName(): String {
        return "checkbox"
    }
}