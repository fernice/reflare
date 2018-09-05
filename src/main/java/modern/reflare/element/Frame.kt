package modern.reflare.element

import de.krall.flare.dom.Device
import de.krall.flare.std.None
import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.style.value.computed.Au
import de.krall.flare.style.value.generic.Size2D
import java.awt.Component
import java.awt.Window
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.WeakHashMap
import javax.swing.SwingUtilities

class Frame(val frame: Window) : Device {

    val cssEngine = CSSEngine.createEngine(this)

    private var root: Option<AWTComponentElement> = None()

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
        if (root is Some) {
            System.err.println("= ERROR ==========================")
            System.err.println(" A SECOND CHILD WAS ADDED TO THE ")
            System.err.println(" FRAME. PLEASE REPORT THIS BUG.")
        }

        val childElement = child.into()

        childElement.frame = Some(this@Frame)
        root = Some(childElement)

        markElementDirty(childElement)
    }

    private fun childRemoved(child: Component) {
        val childElement = child.into()

        childElement.parent = None()

        root = None()
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
        restyle()
    }

    fun restyle() {
        val root = root

        if (root is Some) {
            markElementDirty(root.value)
        } else {
            frame.revalidate()
            frame.repaint()
        }
    }

    fun markElementDirty(element: AWTComponentElement) {
        invokeLater {
            cssEngine.applyStyles(element)
        }
    }

    fun applyStyles(element: AWTComponentElement) {
        invokeAndWait {
            cssEngine.applyStyles(element)
        }
    }

}

private val frames: MutableMap<Window, Frame> = WeakHashMap()

fun Window.into(): Frame {
    return frames[this] ?: Frame(this)
}

fun invokeLater(runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable()
    } else {
        SwingUtilities.invokeLater(runnable)
    }
}

fun invokeAndWait(runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable()
    } else {
        SwingUtilities.invokeAndWait(runnable)
    }
}