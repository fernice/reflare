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

class Introduction01 : Scene {
    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("I'm sorry for us..")

            delay(3)

            lf()
            ln("I actually am.")

            delay(3)

            dlf()
            him()
            ln("What do you mean?")

            delay(3)

            dlf()
            cm("She laughs and looks over into the mirror")

            delay(3)

            dlf()
            her()
            ln("We look like complete idiots!")

            delay(2)

            lf()
            ln("I love it!")

            delay(3)

            lf()
            action {
                ac("Come on we have to go.") { goto("introduction/02") }
            }
        }
    }
}