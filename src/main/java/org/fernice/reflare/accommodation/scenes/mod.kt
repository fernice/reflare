/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation.scenes

import org.fernice.reflare.accommodation.scene.Script

suspend fun Script.him() {
    ln("[Him]") { color = context.palette.accent0 }
}

suspend fun Script.her() {
    ln("[Her]") { color = context.palette.accent1 }
}

suspend fun Script.dlf() {
    lf()
    lf()
}

suspend fun Script.dc(text: String) {
    ln("*$text*")
}

suspend fun Script.cm(text: String) {
    ln("# $text")
}

suspend fun Script.title(text: String) {
    ln(text) { size(30) }
}