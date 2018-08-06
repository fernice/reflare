package modern.reflare.element

import de.krall.flare.selector.NonTSPseudoClass
import javax.swing.AbstractButton
import javax.swing.JCheckBox
import javax.swing.JRadioButton
import javax.swing.JToggleButton

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

                button.model.isArmed || active || button.isSelected
            }
            else -> super.matchNonTSPseudoClass(pseudoClass)
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