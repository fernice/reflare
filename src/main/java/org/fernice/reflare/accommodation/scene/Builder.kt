/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scene

import java.awt.Color
import java.time.Duration

@DslMarker
annotation class ScriptDSL

interface Scene {

    fun run(runner: SceneRunner)
}

interface SceneRunner {

    @ScriptDSL
    fun script(script: suspend Script.() -> Unit)
}

interface ScriptContext {
    val palette: ColorPalette
    val conditions: MutableMap<String, Int>
}

@ScriptDSL
interface Script {

    val context: ScriptContext

    suspend fun delay(seconds: Int) {
        kotlinx.coroutines.time.delay(Duration.ofSeconds(seconds.toLong()))
    }

    suspend fun delay(seconds: Float) {
        kotlinx.coroutines.time.delay(Duration.ofMillis((1000 * seconds).toLong()))
    }

    suspend fun having(vararg conditions: String, conditionalScript: suspend Script.() -> Unit) {
        val condition = conditions
            .map { condition -> context.conditions.getOrDefault(condition, 0) }
            .all { value -> value > 0 }

        if (condition) {
            conditionalScript()
        }
    }

    suspend fun having(condition: String, count: Int, conditionalScript: suspend Script.() -> Unit) {
        if (context.conditions.getOrDefault(condition, 0) == count) {
            conditionalScript()
        }
    }

    suspend fun lf()
    suspend fun ln(text: String, content: Attributable.() -> Unit = {})
    suspend fun action(actionBuilder: suspend ActionBuilder.() -> Unit = {})

    suspend fun ac(action: ActionContext.() -> Unit = {})
}

@ScriptDSL
interface ActionBuilder {

    val context: ScriptContext

    suspend fun having(vararg conditions: String, conditionalScript: suspend ActionBuilder.() -> Unit) {
        val condition = conditions
            .map { condition -> context.conditions.getOrDefault(condition, 0) }
            .all { value -> value > 0 }

        if (condition) {
            conditionalScript()
        }
    }

    suspend fun having(condition: String, count: Int, conditionalScript: suspend ActionBuilder.() -> Unit) {
        if (context.conditions.getOrDefault(condition, 0) == count) {
            conditionalScript()
        }
    }

    suspend fun ac(text: String, action: ActionContext.() -> Unit = {})
}

interface Attributable {

    var color: Color
    fun color(color: Color): Attributable

    fun size(size: Int): Attributable
}

interface ActionContext {

    fun goto(path: String): ActionContext

    fun obtain(vararg conditions: String): ActionContext

    fun strip(vararg conditions: String): ActionContext
}
