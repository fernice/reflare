package org.fernice.reflare.ui

import fernice.reflare.SCROLL_PANE_INLINE_PROPERTY
import fernice.reflare.isInline
import fernice.reflare.isMinimalistic
import org.fernice.reflare.element.ScrollPaneElement
import org.fernice.reflare.element.StyleTreeElementLookup.deregisterElement
import org.fernice.reflare.element.StyleTreeElementLookup.registerElement
import org.fernice.reflare.meta.DefinedBy
import org.fernice.reflare.meta.DefinedBy.Api
import org.fernice.reflare.platform.Platform
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.awt.event.InputEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseWheelListener
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.Timer
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicScrollPaneUI

@Suppress("ACCIDENTAL_OVERRIDE")
class FlareScrollPaneUI(scrollPane: JScrollPane) : BasicScrollPaneUI(), FlareUI {
    override val element = ScrollPaneElement(scrollPane)

    override fun installDefaults(scrollPane: JScrollPane) {
        super.installDefaults(scrollPane)

        installDefaultProperties(scrollPane)

        registerElement(scrollPane, this)

        scrollPane.layout = FlareScrollPaneLayout()
    }

    override fun uninstallDefaults(scrollPane: JScrollPane) {
        deregisterElement(scrollPane)

        super.uninstallDefaults(scrollPane)
    }

    override fun installListeners(c: JScrollPane) {
        super.installListeners(c)

        updateScrollBarVisibilitiesHandler()
    }

    override fun uninstallListeners(c: JComponent) {
        verticalScrollBarVisibilityHandler.scrollBar = null
        horizontalScrollBarVisibilityHandler.scrollBar = null

        super.uninstallListeners(c)
    }

    private val verticalScrollBarVisibilityHandler = ScrollBarVisibilityHandler()
    private val horizontalScrollBarVisibilityHandler = ScrollBarVisibilityHandler()

    private fun updateScrollBarVisibilitiesHandler() {
        val inline = scrollpane.isInline

        verticalScrollBarVisibilityHandler.scrollBar = when {
            !inline -> null
            scrollpane.verticalScrollBarPolicy == JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED -> scrollpane.verticalScrollBar
            else -> null
        }
        horizontalScrollBarVisibilityHandler.scrollBar = when {
            !inline -> null
            scrollpane.horizontalScrollBarPolicy == JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED -> scrollpane.horizontalScrollBar
            else -> null
        }
    }

    private inner class ScrollBarVisibilityHandler : AdjustmentListener, MouseListener {

        var scrollBar: JScrollBar? = null
            set(scrollBar) {
                val previousScrollBar = field
                if (scrollBar !== previousScrollBar) {
                    if (previousScrollBar != null) {
                        previousScrollBar.removeAdjustmentListener(this)
                        previousScrollBar.removeMouseListener(this)
                        previousScrollBar.isMinimalistic = Platform.isMac
                    }
                    field = scrollBar
                    if (scrollBar != null) {
                        scrollBar.isMinimalistic = true
                        scrollBar.addAdjustmentListener(this)
                        scrollBar.addMouseListener(this)
                    }
                }
            }

        private val timer = Timer(1500) {
            scrollBar?.putClientProperty("reflare.hidden", true)
            scrollpane.doLayout()
        }

        init {
            timer.isRepeats = false
        }

        private var mouseInside = false
        private var mousePressed = false

        fun requestVisibility() {
            val scrollBar = scrollBar ?: return
            if (!scrollBar.isVisible) {
                scrollBar.putClientProperty("reflare.hidden", false)
                scrollpane.doLayout()
            }
            scheduleHide()
        }

        private fun scheduleHide() {
            if (!mouseInside && !mousePressed) {
                timer.restart()
            }
        }

        private fun cancelHide() {
            timer.stop()
        }

        override fun adjustmentValueChanged(e: AdjustmentEvent) {
            requestVisibility()
        }

        override fun mouseEntered(e: MouseEvent) {
            mouseInside = true
            updateState()
        }

        override fun mouseExited(e: MouseEvent) {
            mouseInside = false
            updateState()
        }

        override fun mousePressed(e: MouseEvent) {
            mousePressed = e.modifiersEx and InputEvent.BUTTON1_DOWN_MASK != 0
            updateState()
        }

        override fun mouseReleased(e: MouseEvent) {
            mousePressed = e.modifiersEx and InputEvent.BUTTON1_DOWN_MASK != 0
            updateState()
        }

        private fun updateState() {
            if (mouseInside || mousePressed) {
                cancelHide()
            } else {
                requestVisibility()
            }
        }

        override fun mouseClicked(e: MouseEvent) {}
    }

    override fun createPropertyChangeListener(): PropertyChangeListener {
        val propertyChangeListener = super.createPropertyChangeListener()
        return PropertyChangeListener { event ->
            when (event.propertyName) {
                SCROLL_PANE_INLINE_PROPERTY,
                "verticalScrollBar", "horizontalScrollBar",
                "verticalScrollBarPolicy", "horizontalScrollBarPolicy",
                -> updateScrollBarVisibilitiesHandler()
            }

            propertyChangeListener.propertyChange(event)
        }
    }

    override fun createMouseWheelListener(): MouseWheelListener {
        val mouseWheelListener = super.createMouseWheelListener()
        return MouseWheelListener { event ->
            var handler: ScrollBarVisibilityHandler? = verticalScrollBarVisibilityHandler

            if (handler?.scrollBar == null || event.isShiftDown) {
                handler = horizontalScrollBarVisibilityHandler
                if (handler.scrollBar == null) {
                    handler = null
                }
            }

            handler?.requestVisibility()

            mouseWheelListener.mouseWheelMoved(event)
        }
    }

    override fun getMinimumSize(c: JComponent): Dimension? {
        element.pulseForComputation()
        return super.getMinimumSize(c)
    }

    override fun getPreferredSize(c: JComponent): Dimension? {
        element.pulseForComputation()
        return super.getPreferredSize(c)
    }

    override fun getMaximumSize(c: JComponent): Dimension? {
        element.pulseForComputation()
        return super.getMaximumSize(c)
    }

    override fun paint(graphics: Graphics, component: JComponent) {
        element.paintBackground(graphics)
    }

    override fun paintBorder(c: Component, g: Graphics) {
        element.paintBorder(g)
    }

    companion object {
        @DefinedBy(Api.LOOK_AND_FEEL)
        @JvmStatic
        fun createUI(c: JComponent): ComponentUI {
            return FlareScrollPaneUI(c as JScrollPane)
        }
    }
}