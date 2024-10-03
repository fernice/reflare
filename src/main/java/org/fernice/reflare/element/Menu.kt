package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import java.awt.Component
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class MenuBarElement(menuBar: JMenuBar) : ComponentElement(menuBar) {

    override val localName get() = "menubar"
}


open class MenuItemElement(menuItem: JMenuItem) : ButtonElement(menuItem) {

    override val localName get() = "menuitem"
}

class MenuElement(menu: JMenu) : MenuItemElement(menu) {

    override val localName get() = "menu"

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass, component: Component): Boolean {
        return when (pseudoClass) {
            NonTSPseudoClass.Active -> {
                val menu = component as JMenu

                menu.isSelected
            }

            NonTSPseudoClass.Hover -> {
                val menu = component as JMenu

                menu.isSelected && !menu.isTopLevelMenu
            }

            else -> super.matchNonTSPseudoClass(pseudoClass, component)
        }
    }
}
