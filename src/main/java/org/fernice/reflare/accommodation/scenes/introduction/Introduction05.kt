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

class Introduction05 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            cm("They get into the bus.")
            delay(3)

            dlf()
            him()
            ln("So whats that letter, that was in your purse, about?")
            delay(2)

            dlf()
            her()
            ln("What letter?")
            delay(2)

            dlf()
            him()
            action {
                ac("The one that fell to the ground when I got your purse.")
                ac("It said Idahem Hospital or so.")
            }
            delay(2)

            dlf()
            cm("She looks into the distance.")
            delay(2)

            dlf()
            her()
            ln("It's...")
            delay(2)

            lf()
            ln("I mean...")
            delay(2)

            lf()
            ln("Forget about it, it doesn't matter.")
            delay(2)

            dlf()
            cm("She holds in for a moment.")
            delay(3)

            dlf()
            her()
            action {
                ac("I...") { goto("introduction/06") }
            }
        }
    }
}