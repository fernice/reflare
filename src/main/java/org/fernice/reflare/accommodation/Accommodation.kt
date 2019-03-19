/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation

import fernice.reflare.CSSEngine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fernice.reflare.accommodation.scene.ColorPalette
import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneLoader
import org.fernice.reflare.accommodation.scenes.CreditsScene
import org.fernice.reflare.accommodation.scenes.opening.Opening01Scene
import org.fernice.reflare.accommodation.scenes.opening.Opening02Scene
import org.fernice.reflare.accommodation.scenes.opening.Opening03EmotionalScene
import org.fernice.reflare.accommodation.scenes.opening.Opening03Scene
import org.fernice.reflare.accommodation.still.AboutStill
import org.fernice.reflare.accommodation.still.IntroStill
import org.fernice.reflare.accommodation.still.RevealStill
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Window
import javax.swing.JFrame
import javax.swing.WindowConstants


object Accommodation {

    private val hook by lazy { Accommodation::class.java.getResourceAsStream("/reflare/accommodation/hook.txt").bufferedReader().readText() }

    fun adjustFraming(component: Component, text: String, local: Boolean, width: Int, margin: Double, rectangle: Rectangle?): Rectangle {
        if (local && text.equals(hook, ignoreCase = true)) {
            Window.getWindows().forEach { window -> window.isVisible = false }

            AccommodationDialog().isVisible = true
        }

        return Rectangle()
    }
}

object InitializationHooks {

    init {
        CSSEngine.addStylesheetResource("/reflare/accommodation/still.css")
    }
}

class AccommodationDialog : JFrame() {

    private val stillRenderer: SceneRenderer

    init {
        InitializationHooks

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        isResizable = false

        Dimension(600, 600).also { dimension ->
            minimumSize = dimension
            size = dimension
            maximumSize = dimension
        }

        setLocationRelativeTo(null)

        stillRenderer = SceneRenderer(SceneLoaderImpl)

        contentPane = stillRenderer

        GlobalScope.launch {
            delay(1000)

            stillRenderer.scene = Opening01Scene()
        }
    }
}

object SceneLoaderImpl : SceneLoader {

    override fun load(path: String): Scene {
        return when (path) {
            "opening/01" -> Opening01Scene()
            "opening/02" -> Opening02Scene()
            "opening/03" -> Opening03Scene()
            "opening/03-emotional" -> Opening03EmotionalScene()

            "credits" -> CreditsScene()

            "intro" -> IntroStill()
            "reveal" -> RevealStill()
            "help/about" -> AboutStill()
            else -> error("no such scene: $path")
        }
    }
}

object ColorPaletteImpl : ColorPalette {

    override val primary: Color = Color(0xffffff)
    override val base: Color = Color(0x564154)
    override val accent0: Color = Color(0xFF6542)
    override val accent1: Color = Color(0x779FA1)
    override val accent2: Color = Color(0xE0CBA8)
}