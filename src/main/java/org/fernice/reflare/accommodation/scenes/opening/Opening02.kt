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

class Opening02Scene : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            him()
            ln("We should leave then.")

            delay(2)

            lf()
            ln("If they can't stop it now, how can we ever?")

            delay(3)

            lf()
            lf()
            her()
            ln("We can't just leave.")

            delay(3)

            lf()
            lf()
            him()
            ln("Why not?")

            delay(2)

            lf()
            ln("Why should we stay? I mean...")

            delay(2)

            ln("This world is coming to an end, no matter if we win this war or not.")

            delay(1)

            lf()
            ln("You know that.")

            delay(4)

            lf()
            lf()
            her()
            action {
                ac("There is nowhere were we could go!") { strip("emotional"); goto("opening/03") }
                ac("It will only tear us apart.") { obtain("emotional"); goto("opening/03-emotional") }
            }
        }
    }
}