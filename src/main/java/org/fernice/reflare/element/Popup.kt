package org.fernice.reflare.element

import javax.swing.JPopupMenu

open class PopupMenuElement(popupMenu: JPopupMenu) : ComponentElement(popupMenu) {

    override fun localName(): String {
        return "popup"
    }
}

class ComboBoxPopupElement(popupMenu: org.fernice.reflare.ui.FlareComboBoxPopup) : PopupMenuElement(popupMenu) {

    override fun localName(): String {
        return "combobox-popup"
    }
}