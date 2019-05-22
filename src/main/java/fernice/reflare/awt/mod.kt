/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.awt

import java.awt.Insets

operator fun Insets.plus(other: Insets): Insets {
    return Insets(this.top + other.top, this.left + other.left, this.bottom + other.bottom, this.right + other.right)
}

operator fun Insets.plusAssign(other: Insets) {
    this.top += other.top
    this.left += other.left
    this.bottom += other.bottom
    this.right += other.right
}