package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import org.fernice.flare.dom.Device
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import java.awt.Component
import java.awt.Window
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.timer

private val frames: MutableMap<Window, Frame> = WeakHashMap()

val Window.frame: Frame
    get() = frames.getOrPut(this) { Frame(this) }

class Frame(private val frame: Window) : Device {

    private val cssEngine = CSSEngine.createEngine(this)

    var root: AWTComponentElement? = null
        private set

    private val dirtyElements = mutableListOf<AWTComponentElement>()
    private val pulseRequested = AtomicBoolean(false)

    init {
        frame.addContainerListener(object : ContainerListener {
            override fun componentAdded(e: ContainerEvent) {
                childAdded(e.child)
            }

            override fun componentRemoved(e: ContainerEvent) {
                childRemoved(e.child)
            }
        })

        for (child in frame.components) {
            childAdded(child)
        }

        //PulseListener
    }

    private fun childAdded(child: Component) {
        if (root != null) {
            System.err.println("= ERROR ==========================")
            System.err.println(" A SECOND CHILD WAS ADDED TO THE ")
            System.err.println(" FRAME. PLEASE REPORT THIS BUG.")
        }

        val childElement = child.element

        childElement.frame = this
        root = childElement

        childElement.reapplyCSS()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.element

        childElement.parent = null
        root = null
    }

    override fun viewportSize(): Size2D<Au> {
        return Size2D(Au.fromPx(frame.width), Au.fromPx(frame.height))
    }

    var fontSize: Au = Au.fromPx(16)
        set (size) {
            field = size.min(Au.fromPx(1))
        }

    override fun rootFontSize(): Au {
        return fontSize
    }

    override fun setRootFontSize(size: Au) {
        fontSize = size
    }

    override fun invalidate() {
        root?.reapplyCSS()

        frame.revalidate()
        frame.repaint()
    }

    internal fun markElementDirty(element: AWTComponentElement) {
        dirtyElements.add(element)
    }

    private var count: Int = 0

    internal fun doCSSPass() {
        val root = root

        if (root != null && root.cssFlag != StyleState.CLEAN) {
            val context = cssEngine.createEngineContext()

            root.clearDirty(DirtyBits.NODE_CSS)
            root.processCSS(context)

            println("css pass ${count++}")
        }
    }

    internal fun pulse() {
        try {
            doCSSPass()
        } finally {
            dirtyElements.clear()
            pulseRequested.set(false)
        }
    }

    internal fun requestNextPulse(component: AWTComponentElement) {
        if (!pulseRequested.getAndSet(false)) {
            component.component.repaint()
        }
    }

    fun matchStyle(element: AWTComponentElement): MatchingResult {
        return cssEngine.matchStyles(element)
    }
}

object PulseListener {

    fun init() {}

    private val nextPulse = AtomicBoolean(true)

    fun requestNextPulse() {
        nextPulse.set(true)
    }

    init {

        /*
        timer("timer-01", period = 16) {
            if (nextPulse.getAndSet(false)) {
                frames.values.forEach { frame -> frame.pulse() }
            }
        }
        */

        /*
        Toolkit.getDefaultToolkit().addAWTEventListener(
            {
                frames.values.forEach { frame -> frame.pulse() }
            },
            AWTEvent.INVOCATION_EVENT_MASK
        )
        */
    }
}