package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import org.fernice.flare.EngineContext
import org.fernice.flare.dom.Device
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.reflare.trace.CountingTrace
import org.fernice.reflare.trace.Trace
import java.awt.Component
import java.awt.Window
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean

private val frames: MutableMap<Window, Frame> = WeakHashMap()

val Window.frame: Frame
    get() = frames[this] ?: Frame(this)

class Frame(private val frame: Window) : Device {

    private val cssEngine = CSSEngine.createEngine(this)

    var root: AWTComponentElement? = null
        private set

    private val dirtyElements = mutableListOf<AWTComponentElement>()
    private val pulseRequested = AtomicBoolean(false)

    init {
        frames[frame] = this

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

        childElement.traceReapplyOrigin("frame:added")
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
        set(size) {
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

    private fun doCSSPass() {
        val root = root

        if (root != null && root.cssFlag != StyleState.CLEAN) {
            val engineContext = cssEngine.createEngineContext()
            val context = TracingContext(engineContext, CountingTrace(count++))

            context.traceBeginPass()

            try {
                root.clearDirty(DirtyBits.NODE_CSS)
                root.processCSS(context)
            } finally {
                context.traceEndPass()
            }
        }
    }

    internal fun pulse() {
        if (pulseRequested.getAndSet(false)) {
            try {
                doCSSPass()
            } finally {
                dirtyElements.clear()
            }
        }
    }

    internal fun requestNextPulse(component: AWTComponentElement) {
        if (!pulseRequested.getAndSet(true)) {
            component.component.repaint()
        }
    }

    fun matchStyle(element: AWTComponentElement): MatchingResult {
        return cssEngine.matchStyles(element)
    }
}

class TracingContext(
    delegate: EngineContext,
    val trace: Trace
) : EngineContext by delegate

private fun EngineContext.trace(): Trace? {
    return (this as? TracingContext)?.trace
}

internal fun EngineContext.traceBeginPass() {
    trace()?.beginCSSPass()
}

internal fun EngineContext.traceElement(element: AWTComponentElement) {
    trace()?.traceRestyledElement(element)
}

internal fun EngineContext.traceEndPass() {
    trace()?.endCSSPass()
}