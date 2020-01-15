package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import org.fernice.flare.dom.Device
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.reflare.trace.trace
import org.fernice.reflare.trace.traceRoot
import org.fernice.reflare.util.ConcurrentReferenceHashMap
import org.fernice.reflare.util.VacatingRef
import org.fernice.reflare.util.VacatingReferenceHolder
import org.fernice.reflare.util.weakReferenceHashMap
import java.awt.Component
import java.awt.Window
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.SwingUtilities

private val FRAME_COUNT = AtomicInteger(0)

val Window.frame: Frame
    get() = StyleTreeFrameLookup.ensureFrame(this)

object StyleTreeFrameLookup {

    @JvmStatic
    private val frames: MutableMap<Window, Frame> = weakReferenceHashMap()

    @JvmStatic
    fun ensureFrame(window: Window): Frame {
        return frames.computeIfAbsent(window) { window -> Frame(window) }
    }

    @JvmStatic
    fun removeReferences() {
        (frames as ConcurrentReferenceHashMap).removeStale()
    }
}

class Frame(frameInstance: Window) : Device, VacatingReferenceHolder {

    private val frameReference = VacatingRef(frameInstance)
    private val frame by frameReference

    // early creation to prevent null pointer exception
    // through forward calls to requestNextPulse()
    private val pulseRunnable = { pulse() }

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

        //childElement.reapplyCSS(origin = "frame:added")
        childElement.applyCSS(origin = "frame:added")
//        frame.revalidate()
//        frame.repaint()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.element

        childElement.parent = null
        root = null
    }

    override fun hasVacated(): Boolean = frameReference.hasVacated()

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
        root?.reapplyCSS("frame:invalidate")

//        frame.revalidate()
//        frame.repaint()
    }

    internal fun markElementDirty(element: AWTComponentElement) {
        dirtyElements.add(element)
    }

    private val debug_frameCount = FRAME_COUNT.get()

    private fun doCSSPass() {
        val root = root

        if (root != null && root.cssFlag != StyleState.CLEAN) {
            val engineContext = cssEngine.createEngineContext()

            trace(engineContext, name = "frame $debug_frameCount") { traceContext ->
                traceContext.traceRoot(root)
                root.clearDirty(DirtyBits.NODE_CSS)
                root.processCSS(traceContext)
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

    internal fun requestNextPulse() {
        if (!pulseRequested.getAndSet(true)) {
            SwingUtilities.invokeLater(pulseRunnable)
        }
    }

    fun matchStyle(element: AWTComponentElement): MatchingResult {
        return cssEngine.matchStyles(element)
    }
}

