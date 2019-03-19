/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes.opening

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner
import org.fernice.reflare.accommodation.scenes.her
import org.fernice.reflare.accommodation.scenes.him

class Opening01Scene : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("I'm sorry for us...")

            delay(3)

            lf()
            lf()
            him()
            ln("What do you mean?")

            delay(3)

            lf()
            lf()
            her()
            ln("Haven't you seen the news?")
            delay(2)
            ln("The negotiations failed even before they had started.")
            delay(2)
            ln("They were not responding to our hails.")

            delay(2)

            lf()
            lf()
            him()
            ln("I don't think I can follow.")

            delay(2)

            lf()
            lf()
            her()
            ln("We won't have any other option.")
            delay(3)
            lf()
            ln("There will be war.")

            delay(4)

            lf()
            lf()
            her()
            action {
                ac("I wish it would not have come this far") { obtain("faith"); goto("opening/02") }
                ac("I'm sorry for us") { obtain("remorse"); goto("opening/02") }
            }
        }
    }
}