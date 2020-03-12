package org.fernice.reflare.element

import javax.swing.CellRendererPane
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JProgressBar
import javax.swing.JScrollPane
import javax.swing.JViewport
import org.fernice.reflare.render.CellRendererPane as ModernCellRenderPane

class ComboBoxElement(comboBox: JComboBox<*>) : ComponentElement(comboBox) {

    override val localName get() = "combobox"
}

class ListElement(list: JList<*>) : ComponentElement(list) {

    override val localName get() = "list"
}

class ScrollPaneElement(scrollPane: JScrollPane) : ComponentElement(scrollPane) {

    override val localName get() = "scrollpane"
}

class ViewportElement(viewport: JViewport) : ComponentElement(viewport) {

    override val localName get() = "viewport"
}

/**
 * This special element allows us to significantly improve the matching performance in for example
 * list cell renderers as they often use a different style for common elements. Through this element
 * we can use the child combinator and reduce this case down to parent traversal.
 */
class CellRendererPaneElement(cellRendererPane: CellRendererPane) : AWTContainerElement(cellRendererPane) {

    override val isVisible: Boolean
        get() = true

    override val localName get() = "-flr-renderer"
}

class ModernCellRendererPaneElement(cellRendererPane: ModernCellRenderPane) : AWTContainerElement(cellRendererPane) {

    override val isVisible: Boolean
        get() = true

    override val localName get() = "-flr-renderer"
}

class ProgressBarElement(progressBar: JProgressBar) : ComponentElement(progressBar) {

    override val localName get() = "progress"
}