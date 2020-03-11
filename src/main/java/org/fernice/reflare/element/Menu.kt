package org.fernice.reflare.element

import org.fernice.flare.selector.NonTSPseudoClass
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class MenuBarElement(menuBar: JMenuBar) : ComponentElement(menuBar) {

    override val localName = "menubar"
}


open class MenuItemElement(menuItem: JMenuItem) : ButtonElement(menuItem) {

    override val localName = "menuitem"
}

class MenuElement(menu: JMenu) : MenuItemElement(menu) {

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            NonTSPseudoClass.Active -> {
                val menu = component as JMenu

                menu.isSelected
            }
            NonTSPseudoClass.Hover -> {
                val menu = component as JMenu

                menu.isSelected && !menu.isTopLevelMenu
            }
            else -> super.matchNonTSPseudoClass(pseudoClass)
        }
    }

    override val localName = "menu"
}
