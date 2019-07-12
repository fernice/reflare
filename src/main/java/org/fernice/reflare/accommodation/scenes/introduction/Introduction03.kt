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
import org.fernice.reflare.accommodation.scenes.title

class Introduction03 : Scene {
    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("Come on! We're gonna be late!")
            delay(3)

            dlf()
            dc("rumbling noises")
            delay(3)

            dlf()
            cm("He sprints down the stairs. She is already waiting, handing him his purse.")
            delay(4)

            dlf()
            him()
            ln("I'm ready!")
            delay(3)

            dlf()
            cm("She smiles.")
            delay(3)

            dlf()
            her()
            action {
                ac("Come on..")
            }
        }
    }
}