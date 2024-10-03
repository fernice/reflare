package fernice.reflare

import org.fernice.flare.EngineInstance
import org.fernice.flare.EngineContext
import org.fernice.flare.Engine
import org.fernice.flare.dom.Device
import org.fernice.flare.dom.Element
import org.fernice.flare.font.FontMetricsProvider
import org.fernice.flare.font.FontMetricsQueryResult
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.properties.stylestruct.Font
import org.fernice.flare.style.Origin
import org.fernice.flare.style.QuirksMode
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.flare.url.Url
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.platform.OperatingSystem
import org.fernice.reflare.platform.Platform
import org.fernice.reflare.util.VacatingReferenceHolder
import org.fernice.std.systemFlag
import java.awt.Component
import java.lang.ref.WeakReference
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import org.fernice.flare.style.stylesheet.Stylesheet as StylesheetPeer

private val SUPPRESS_USER_AGENT_STYLESHEETS = systemFlag("fernice.reflare.suppressUserAgentStylesheet")

object CSSEngine {

    internal val device: Device = SharedDevice()
    internal val engine = Engine.new(
        device,
        object : FontMetricsProvider {
            override fun query(font: Font, fontSize: Au, device: Device): FontMetricsQueryResult {
                return FontMetricsQueryResult.NotAvailable
            }
        }
    )

    private val engineInstances: MutableList<WeakReference<EngineInstance>> = mutableListOf()

    fun createEngine(device: Device): EngineInstance {
        val engine = engine.createEngineInstance { device }

        removeVacatedEngineInstances()
        engineInstances.add(WeakReference(engine))

        return engine
    }

    fun createLocalEngineContext(element: Element): EngineContext {
        val localDevice = createLocalDevice(element)

        return engine.createEngineContext(localDevice)
    }

    fun styleWithLocalContext(element: Element) {
        val localDevice = createLocalDevice(element)

        engine.restyle(localDevice, element)
    }

    fun matchStyleWithLocalContext(element: Element): MatchingResult {
        val localDevice = createLocalDevice(element)

        return engine.matchStyle(localDevice, element)
    }

    private fun createLocalDevice(element: Element): Device {
        return LocalDevice((element as AWTComponentElement).component, device)
    }

    private val lock = ReentrantLock()

    private val mutableStylesheets = mutableListOf<Stylesheet>()

    @JvmStatic
    val stylesheets: List<Stylesheet>
        get() = lock.withLock { mutableStylesheets.toList() }

    init {
        if (!SUPPRESS_USER_AGENT_STYLESHEETS) {
            val styleRoot = engine.stylist.styleRoot

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
            engine.stylist.styleRoot.addStylesheet(stylesheet.peer)
        }
        invalidate()
    }

    @JvmStatic
    fun removeStylesheet(stylesheet: Stylesheet) {
        lock.withLock {
            mutableStylesheets.remove(stylesheet)
            engine.stylist.styleRoot.removeStylesheet(stylesheet.peer)
        }
        invalidate()
    }

    fun invalidate() {
        invalidateEngineInstances()
    }

    private fun invalidateEngineInstances() {
        val iter = engineInstances.iterator()
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

    private fun removeVacatedEngineInstances() {
        val iter = engineInstances.iterator()
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

private fun Stylesheet(origin: Origin, resource: String): StylesheetPeer {
    val url = CSSEngine::class.java.getResource(resource) ?: error("cannot locate user agent stylesheet: $resource")

    val text = url.openStream().bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    val source = url.toURI()

    return StylesheetPeer.from(text, Url(""), origin, QuirksMode.NoQuirks, source)
}

internal class SharedDevice : Device {

    override val viewportSize: Size2D<Au>
        get() = error("use derived device")

    override var rootFontSize: Au
        get() = error("use derived device")
        set(_) {
            error("use derived device")
        }

    override val systemFontSize: Au = Au.fromPx(16)

    override fun invalidate() {}
}

private class LocalDevice(
    private val component: Component,
    private val sharedDevice: Device,
) : Device {

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
