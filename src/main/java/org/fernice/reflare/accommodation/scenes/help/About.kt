/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes.help

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scenes.dlf
import org.fernice.reflare.accommodation.scenes.title

class About : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            dlf()
            title("Accommodations")

            lf()
            ln("A tale of a life that is gone.")
            delay(1)

            dlf()
            ln("A text-base Story")
            delay(1)

            dlf()
            ln("Click the [actions] to progress.")
            delay(1)

            ln("Make choices and face the consequences.")
            delay(1)

            ln("But most importantly enjoy.")
            delay(1)

            dlf()
            action {
                ac("Back") { goto("title-screen") }
            }
        }
    }
}