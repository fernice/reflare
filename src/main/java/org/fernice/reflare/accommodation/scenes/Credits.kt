/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner

class CreditsScene : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            ln("This was Accommodations")

            delay(2)

            lf()
            ln("A text-based Story of a world facing inevitable death")
            ln("expressed through the eyes of the very few two who")
            ln("ultimately unwillingly caused the impending doom.")

            delay(6)

            lf()
            lf()
            ln("A story by Iven Krall")

            delay(4)

            lf()
            lf()
            ln("In memory of Ulrich Krall")

            delay(6)

            lf()
            ln("This is my way of saying goodbye")

            delay(6)

            lf()
            lf()
            action {
                having("anger", 2) {
                    ac("Forgo") { System.exit(-1) }
                }
                ac("Exit") { System.exit(0) }
                ac("Again") { goto("intro") }
            }
        }
    }
}