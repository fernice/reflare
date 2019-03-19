/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.still

import org.fernice.reflare.accommodation.scene.Scene
import org.fernice.reflare.accommodation.scene.SceneRunner

class AboutStill : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            ln("this is a game.")

            delay(2)

            action {
                ac("Forgo") { System.exit(0) }
                ac("Again") { goto("intro") }
            }
        }
    }
}