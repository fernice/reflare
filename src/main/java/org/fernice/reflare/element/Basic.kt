package org.fernice.reflare.element

import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.JRootPane
import javax.swing.JTabbedPane

class RootPaneElement(rootPane: JRootPane) : ComponentElement(rootPane) {
    override val localName get() = "root"
}

class LayeredPaneElement(layeredPane: JLayeredPane) : ComponentElement(layeredPane) {
    override val localName get() = "layeredpane"
}

class PanelElement(panel: JPanel) : ComponentElement(panel) {
    override val localName get() = "panel"
}

class TabbedPaneElement(tabbedPane: JTabbedPane) : ComponentElement(tabbedPane) {
    override val localName get() = "tabbed"
}