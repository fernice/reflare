package modern.reflare.element

import de.krall.flare.Engine
import de.krall.flare.SharedEngine
import de.krall.flare.dom.Device
import de.krall.flare.font.FontMetricsProvider
import de.krall.flare.font.FontMetricsQueryResult
import de.krall.flare.style.properties.stylestruct.Font
import de.krall.flare.style.stylesheet.Origin
import de.krall.flare.style.stylesheet.Stylesheet
import de.krall.flare.style.value.computed.Au
import modern.reflare.FlareLookAndFeel
import java.io.File
import java.lang.ref.WeakReference
import java.nio.charset.StandardCharsets
import java.nio.file.Files

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

        val text = input.bufferedReader(StandardCharsets.UTF_8).use { reader -> reader.readText() }

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

        shared.stylist.addStylesheet(stylesheet)

        for (engine in engines.toStrongList()) {
            engine.device.invalidate()
        }
    }

    fun removeStylesheet(file: File) {
        val stylesheet = stylesheets.remove(file)

        if (stylesheet != null) {
            shared.stylist.removeStylesheet(stylesheet)

            for (engine in engines.toStrongList()) {
                engine.device.invalidate()
            }
        }
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