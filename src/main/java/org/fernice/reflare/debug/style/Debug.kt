/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.debug.style

import fernice.reflare.CSSEngine
import fernice.reflare.Stylesheet
import fernice.reflare.addAll
import fernice.reflare.classes
import org.fernice.flare.cssparser.toCssString
import org.fernice.flare.style.ruletree.CascadeLevel
import org.fernice.flare.style.ruletree.printTree
import org.fernice.flare.style.source.StyleAttribute
import org.fernice.flare.style.source.StyleRule
import org.fernice.flare.style.source.StyleSource
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.element.ElementRestyleListener
import org.fernice.reflare.element.element
import org.fernice.reflare.layout.VerticalLayout
import java.awt.*
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import javax.swing.*

object DebugHelper : JPanel() {
    private val frame: JFrame by lazy {
        val frame = JFrame("Debug Helper")
        frame.modalExclusionType = Dialog.ModalExclusionType.TOOLKIT_EXCLUDE
        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.setSize(300, 800)

        val debugHelper = DebugStylePanel()

        frame.contentPane = debugHelper

        CSSEngine.addStylesheet(Stylesheet.Companion.fromResource("/reflare/style/debug/debug.css"))

        frame
    }

    fun open() {
        frame.isVisible = true
    }

    fun close() {
        frame.isVisible = false
    }
}

private val RELEVANT_EVENTS = listOf(MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_RELEASED)

class DebugStylePanel : JPanel() {

    private val pickButton: JButton
    private val matchingStylesPanel: MatchingStylesPanel

    private var picking: Boolean = false

    init {
        layout = BorderLayout()
        classes.add("dbg-style-panel")

        val actionPanel = JPanel(BorderLayout())
        actionPanel.classes.add("dbg-style-actions")
        add(actionPanel, BorderLayout.NORTH)

        pickButton = JButton("Pick Component")
        pickButton.classes.addAll("dbg-button", "dbg-button-imp")
        pickButton.addActionListener {
            picking = true
            pickButton.isEnabled = false
            pickButton.text = "Picking..."
        }
        actionPanel.add(pickButton, BorderLayout.WEST)

        matchingStylesPanel = MatchingStylesPanel()

        val scrollPane = JScrollPane(matchingStylesPanel)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBar.unitIncrement = 16
        add(scrollPane, BorderLayout.CENTER)
    }

    private val mouseEventListener = AWTEventListener { event ->
        if (picking) {
            val mouseEvent = event as MouseEvent

            if (!RELEVANT_EVENTS.contains(mouseEvent.id))
                return@AWTEventListener

            if (mouseEvent.id == MouseEvent.MOUSE_PRESSED) {
                val component = mouseEvent.component as Container

                val view = component.findComponentAt(mouseEvent.point)

                matchingStylesPanel.element = view.element

                mouseEvent.consume()
                picking = false

                pickButton.isEnabled = true
                pickButton.text = "Pick Component"
            }
        }
    }

    init {
        Toolkit.getDefaultToolkit().addAWTEventListener(mouseEventListener, AWTEvent.MOUSE_EVENT_MASK)
    }
}

private class MatchingStylesPanel : JPanel() {

    init {
        layout = VerticalLayout()
        classes.add("dbg-matching")
    }

    val restyleListener = ElementRestyleListener{ update() }

    var element: AWTComponentElement? = null
        set(value) {
            val previous = field
            previous?.removeElementRestyleListener(restyleListener)

            field = value

            value?.addElementRestyleListener(restyleListener)

            update()
        }


    private fun update() {
        removeAll()

        val element = element
        if (element != null) {
            val result = element.getMatchingStyles()

            result.ruleNode.printTree()

            var count = 1
            for (node in result.ruleNode.selfAndAncestors()) {
                val declarations = node.source?.get()
                if (declarations != null) {
                    println("$count $node")
                    add(StylesPanel(declarations, node.priority.level))
                }
                count++
            }
        }

        revalidate()
        repaint()
    }
}

private class StylesPanel(source: StyleSource, level: CascadeLevel) : JPanel() {

    init {
        layout = VerticalLayout()
        classes.add("dbg-styles")

        val (selectors, declarations) = when (source) {
            is StyleRule -> source.selectors to source.declarations
            is StyleAttribute -> null to source.declarations
        }

        val headerPanel = JPanel(BorderLayout())
        add(headerPanel)

        val selectorLabel = JLabel()
        selectorLabel.classes.add("dbg-selector")
        headerPanel.add(selectorLabel, BorderLayout.CENTER)

        val levelLabel = JLabel()
        levelLabel.text = level.name
        levelLabel.classes.add("dbg-cascade-level")
        headerPanel.add(levelLabel, BorderLayout.EAST)

        if (selectors != null) {
            selectorLabel.text = (selectors.toCssString() + " {").wrappable()
        } else {
            selectorLabel.classes.add("dbg-selector-none")
            selectorLabel.text = "element.style"
        }

        for (declaration in declarations.asSequence()) {
            val declarationLabel = JLabel()
            declarationLabel.text = declaration.toCssString().wrappable()
            declarationLabel.classes.add("dbg-declaration")
            add(declarationLabel)
        }

        val declarationLabel = JLabel()
        declarationLabel.text = "}"
        declarationLabel.classes.add("dbg-selector")
        add(declarationLabel)
    }
}

private fun String.wrappable(): String {
    return "<html>$this</html>"
}