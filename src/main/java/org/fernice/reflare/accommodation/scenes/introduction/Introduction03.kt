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
import org.fernice.reflare.accommodation.scenes.her
import org.fernice.reflare.accommodation.scenes.him

class Introduction03 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            cm("He walks down the stairs.")
            delay(2)

            lf()
            cm("Out of his eyes he spots the purse lying a chair.")
            delay(2)

            dlf()
            him()
            ln("Yeah, it's down here.")
            delay(3)

            dlf()
            cm("As he picks it up, a letter falls to the ground.")
            delay(2)

            dlf()
            dc("faint rumbling noises coming from behind him")
            delay(2)

            dlf()
            cm("Shes walks downstairs.")
            delay(2)

            dlf()
            her()
            ln("Well, I'm ready.")
            delay(2)
            ln("And we still have a bus to catch.")

            delay(3)

            dlf()
            him()
            action {
                ac("Right.") { goto("introduction/04") }
            }
        }
    }
}