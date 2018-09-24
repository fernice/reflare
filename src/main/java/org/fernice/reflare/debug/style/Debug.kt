/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.debug.style

import fernice.reflare.CSSEngine
import fernice.reflare.classes
import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import org.fernice.flare.std.First
import org.fernice.flare.std.Second
import org.fernice.flare.style.ruletree.StyleSource
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.element.into
import org.fernice.reflare.layout.VerticalLayout
import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dialog
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import java.util.Arrays
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.WindowConstants

object DebugHelper : JPanel() {
    private val frame: JFrame by lazy {
        val frame = JFrame("Debug Helper")
        frame.modalExclusionType = Dialog.ModalExclusionType.TOOLKIT_EXCLUDE
        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.setSize(300, 800)

        val debugHelper = DebugStylePanel()

        frame.contentPane = debugHelper

        CSSEngine.addStylesheetResource("/debug.css")

        frame
    }

    fun open() {
        frame.isVisible = true
    }

    fun close() {
        frame.isVisible = false
    }
}

private val RELEVANT_EVENTS = Arrays.asList(MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_RELEASED)

class DebugStylePanel : JPanel() {

    private val pickButton: JButton
    private val matchingStylesPanel: MatchingStylesPanel

    private var picking: Boolean = false

    init {
        layout = BorderLayout()
        classes.add("dbg-style-panel")

        pickButton = JButton("Pick Component")
        pickButton.classes.add("dbg-button")
        pickButton.addActionListener {
            picking = true
            pickButton.isEnabled = false
            pickButton.text = "Picking..."
        }
        add(pickButton, BorderLayout.NORTH)

        matchingStylesPanel = MatchingStylesPanel()
        add(matchingStylesPanel, BorderLayout.CENTER)
    }

    private val mouseEventListener = AWTEventListener { event ->
        if (picking) {
            val mouseEvent = event as MouseEvent

            if (!RELEVANT_EVENTS.contains(mouseEvent.id))
                return@AWTEventListener

            if (mouseEvent.id == MouseEvent.MOUSE_PRESSED) {
                val component = mouseEvent.component as Container

                val view = component.findComponentAt(mouseEvent.point)

                matchingStylesPanel.element = Some(view.into())

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

    var element: Option<AWTComponentElement> = None
        set(value) {
            field = value
            update()
        }

    private fun update() {
        removeAll()

        val elementOption = element
        if (elementOption is Some) {
            val element = elementOption.value

            val result = element.getMatchingStyles()

            for (node in result.ruleNode.selfAndAncestors()) {
                val source = node.source
                if (source is Some) {
                    add(StylesPanel(source.value))
                }
            }
        }
    }
}

private class StylesPanel(source: StyleSource) : JPanel() {

    init {
        layout = VerticalLayout()
        classes.add("dbg-styles")

        val either = source.source
        val (selectors, declarations) = when (either) {
            is First -> Some(either.value.selectors) to either.value.declarations
            is Second -> None to either.value
        }

        val selectorLabel = JLabel()
        selectorLabel.classes.add("dbg-selector")
        add(selectorLabel)

        if (selectors is Some) {
            selectorLabel.text = (selectors.value.toCssString() + " {") .wrappable()
        } else {
            selectorLabel.classes.add("dbg-selector-none")
            selectorLabel.text = "element.style"
        }

        for ((declaration, importance) in declarations.declarationImportanceIter()) {
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