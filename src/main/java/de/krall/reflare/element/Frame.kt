package de.krall.reflare.element

import de.krall.flare.Engine
import de.krall.flare.dom.Device
import de.krall.flare.font.FontMetricsProvider
import de.krall.flare.font.FontMetricsQueryResult
import de.krall.flare.std.None
import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.style.properties.stylestruct.Font
import de.krall.flare.style.stylesheet.Origin
import de.krall.flare.style.stylesheet.Stylesheet
import de.krall.flare.style.value.computed.Au
import de.krall.flare.style.value.generic.Size2D
import de.krall.reflare.FlareLookAndFeel
import java.awt.Component
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.WeakHashMap
import javax.swing.JFrame
import javax.swing.SwingUtilities

class Frame(val frame: JFrame) : Device {

    val cssEngine = Engine.from(
            this,
            object : FontMetricsProvider {
                override fun query(font: Font, fontSize: Au, device: Device): FontMetricsQueryResult {
                    return FontMetricsQueryResult.NotAvailable()
                }
            }
    )

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

    fun restyle() {
        val root = root

        if (root is Some) {
            markElementDirty(root.value)
        } else {
            frame.contentPane.revalidate()
            frame.repaint()
        }
    }

    fun markElementDirty(element: AWTComponentElement) {
        invokeAndWait {
            cssEngine.applyStyles(element)
        }
    }

// ***************************** Stylesheet ***************************** //

    private val stylesheets: MutableMap<File, Stylesheet> = mutableMapOf()

    init {
        addStylesheetResource("/default.css", Origin.USER_AGENT)
    }

    fun addStylesheetResource(resource: String) {
        addStylesheetResource(resource, Origin.AUTHOR)
    }

    private fun addStylesheetResource(resource: String, origin: Origin) {
        val file = File(FlareLookAndFeel::class.java.getResource(resource).file)

        val input = FlareLookAndFeel::class.java.getResourceAsStream(resource)

        val text = input.use { input ->
            val reader = InputStreamReader(input)

            reader.readText()
        }

        addStylesheet(file, text, origin)
    }

    fun addStylesheet(file: File) {
        val path = file.toPath()
        val encoded = Files.readAllBytes(path)

        val text = String(encoded)

        addStylesheet(file, text, Origin.AUTHOR)
    }

    private fun addStylesheet(file: File, text: String, origin: Origin) {
        if (stylesheets.containsKey(file)) {
            removeStylesheet(file)
        }

        val stylesheet = Stylesheet.from(text, origin)

        stylesheets[file] = stylesheet

        invokeAndWait {
            cssEngine.stylist.addStylesheet(stylesheet)

            restyle()
        }
    }

    fun removeStylesheet(file: File) {
        val stylesheet = stylesheets.remove(file)

        if (stylesheet != null) {
            invokeAndWait {
                cssEngine.stylist.removeStylesheet(stylesheet)

                restyle()
            }
        }
    }

    private fun invokeLater(runnable: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable()
        } else {
            SwingUtilities.invokeLater(runnable)
        }
    }

    private fun invokeAndWait(runnable: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable()
        } else {
            SwingUtilities.invokeAndWait(runnable)
        }
    }
}

private val frames: MutableMap<JFrame, Frame> = WeakHashMap()

fun JFrame.into(): Frame {
    return frames[this] ?: Frame(this)
}