package de.krall.reflare.element

import javax.swing.JPopupMenu

class PopupMenuElement(popupMenu: JPopupMenu) : ComponentElement(popupMenu) {

    override fun localName(): String {
        return "popup"
    }
}