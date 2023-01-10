package fernice.reflare

import org.fernice.flare.Engine
import org.fernice.flare.EngineContext
import org.fernice.flare.SharedEngine
import org.fernice.flare.dom.Device
import org.fernice.flare.dom.Element
import org.fernice.flare.font.FontMetricsProvider
import org.fernice.flare.font.FontMetricsQueryResult
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.properties.stylestruct.Font
import org.fernice.flare.style.Origin
import org.fernice.flare.style.StyleRoot
import org.fernice.flare.style.parser.QuirksMode
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.platform.OperatingSystem
import org.fernice.reflare.platform.Platform
import org.fernice.reflare.util.VacatingReferenceHolder
import org.fernice.std.systemFlag
import java.awt.Component
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import org.fernice.flare.style.stylesheet.Stylesheet as StylesheetPeer

private val SUPPRESS_USER_AGENT_STYLESHEETS = systemFlag("fernice.reflare.suppressUserAgentStylesheet")

object CSSEngine {

    internal val sharedEngine = SharedEngine.new(
        object : FontMetricsProvider {
            override fun query(font: Font, fontSize: Au, device: Device): FontMetricsQueryResult {
                return FontMetricsQueryResult.NotAvailable
            }
        }
    )

    private val engines: MutableList<WeakReference<Engine>> = mutableListOf()

    fun createEngine(device: Device): Engine {
        val engine = Engine(
            device,
            sharedEngine
        )

        removeVacatedEngines()
        engines.add(WeakReference(engine))

        return engine
    }

    fun createLocalEngineContext(element: Element): EngineContext {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        return sharedEngine.createEngineContext(localDevice)
    }

    fun styleWithLocalContext(element: Element) {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        sharedEngine.style(localDevice, element)
    }

    fun matchStyleWithLocalContext(element: Element): MatchingResult {
        val localDevice = LocalDevice((element as AWTComponentElement).component)

        return sharedEngine.matchStyle(localDevice, element)
    }

    private val lock = ReentrantLock()

    private val mutableStylesheets = mutableListOf<Stylesheet>()

    @JvmStatic
    val stylesheets: List<Stylesheet>
        get() = lock.withLock { mutableStylesheets.toList() }

    init {
        if (!SUPPRESS_USER_AGENT_STYLESHEETS) {
            val styleRoot = sharedEngine.stylist.styleRoot

            styleRoot.addStylesheet(Stylesheet(Origin.UserAgent, "/reflare/style/user-agent.css"))

            val platformStylesheet = when (Platform.operatingSystem) {
                OperatingSystem.Windows -> "/reflare/style/user-agent-windows.css"
                OperatingSystem.Mac -> "/reflare/style/user-agent-macos.css"
                OperatingSystem.Linux -> "/reflare/style/user-agent-linux.css"
            }

            styleRoot.addStylesheet(Stylesheet(Origin.UserAgent, platformStylesheet))
        }
    }

    @JvmStatic
    fun addStylesheet(stylesheet: Stylesheet) {
        lock.withLock {
            mutableStylesheets.add(stylesheet)
            sharedEngine.stylist.styleRoot.addStylesheet(stylesheet.peer)
        }
        invalidate()
    }

    @JvmStatic
    fun removeStylesheet(stylesheet: Stylesheet) {
        lock.withLock {
            mutableStylesheets.remove(stylesheet)
            sharedEngine.stylist.styleRoot.removeStylesheet(stylesheet.peer)
        }
        invalidate()
    }

    fun invalidate() {
        invalidateEngines()
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

    @Deprecated(
        message = "use Stylesheet instead",
        replaceWith = ReplaceWith("addStylesheet(Stylesheet.fromResource(resource))", "fernice.reflare.CSSEngine.addStylesheet")
    )
    @JvmStatic
    fun addStylesheetResource(resource: String) {
        addStylesheet(Stylesheet.fromResource(resource))
    }

    @Deprecated(
        message = "use Stylesheet instead",
        replaceWith = ReplaceWith("addStylesheet(Stylesheet.fromFile(file))", "fernice.reflare.CSSEngine.addStylesheet")
    )
    @JvmStatic
    fun addStylesheet(file: File) {
        addStylesheet(Stylesheet.fromFile(file))
    }

    @Deprecated(message = "use Stylesheet instead")
    @JvmStatic
    fun removeStylesheet(file: File) {
        val uri = file.toURI()
        val stylesheet = stylesheets.firstOrNull { it.source == uri } ?: return
        removeStylesheet(stylesheet)
    }

    @Deprecated(message = "use Stylesheet instead")
    @JvmStatic
    fun removeStylesheetResource(resource: String) {
        val url = CSSEngine::class.java.getResource(resource) ?: return
        val uri = url.toURI()
        val stylesheet = stylesheets.firstOrNull { it.source == uri } ?: return
        removeStylesheet(stylesheet)
    }

    @Deprecated(message = "no longer supported")
    fun reloadStylesheets() {
    }
}

private fun Stylesheet(origin: Origin, resource: String): StylesheetPeer {
    val url = CSSEngine::class.java.getResource(resource) ?: error("cannot locate user agent stylesheet: $resource")

    val text = url.openStream().bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    val source = url.toURI()

    return StylesheetPeer.from(origin, text, source)
}

private class LocalDevice(val component: Component) : Device {

    override val viewportSize: Size2D<Au>
        get() = Size2D(Au.fromPx(component.width), Au.fromPx(component.height))

    override var rootFontSize: Au
        get() = Au.fromPx(component.font.size)
        set(value) {
            component.font = component.font.deriveFont(value.toFloat())
        }

    override val systemFontSize: Au = Au.fromPx(16)

    override fun invalidate() {}
}
