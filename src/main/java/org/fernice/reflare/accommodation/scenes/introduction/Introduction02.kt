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

class Introduction02 : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("Have you seen my purse?")
            delay(2)

            dlf()
            cm("She walks out of the bedroom.")
            delay(4)

            dlf()
            her()
            ln("It has got to be here somewhere.")
            delay(3)

            dlf()
            him()
            ln("Have you taken a look downstairs?")
            delay(2)

            dlf()
            her()
            ln("No, i thought i brought it upstairs with me.")
            delay(2)

            dlf()
            her()
            action {
                ac("Could you please take a look.") { goto("introduction/03") }
            }
        }
    }
}