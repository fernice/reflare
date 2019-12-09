/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

import java.awt.Insets

internal interface TInsets : Owned {
    val top: Float
    val right: Float
    val bottom: Float
    val left: Float
}

internal operator fun TInsets.plus(other: TInsets): TInsets {
    checkResourceOperationAccess(this, other)

    return ResourceContext.TInsets(
        top + other.top,
        right + other.right,
        bottom + other.bottom,
        left + other.left
    )
}

internal operator fun TInsets.minus(other: TInsets): TInsets {
    checkResourceOperationAccess(this, other)

    return ResourceContext.TInsets(
        top - other.top,
        right - other.right,
        bottom - other.bottom,
        left - other.left
    )
}

internal operator fun Insets.plusAssign(other: TInsets) {
    checkResourceOperationAccess(other)

    this.top += other.top.toInt()
    this.right = other.right.toInt()
    this.bottom = other.bottom.toInt()
    this.left = other.left.toInt()
}

internal fun TInsets.toAWTInsets(insets: Insets? = null): Insets {
    checkResourceAccess(this)

    val destination = insets ?: Insets(0, 0, 0, 0)

    destination.top = top.toInt()
    destination.right = right.toInt()
    destination.bottom = bottom.toInt()
    destination.left = left.toInt()

    return destination
}