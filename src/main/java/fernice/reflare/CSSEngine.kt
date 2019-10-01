package fernice.reflare

import org.fernice.flare.Engine
import org.fernice.flare.EngineContext
import org.fernice.flare.SharedEngine
import org.fernice.flare.dom.Device
import org.fernice.flare.dom.Element
import org.fernice.flare.font.FontMetricsProvider
import org.fernice.flare.font.FontMetricsQueryResult
import org.fernice.flare.std.systemFlag
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.properties.stylestruct.Font
import org.fernice.flare.style.stylesheet.Origin
import org.fernice.flare.style.stylesheet.Stylesheet
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.platform.OperatingSystem
import org.fernice.reflare.platform.Platform
import java.awt.Component
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import java.nio.charset.StandardCharsets

private val SUPPRESS_USER_AGENT_STYLESHEETS = systemFlag("fernice.reflare.suppressUserAgentStylesheet")

object CSSEngine {

    val shared = SharedEngine.new(
        object : FontMetricsProvider {
            override fun query(font: Font, fontSize: Au, device: Device): FontMetricsQueryResult {
                return FontMetricsQueryResult.NotAvailable()
            }
        }
    )

    private val engines: MutableList<WeakReference<Engine>> = mutableListOf()

    fun createEngine(device: Device): Engine {
        val engine = Engine(
            device,
            shared
        )

        engines.add(WeakReference(engine))

        return engine
    }

    fun createLocalEngineContext(element: Element): EngineContext {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        return shared.createEngineContext(localDevice)
    }

    fun styleWithLocalContext(element: Element) {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        shared.style(localDevice, element)
    }

    fun matchStyleWithLocalContext(element: Element): MatchingResult {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        return shared.matchStyle(localDevice, element)
    }

    private val stylesheets: MutableMap<Source, Stylesheet> = mutableMapOf()

    init {
        if (!SUPPRESS_USER_AGENT_STYLESHEETS) {
            addStylesheet(Source.Resource("/reflare/style/user-agent.css"), Origin.USER_AGENT)

            val platformStylesheet = when (Platform.operatingSystem) {
                OperatingSystem.Windows -> "/reflare/style/user-agent-windows.css"
                OperatingSystem.Mac -> "/reflare/style/user-agent-macos.css"
                OperatingSystem.Linux -> "/reflare/style/user-agent-linux.css"
            }

            addStylesheet(Source.Resource(platformStylesheet), Origin.USER_AGENT)

            addStylesheet(Source.Resource("/reflare/style/file_chooser.css"), Origin.USER_AGENT)
            addStylesheet(Source.Resource("/reflare/style/material.css"), Origin.USER)
        }
    }

    fun addStylesheetResource(resource: String) {
        addStylesheet(Source.Resource(resource), Origin.AUTHOR)
    }

    fun addStylesheet(file: File) {
        addStylesheet(Source.File(file), Origin.AUTHOR)
    }

    private fun addStylesheet(source: Source, origin: Origin) {
        if (stylesheets.containsKey(source)) {
            removeStylesheet(source)
        }

        val text = source.inputStream()
            .bufferedReader(StandardCharsets.UTF_8)
            .use { reader -> reader.readText() }

        val stylesheet = Stylesheet.from(text, origin)

        stylesheets[source] = stylesheet

        shared.stylist.addStylesheet(stylesheet)

        for (engine in engines.toStrongList()) {
            engine.device.invalidate()
        }
    }

    fun removeStylesheet(file: File) {
        removeStylesheet(Source.File(file))
    }

    fun removeStylesheet(source: Source) {
        val stylesheet = stylesheets.remove(source)

        if (stylesheet != null) {
            shared.stylist.removeStylesheet(stylesheet)

            for (engine in engines.toStrongList()) {
                engine.device.invalidate()
            }
        }
    }

    fun reloadStylesheets() {
        val stylesheets = stylesheets.toMap()

        for ((source, _) in stylesheets) {
            removeStylesheet(source)
        }

        for ((source, stylesheet) in stylesheets) {
            addStylesheet(source, stylesheet.origin)
        }
    }

    fun stylesheets(): List<Stylesheet> {
        return stylesheets.values.toList()
    }
}

sealed class Source {

    data class Resource(val path: String) : Source()

    data class File(val path: java.io.File) : Source()

    fun inputStream(): InputStream {
        return when (this) {
            is Resource -> FlareLookAndFeel::class.java.getResourceAsStream(path)
            is File -> path.inputStream()
        }
    }
}

private class LocalDevice(val component: Component) : Device {
    override fun viewportSize(): Size2D<Au> {
        return Size2D(Au.fromPx(component.width), Au.fromPx(component.height))
    }

    override fun rootFontSize(): Au {
        return Au.fromPx(component.font.size)
    }

    override fun setRootFontSize(size: Au) {
        component.font = component.font.deriveFont(size.toFloat())
    }

    override fun invalidate() {
    }
}

private fun <E> MutableList<WeakReference<E>>.toStrongList(): List<E> {
    val strong: MutableList<E> = mutableListOf()

    val iter = this.iterator()
    while (iter.hasNext()) {
        val ref = iter.next()
        val value = ref.get()

        if (value != null) {
            strong.add(value)
        } else {
            iter.remove()
        }
    }

    return strong
}