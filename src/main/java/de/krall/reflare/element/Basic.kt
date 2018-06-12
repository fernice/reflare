package de.krall.reflare.element

import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.JRootPane
import javax.swing.JTabbedPane

class RootPaneElement(rootPane: JRootPane) : ComponentElement(rootPane) {
    override fun localName(): String {
        return "root"
    }
}

class LayeredPaneElement(layeredPane: JLayeredPane) : ComponentElement(layeredPane) {
    override fun localName(): String {
        return "layeredpane"
    }
}

class PanelElement(panel: JPanel) : ComponentElement(panel) {
    override fun localName(): String {
        return "panel"
    }
}

class TabbedPaneElement(tabbedPane: JTabbedPane) : ComponentElement(tabbedPane) {
    override fun localName(): String {
        return "tabbed"
    }
}