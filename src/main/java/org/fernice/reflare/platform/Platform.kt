/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.platform

import java.awt.Font

enum class OperatingSystem {

    Windows,

    Mac,

    Linux
}

object Platform {

    val operatingSystem by lazy {
        val os = System.getProperty("fernice.reflare.platform")?.toLowerCase() ?: System.getProperty("os.name").toLowerCase()
        when {
            os.startsWith("mac") || os.startsWith("darwin") -> OperatingSystem.Mac
            os.startsWith("wind") -> OperatingSystem.Windows
            os.startsWith("linux") -> OperatingSystem.Linux
            else -> throw RuntimeException("cannot infer operating system")
        }
    }

    fun isWindows(): Boolean = operatingSystem == OperatingSystem.Windows
    fun isMac(): Boolean = operatingSystem == OperatingSystem.Mac
    fun isLinux(): Boolean = operatingSystem == OperatingSystem.Linux

    private val platform: PlatformImpl by lazy {
        when (operatingSystem) {
            OperatingSystem.Windows -> WindowsPlatformImpl()
            OperatingSystem.Mac -> MacPlatformImpl()
            OperatingSystem.Linux -> UnixPlatformImpl()
        }
    }

    val SystemSerifFont = platform.systemSerifFont
    val SystemSansSerifFont = platform.systemSansSerifFont
    val SystemMonospaceFont = platform.systemMonospaceFont
}

private interface PlatformImpl {

    val systemSerifFont: Font
    val systemSansSerifFont: Font
    val systemMonospaceFont: Font
}

class WindowsPlatformImpl : PlatformImpl {

    override val systemSerifFont = Font("Times", 0, 12)
    override val systemSansSerifFont = Font("Arial", 0, 12)
    override val systemMonospaceFont = Font("Courier", 0, 12)
}

class MacPlatformImpl : PlatformImpl {

    override val systemSerifFont = Font("Times", 0, 12)
    override val systemSansSerifFont = Font("Helvetica Neue", 0, 12)
    override val systemMonospaceFont = Font("Courier", 0, 12)
}

class UnixPlatformImpl : PlatformImpl {

    override val systemSerifFont = Font("DejaVu Serif", 0, 12)
    override val systemSansSerifFont = Font("DejaVu Sans", 0, 12)
    override val systemMonospaceFont = Font("DejaVu Sans Mono", 0, 12)
}