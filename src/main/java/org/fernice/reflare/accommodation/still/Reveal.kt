/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.still

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner

class RevealStill : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            lf()
            ln("[Him]") { color = context.palette.accent0 }
            ln("Have see the news? This madness! Pure madness!")

            delay(1)

            lf()
            ln("Lea?")

            delay(1)

            lf()
            ln("Lea?")

            delay(2)

            lf()
            ln("[Lea]") { color = context.palette.accent1 }
            ln("I'm here...")
        }
    }
}