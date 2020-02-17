package org.fernice.reflare.element

import javax.swing.CellRendererPane
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JProgressBar
import javax.swing.JScrollPane
import javax.swing.JViewport
import org.fernice.reflare.render.CellRendererPane as ModernCellRenderPane

class ComboBoxElement(comboBox: JComboBox<*>) : ComponentElement(comboBox) {

    override fun localName(): String {
        return "combobox"
    }
}

class ListElement(list: JList<*>) : ComponentElement(list) {

    override fun localName(): String {
        return "list"
    }
}

class ScrollPaneElement(scrollPane: JScrollPane) : ComponentElement(scrollPane) {

    override fun localName(): String {
        return "scrollpane"
    }
}

class ViewportElement(viewport: JViewport) : ComponentElement(viewport) {

    override fun localName(): String {
        return "viewport"
    }
}

/**
 * This special element allows us to significantly improve the matching performance in for example
 * list cell renderers as they often use a different style for common elements. Through this element
 * we can use the child combinator and reduce this case down to parent traversal.
 */
class CellRendererPaneElement(cellRendererPane: CellRendererPane) : AWTContainerElement(cellRendererPane) {

    override val isVisible: Boolean
        get() = true

    override fun localName(): String {
        return "-flr-renderer"
    }
}

class ModernCellRendererPaneElement(cellRendererPane: ModernCellRenderPane) : AWTContainerElement(cellRendererPane) {

    override val isVisible: Boolean
        get() = true

    override fun localName(): String {
        return "-flr-renderer"
    }
}

class ProgressBarElement(progressBar: JProgressBar) : ComponentElement(progressBar) {

    override fun localName(): String = "progress"
}