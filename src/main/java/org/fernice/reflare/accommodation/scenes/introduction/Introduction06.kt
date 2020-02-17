/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes.introduction

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scenes.cm
import org.fernice.reflare.accommodation.scenes.dc
import org.fernice.reflare.accommodation.scenes.dlf

class Introduction06 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            dc("loud blast")
            delay(4)

            dlf()
            cm("She is interrupted by a loud sound.")
            delay(4)

            dlf()
            cm("They both feel the force acting on their bodies.")
            delay(4)

            dlf()
            cm("As she faints away she takes on last look at him.")
            delay(3)

            dlf()
            dc("don't go")
            delay(4)

            lf()
            dc("don't...")
            delay(4)

            dlf()
            cm("She faints.")
            delay(6)

            dlf()
            action {
                ac("") { goto("title-screen") }
            }
        }
    }
}