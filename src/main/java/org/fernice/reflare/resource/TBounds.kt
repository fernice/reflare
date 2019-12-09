/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

import java.awt.Dimension

internal interface TBounds : Owned {

    val x: Float
    val y: Float
    val width: Float
    val height: Float

    companion object {

        fun fromDimension(dimension: Dimension): TBounds {
            return ResourceContext.TBounds(0f, 0f, dimension.width.toFloat(), dimension.height.toFloat())
        }
    }
}

internal operator fun TBounds.minus(insets: TInsets): TBounds {
    return ResourceContext.TBounds(
        x + insets.left,
        y + insets.top,
        width - insets.left - insets.right,
        height - insets.top - insets.bottom
    )
}