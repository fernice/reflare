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
import org.fernice.reflare.accommodation.scenes.help.About
import org.fernice.reflare.accommodation.scenes.introduction.Introduction01
import org.fernice.reflare.accommodation.scenes.TitleScreen
import org.fernice.reflare.accommodation.scenes.introduction.Introduction02
import org.fernice.reflare.accommodation.scenes.introduction.Introduction03
import org.fernice.reflare.accommodation.scenes.introduction.Introduction04
import org.fernice.reflare.accommodation.scenes.introduction.Introduction05
import org.fernice.reflare.accommodation.scenes.introduction.Introduction06
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

            stillRenderer.scene = Introduction01()
        }
    }
}

object SceneLoaderImpl : SceneLoader {

    override fun load(path: String): Scene {
        return when (path) {
            "introduction/01" -> Introduction01()
            "introduction/02" -> Introduction02()
            "introduction/03" -> Introduction03()
            "introduction/04" -> Introduction04()
            "introduction/05" -> Introduction05()
            "introduction/06" -> Introduction06()

            "title-screen" -> TitleScreen()

            "help/about" -> About()

            "credits" -> CreditsScene()
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