package de.krall.reflare.element

import de.krall.flare.Engine
import de.krall.flare.dom.Device
import de.krall.flare.font.FontMetricsProvider
import de.krall.flare.font.FontMetricsQueryResult
import de.krall.flare.std.None
import de.krall.flare.std.Some
import de.krall.flare.style.properties.stylestruct.Font
import de.krall.flare.style.stylesheet.Origin
import de.krall.flare.style.stylesheet.Stylesheet
import de.krall.flare.style.value.computed.Au
import de.krall.flare.style.value.generic.Size2D
import de.krall.reflare.FlareLookAndFeel
import java.awt.Component
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.swing.JFrame

class Frame(val frame: JFrame) : Device {

    val cssEngine = Engine.from(
            this,
            object : FontMetricsProvider {
                override fun query(font: Font, fontSize: Au, device: Device): FontMetricsQueryResult {
                    return FontMetricsQueryResult.NotAvailable()
                }
            }
    )

    init {
        val path = File(FlareLookAndFeel::class.java.getResource("/test.css").file).toPath()
        val encoded = Files.readAllBytes(path)

        val text = String(encoded)

        val stylesheet = Stylesheet.from(text, Origin.AUTHOR)

        cssEngine.stylist.insertStyleheet(stylesheet)
    }

    private val children: MutableList<AWTComponentElement> = mutableListOf()

    init {
        frames[frame] = this

        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                restyle()
            }
        })

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
        val childElement = child.into()

        childElement.frame = Some(this@Frame)

        children.add(childElement)

        restyle()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.into()

        childElement.parent = None()

        children.remove(childElement)
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

    fun restyle() {
        for (root in children) {
            cssEngine.applyStyles(root)

        }
        frame.contentPane.revalidate()
        frame.repaint()
    }
}

private val frames: MutableMap<JFrame, Frame> = WeakHashMap()

fun JFrame.into(): Frame {
    return frames[this] ?: Frame(this)
}