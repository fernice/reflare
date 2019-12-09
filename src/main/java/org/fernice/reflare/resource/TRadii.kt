/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

internal interface TRadii : Owned {
    val topLeftWidth: Float
    val topLeftHeight: Float
    val topRightWidth: Float
    val topRightHeight: Float
    val bottomRightWidth: Float
    val bottomRightHeight: Float
    val bottomLeftWidth: Float
    val bottomLeftHeight: Float
}