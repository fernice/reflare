package de.krall.reflare.element

import javax.swing.AbstractButton

class ButtonElement(button: AbstractButton) : ComponentElement(button) {

    override fun localName(): String {
        return "button"
    }
}