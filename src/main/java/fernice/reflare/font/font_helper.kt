/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package fernice.reflare.font

import org.fernice.reflare.internal.SunFontHelper
import java.awt.Font
import java.awt.GraphicsEnvironment

object FontHelper {

    /**
     * Installs the font in the local graphics environment and in the internal font manager of the
     * css engine. If [override] is set to true, the specified font will be preferred by the css
     * engine over physical and other fonts.
     *
     * The use of this function over the [GraphicsEnvironment.registerFont] is endorsed as this
     * function will install the font regardless of the already install system font.
     * [GraphicsEnvironment] on the other hand will not install a font if the font family is known
     * to the operating system. This can lead to unwanted behaviour such as a different weighted
     * font not being installed because the regular version already exists.
     *
     * Returns true if the font has been successfully installed in the internal font manager of the
     * css engine, returns false if the font has already been installed or if override is false and
     * system font already exists.
     */
    @JvmStatic
    @JvmOverloads
    fun installFont(font: Font, override: Boolean = true): Boolean {
        val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()

        graphicsEnvironment.registerFont(font)

        return SunFontHelper.registerFontExtension(font, override)
    }

    /**
     * Uninstalls the font from the internal font manager only. Fonts cannot be removed from the
     * local graphics environment.
     */
    @JvmStatic
    fun uninstallFont(font: Font): Boolean {
        return SunFontHelper.unregisterFontExtension(font)
    }
}