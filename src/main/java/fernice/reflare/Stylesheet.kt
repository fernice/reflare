/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare

import org.fernice.flare.style.Origin
import org.fernice.flare.style.QuirksMode
import org.fernice.flare.url.Url
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import org.fernice.flare.style.stylesheet.Stylesheet as StylesheetPeer

class Stylesheet private constructor(
    internal val peer: StylesheetPeer,
) {

    val source: URI
        get() = peer.source

    companion object {

        @JvmStatic
        fun fromResource(resource: String): Stylesheet {
            return fromResource(resource, Stylesheet::class.java)
        }

        @JvmStatic
        fun fromResource(resource: String, clazz: Class<*>): Stylesheet {
            return fromResource(clazz.resolveResourceName(resource), clazz.classLoader)
        }

        @JvmStatic
        fun fromResource(resource: String, classLoader: ClassLoader?): Stylesheet {
            val url = when {
                classLoader != null -> classLoader.getResource(resource)
                else -> ClassLoader.getSystemResource(resource)
            } ?: error("cannot locate resource '$resource' for ClassLoader $classLoader")
            return fromURL(url)
        }

        @JvmStatic
        fun fromURL(url: URL): Stylesheet {
            val source = url.toURI()
            return url.openStream().use { inputStream ->
                fromInputStream(inputStream, source)
            }
        }

        @JvmStatic
        fun fromFile(file: File): Stylesheet {
            return fromPath(file.toPath())
        }

        @JvmStatic
        fun fromPath(path: Path): Stylesheet {
            val source = path.toUri()
            return Files.newInputStream(path).use { inputStream ->
                fromInputStream(inputStream, source)
            }
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream, source: URI): Stylesheet {
            return fromReader(inputStream.bufferedReader(StandardCharsets.UTF_8), source)
        }

        @JvmStatic
        fun fromReader(reader: Reader, source: URI): Stylesheet {
            val text = reader.readText()

            return from(text, source)
        }

        @JvmStatic
        fun from(text: String, source: URI): Stylesheet {
            return Stylesheet(StylesheetPeer.from(text, Url(""), Origin.Author, QuirksMode.NoQuirks, source))
        }
    }
}

private fun Class<*>.resolveResourceName(name: String): String {
    return when {
        !name.startsWith("/") -> {
            var c: Class<*> = this
            while (c.isArray) {
                c = c.componentType
            }
            val baseName = c.name
            val index = baseName.lastIndexOf('.')
            if (index != -1) {
                (baseName.substring(0, index).replace('.', '/') + "/" + name)
            } else {
                name
            }
        }

        else -> {
            name.substring(1)
        }
    }
}
