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

class Introduction01 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("I'm sorry for us.")
            delay(2)

            dlf()
            him()
            ln("What do you mean?")
            delay(3)

            dlf()
            her()
            ln("We look like complete idiots.")
            delay(2)

            dlf()
            cm("She turns around in front of the mirror.")
            delay(2)

            dlf()
            him()
            action {
                ac("I think we look fabulous.")
                ac("Isn't that the point?")
            }
            delay(2)

            dlf()
            her()
            dc("hmm")
            delay(3)

            dlf()
            him()
            action {
                ac("Come on, we've got to hurry.") { goto("introduction/02") }
            }
        }
    }
}