package modern.reflare.element

import modern.reflare.ui.FlareComboBoxPopup
import javax.swing.JPopupMenu

open class PopupMenuElement(popupMenu: JPopupMenu) : ComponentElement(popupMenu) {

    override fun localName(): String {
        return "popup"
    }
}

class ComboBoxPopupElement(popupMenu: FlareComboBoxPopup) : PopupMenuElement(popupMenu) {

    override fun localName(): String {
        return "combo-box-popup"
    }
}