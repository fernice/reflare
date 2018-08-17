package modern.reflare.element

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class MenuBarElement(menuBar: JMenuBar) : ComponentElement(menuBar) {

    override fun localName(): String {
        return "menubar"
    }
}


open class MenuItemElement(menuItem: JMenuItem) : ButtonElement(menuItem) {

    override fun localName(): String {
        return "menuitem"
    }
}

class MenuElement(menu: JMenu) : MenuItemElement(menu) {

    override fun localName(): String {
        return "menu"
    }
}
