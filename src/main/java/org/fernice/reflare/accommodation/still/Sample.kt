/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.still

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner

class IntroStill : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            ln("The sky is falling in.")

            delay(1)

            lf()
            ln("A world came to an end.")

            delay(1)

            lf()
            ln("[Him]") { color = context.palette.accent0 }
            ln("I believed it would never come this far.")

            delay(1)

            lf()
            ln("[Her]") { color = context.palette.accent1 }

            action {
                ac("We can't change what happened.") { goto("reveal") }
                ac("We never had the chance to stop it.") { obtain("spirit", "guided") }
            }

            lf()
            ln("[Him]") { color = context.palette.accent0 }
            ln("I believed it would never come this far.")

            delay(1)

            lf()
            ln("[Her]") { color = context.palette.accent1 }
            action {
                having("spirit", "guided") {
                    ac("We should have") { goto("recap/intro") }
                }

                ac("*silence*") { goto("recap/intro") }
                ac("I know") { obtain("helpless"); goto("help/about") }
            }
        }
    }
}