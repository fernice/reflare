package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import java.awt.Component
import javax.swing.AbstractButton
import javax.swing.ButtonModel
import javax.swing.JCheckBox
import javax.swing.JRadioButton
import javax.swing.JToggleButton
import javax.swing.event.ChangeListener

open class ButtonElement(button: AbstractButton) : ComponentElement(button) {

    private val modelChangeListener = ChangeListener {
        reapplyCSS(origin = "active")
    }

    init {
        modelChanged(null, button.model)

        button.addPropertyChangeListener("model") { modelChanged(it.oldValue as ButtonModel?, it.newValue as ButtonModel?) }
    }

    private fun modelChanged(old: ButtonModel?, new: ButtonModel?) {
        old?.removeChangeListener(modelChangeListener)
        new?.addChangeListener(modelChangeListener)
    }

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass, component: Component): Boolean {
        return when (pseudoClass) {
            NonTSPseudoClass.Active -> {
                val button = component as AbstractButton

                button.model.isArmed || isHinted(NonTSPseudoClass.Active)
            }

            NonTSPseudoClass.Checked -> {
                val button = component as AbstractButton

                button.isSelected
            }

            else -> super.matchNonTSPseudoClass(pseudoClass, component)
        }
    }

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            PseudoElement.Flare_Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return when (pseudoElement) {
            PseudoElement.Flare_Icon -> true
            else -> super.matchPseudoElement(pseudoElement)
        }
    }

    override val localName get() = "button"
}

open class ToggleButtonElement(toggleButton: JToggleButton) : ButtonElement(toggleButton) {

    override val localName get() = "toggle"
}

class RadioButtonElement(radioButton: JRadioButton) : ToggleButtonElement(radioButton) {

    override val localName get() = "radio"
}

class CheckBoxElement(checkbox: JCheckBox) : ToggleButtonElement(checkbox) {

    override val localName get() = "checkbox"
}