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
import org.fernice.reflare.accommodation.scenes.title

class Introduction02 : Scene {
    override fun run(runner: SceneRunner) {
        runner.script {
            delay(1)

            dlf()
            title("Fall 2014")

            delay(5)

            ac { goto("introduction/03") }
        }
    }
}