package org.fernice.reflare.element

import javax.swing.JSeparator

class SeparatorElement(separator: JSeparator) : ComponentElement(separator) {

    override val localName get() = "separator"
}
