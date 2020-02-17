/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes.introduction

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scenes.cm
import org.fernice.reflare.accommodation.scenes.dlf
import org.fernice.reflare.accommodation.scenes.her
import org.fernice.reflare.accommodation.scenes.him

class Introduction04 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            cm("She walks outside, along the front garden to the street.")
            delay(2)

            dlf()
            her()
            ln("Come on, hurry.")
            delay(1)
            ln("I can already see the bus coming.")
            delay(3)

            dlf()
            cm("He closes the door and locks it.")
            delay(3)

            dlf()
            him()
            ln("Don't worry, I'm coming.")
            delay(1)

            dlf()
            cm("He walks up to her and stands beside her as the bus arrives.")
            delay(3)

            dlf()
            him()
            ln("Do you think we went to far with our outfit?")
            delay(2)

            lf()
            ln("I mean even for costume party.")
            delay(3)

            dlf()
            cm("She smiles.")
            delay(2)

            dlf()
            her()
            action {
                ac("Nah.") { goto("introduction/05") }
                ac("Only you.") { goto("introduction/05") }
            }
        }
    }
}