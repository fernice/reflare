/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scenes.dlf
import org.fernice.reflare.accommodation.scenes.title
import kotlin.system.exitProcess

class TitleScreen : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            delay(1)

            dlf()
            title("Accommodations")

            delay(3)

            lf()
            ln("A tale of a life that is gone.")
            delay(2)

            dlf()
            dlf()
            action {
                ac("Play") { goto("help/about") }
                ac("About") { goto("help/about") }
                ac("Exit") { exitProcess(-1) }
            }
        }
    }
}