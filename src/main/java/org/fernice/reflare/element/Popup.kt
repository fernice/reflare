package org.fernice.reflare.element

import org.fernice.reflare.ui.FlareComboBoxPopup
import javax.swing.JPopupMenu

open class PopupMenuElement(popupMenu: JPopupMenu) : ComponentElement(popupMenu) {

    override fun localName(): String {
        return "popup"
    }
}

class ComboBoxPopupElement(popupMenu: org.fernice.reflare.ui.FlareComboBoxPopup) : PopupMenuElement(popupMenu) {

    override fun localName(): String {
        return "combo-box-popup"
    }
}