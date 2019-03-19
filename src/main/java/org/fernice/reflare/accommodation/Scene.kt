/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation

import fernice.reflare.classes
import fernice.reflare.style
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.fernice.reflare.accommodation.scene.ActionBuilder
import org.fernice.reflare.accommodation.scene.ActionContext
import org.fernice.reflare.accommodation.scene.Attributable
import org.fernice.reflare.accommodation.scene.ColorPalette
import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneLoader
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scene.Script
import org.fernice.reflare.accommodation.scene.ScriptContext
import org.fernice.reflare.layout.VerticalLayout
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates

object NoScene : Scene {
    override fun run(runner: SceneRunner) {}
}

class SceneRenderer(val sceneLoader: SceneLoader) : JPanel(), Script, SceneRunner {

    private val textPanel: JPanel

    init {
        layout = BorderLayout()
        classes.add("still")

        textPanel = JPanel()
        textPanel.layout = VerticalLayout()
        add(textPanel, BorderLayout.CENTER)
    }

    var scene by Delegates.observable<Scene>(NoScene) { _, _, scene ->
        textPanel.removeAll()

        scene.run(this)
    }

    override fun script(script: suspend Script.() -> Unit) {
        GlobalScope.launch {
            script(this@SceneRenderer)
        }
    }

    override val context: ScriptContext = SceneRendererScriptContext(ColorPaletteImpl)

    override suspend fun lf() {
        textPanel.mutate {
            add(ElementPanel(" ", context.palette.primary))
        }
    }

    override suspend fun ln(text: String, content: Attributable.() -> Unit) {
        textPanel.mutate {
            val attributes = AttributableImpl(context.palette.primary).apply { content() }

            add(ElementPanel(text, attributes.color))
        }
    }

    override suspend fun action(actionBuilder: suspend ActionBuilder.() -> Unit) {
        val actionPanel = ActionsPanel(context)
        actionPanel.actionBuilder()

        textPanel.mutate {
            add(actionPanel)
        }

        suspendCoroutine<Unit> { continuation ->
            actionPanel.addActionListener { goto ->
                if (goto != null) {
                    scene = sceneLoader.load(goto)
                    continuation.context.cancel()
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun ac(action: ActionContext.() -> Unit) {
        val context = ActionContextImpl(context).apply { action() }

        context.goto?.let { goto ->
            scene = sceneLoader.load(goto)
            coroutineContext.cancel()
        }
    }
}

private class ElementPanel(content: String, color: Color) : JLabel() {

    init {
        text = content
        style = "color: ${color.toHexString()}"
    }
}

private class ActionsPanel(override val context: ScriptContext) : JPanel(), ActionBuilder {

    init {
        layout = VerticalLayout()
    }

    override suspend fun ac(text: String, action: ActionContext.() -> Unit) {

        val fireAction = local@{
            val parent = parent ?: return@local

            val index = parent.components.indexOf(this)
            parent.remove(this)

            val panel = ElementPanel(text, context.palette.primary)
            parent.add(panel, index)

            parent.revalidate()
            parent.repaint()

            val context = ActionContextImpl(context).apply { action() }

            GlobalScope.launch {
                listeners.forEach { it(context.goto) }
            }
        }

        mutate {
            val actionPanel = ActionPanel(text, context.palette.primary)
            actionPanel.addActionListener { fireAction() }
            add(actionPanel)
        }
    }

    private val listeners = mutableListOf<(String?) -> Unit>()

    fun addActionListener(listener: (String?) -> Unit) {
        listeners.add(listener)
    }
}

private class ActionPanel(content: String, color: Color) : JButton() {

    init {
        classes.add("acc-action")
        text = "[$content]"
        style = "color: ${color.toHexString()}"
        horizontalAlignment = JLabel.LEFT
    }
}

class SceneRendererScriptContext(
    override val palette: ColorPalette,
    override val conditions: MutableMap<String, Int> = mutableMapOf()
) : ScriptContext

class AttributableImpl(
    override var color: Color
) : Attributable {

    override fun color(color: Color): Attributable {
        this.color = color

        return this
    }
}

class ActionContextImpl(val context: ScriptContext) : ActionContext {

    var goto: String? = null

    override fun goto(path: String): ActionContext {
        goto = path

        return this
    }

    override fun obtain(vararg conditions: String): ActionContext {
        for (condition in conditions) {
            context.conditions.merge(condition, 1) { a, b -> a + b }
        }

        return this
    }

    override fun strip(vararg conditions: String): ActionContext {
        for (condition in conditions) {
            context.conditions.merge(condition, -1) { a, b -> a + b }
        }

        return this
    }
}

fun Color.toHexString(): String {
    return "#" + Integer.toHexString(this.rgb and 0xffffff).padStart(6, '0')
}

suspend fun <T : Component> T.mutate(block: T.() -> Unit) {
    withContext(Dispatchers.Swing) {
        block()

        revalidate()
        repaint()
    }
}