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

class Opening03Scene : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            him()
            ln("You know were we can go.")

            delay(2)

            lf()
            ln("No one will finds there. Will be save.")

            delay(4)

            lf()
            ln("But if we don't go now then, yes, they will find us.")

            delay(4)

            lf()
            ln("And I")
            delay(3)
            ln("I cannot loose this chance. This chance of having at least a few")
            ln("weeks more with you. I'm not ready to give up on us yet.")

            delay(6)

            lf()
            lf()
            her()
            action {
                ac("But this world?") { obtain("anger") }
                ac("But we could still fight for those weeks!")
            }

            delay(3)

            lf()
            lf()
            him()
            ln("I doesn't make a difference anymore.")

            delay(3)

            lf()
            ln("Many people will die a meaningless death. You know that the")
            ln("negotiations were our only hope. We can't change any of this.")
            ln("Only they could have.")

            delay(6)

            lf()
            lf()
            her()
            action {
                ac("It still doesn't justify our actions") { obtain("rigorous") }
                ac("We could at least try, like they all will.") { goto("credits") }
            }
        }
    }
}

class Opening03EmotionalScene : Scene {

    override fun run(runner: SceneRunner) {
        runner.script {
            her()
            ln("It will only tear us apart.")

            delay(2)

            lf()
            ln("And I don't...")

            delay(1)

            lf()
            lf()
            him()
            ln("No, it will not. We will get through this together.")

            delay(1)

            lf()
            lf()
            her()
            action {
                ac("I don't think I can take it.") { }
                ac("Through what? There won't be anything left!") { obtain("anger") }
                ac("*silence*") { }
            }

            delay(2)

            lf()
            lf()
            him()
            ln("You can do it. We can do it!")

            delay(2)

            lf()
            ln("We can leave before things go bad.")
            delay(2)
            ln("But we have to leave now.")

            delay(2)

            lf()
            lf()
            her()
            action {
                ac("There is nowhere were we could go!") { goto("opening/03") }
                ac("They won't let us leave.") { goto("opening/03") }
            }
        }
    }
}