package org.fernice.reflare.element

import javax.swing.JPopupMenu

open class PopupMenuElement(popupMenu: JPopupMenu) : ComponentElement(popupMenu) {

//    override val isVisible: Boolean
//        get() = true

    override val localName get() = "popup"
}

class ComboBoxPopupElement(popupMenu: org.fernice.reflare.ui.FlareComboBoxPopup) : PopupMenuElement(popupMenu) {
//
//    override val isVisible: Boolean
//        get() = true

    override val localName get() = "combobox-popup"
}