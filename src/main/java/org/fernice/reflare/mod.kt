/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare

import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel

fun JLabel.setIconResource(resource: String) {
    icon = getIconResource(resource)
}

fun JButton.setIconResource(resource: String) {
    icon = getIconResource(resource)
}

private fun getIconResource(resource: String): Icon {
    try {
        val image = ImageIO.read(Ref::class.java.getResourceAsStream(resource))

        return AntiAliasedIcon(image)//.getScaledImage(width, height))
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

private class AntiAliasedIcon(image: Image) : ImageIcon(image) {
    override fun getIconWidth(): Int = image.getWidth(imageObserver) / 2
    override fun getIconHeight(): Int = image.getHeight(imageObserver) / 2

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val g2 = g as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2.scale(0.5, 0.5)

        val observer = imageObserver

        if (observer != null) {
            g2.drawImage(image, x, y, observer)
        } else {
            g2.drawImage(image, x, y, c)
        }

        g2.scale(1.0, 1.0)
    }
}

private object Ref