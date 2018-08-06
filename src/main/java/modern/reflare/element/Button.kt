package modern.reflare.element

import de.krall.flare.selector.NonTSPseudoClass
import javax.swing.AbstractButton

class ButtonElement(button: AbstractButton) : ComponentElement(button) {

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
            else -> super.matchNonTSPseudoClass(pseudoClass)
        }
    }

    override fun localName(): String {
        return "button"
    }
}