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
import org.fernice.reflare.util.VacatingReferenceHolder
import java.awt.Component
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

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

        removeVacatedEngines()
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

    private val stylesheets: MutableMap<Source, Stylesheet> = ConcurrentHashMap()

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
        }
    }

    @JvmStatic
    fun addStylesheetResource(resource: String) {
        addStylesheet(Source.Resource(resource), Origin.AUTHOR)
    }

    @JvmStatic
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

        val stylesheet = Stylesheet.from(text, origin, source.uri)

        stylesheets[source] = stylesheet

        shared.stylist.addStylesheet(stylesheet)

        invalidateEngines()
    }

    @JvmStatic
    fun removeStylesheet(file: File) {
        removeStylesheet(Source.File(file))
    }

    @JvmStatic
    fun removeStylesheetResource(resource: String) {
        removeStylesheet(Source.Resource(resource))
    }

    private fun removeStylesheet(source: Source) {
        val stylesheet = stylesheets.remove(source)

        if (stylesheet != null) {
            shared.stylist.removeStylesheet(stylesheet)

            invalidateEngines()
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

    private fun invalidateEngines() {
        val iter = engines.iterator()
        while (iter.hasNext()) {
            val ref = iter.next()
            val value = ref.get()

            if (value == null) {
                iter.remove()
            } else {
                val device = value.device
                if (device is VacatingReferenceHolder && device.hasVacated()) {
                    iter.remove()
                } else {
                    device.invalidate()
                }
            }
        }
    }

    private fun removeVacatedEngines() {
        val iter = engines.iterator()
        while (iter.hasNext()) {
            val ref = iter.next()
            val value = ref.get()

            if (value == null) {
                iter.remove()
            } else {
                val device = value.device
                if (device is VacatingReferenceHolder && device.hasVacated()) {
                    iter.remove()
                }
            }
        }
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

    val uri: URI
        get() = when (this) {
            is Resource -> URI(path)
            is File -> path.toURI()
        }
}

private class LocalDevice(val component: Component) : Device {

    override val viewportSize: Size2D<Au>
        get() = Size2D(Au.fromPx(component.width), Au.fromPx(component.height))

    override var rootFontSize: Au
        get() = Au.fromPx(component.font.size)
        set(value) {
            component.font = component.font.deriveFont(value.toFloat())
        }

    override fun invalidate() {
    }
}