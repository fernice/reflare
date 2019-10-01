/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.shape

import org.fernice.reflare.resource.ResourceContext
import org.fernice.reflare.resource.TBounds
import org.fernice.reflare.resource.TInsets
import org.fernice.reflare.resource.TRadii
import kotlin.math.max
import kotlin.math.min

internal operator fun TRadii.minus(insets: TInsets): TRadii {
    return ResourceContext.TRadii(
        max(topLeftWidth - insets.left, 0f),
        max(topLeftHeight - insets.top, 0f),
        max(topRightWidth - insets.right, 0f),
        max(topRightHeight - insets.top, 0f),
        max(bottomRightWidth - insets.right, 0f),
        max(bottomRightHeight - insets.bottom, 0f),
        max(bottomLeftWidth - insets.left, 0f),
        max(bottomLeftHeight - insets.bottom, 0f)
    )
}

internal infix fun TRadii.diminish(bounds: TBounds): TRadii {
    val actual = this actual bounds

    val topLeftRatio = min(min(actual.topLeftWidth ratio topLeftWidth, actual.topLeftHeight ratio topLeftHeight), 1f)
    val topRightRatio = min(min(actual.topRightWidth ratio topRightWidth, actual.topRightHeight ratio topRightHeight), 1f)
    val bottomRightRatio = min(min(actual.bottomRightWidth ratio bottomRightWidth, actual.bottomRightHeight ratio bottomRightHeight), 1f)
    val bottomLeftRatio = min(min(actual.bottomLeftWidth ratio bottomLeftWidth, actual.bottomLeftHeight ratio bottomLeftHeight), 1f)

    return ResourceContext.TRadii(
        topLeftWidth = topLeftWidth * topLeftRatio,
        topLeftHeight = topLeftHeight * topLeftRatio,
        topRightWidth = topRightWidth * topRightRatio,
        topRightHeight = topRightHeight * topRightRatio,
        bottomRightWidth = bottomRightWidth * bottomRightRatio,
        bottomRightHeight = bottomRightHeight * bottomRightRatio,
        bottomLeftWidth = bottomLeftWidth * bottomLeftRatio,
        bottomLeftHeight = bottomLeftHeight * bottomLeftRatio
    )
}

internal infix fun TRadii.actual(bounds: TBounds): TRadii {
    val topRatio = real(bounds.width / (topLeftWidth + topRightWidth))
    val rightRatio = real(bounds.height / (topRightHeight + bottomRightHeight))
    val bottomRatio = real(bounds.width / (bottomLeftWidth + bottomRightWidth))
    val leftRatio = real(bounds.height / (topLeftHeight + bottomLeftHeight))

    return ResourceContext.TRadii(
        topLeftWidth = topLeftWidth * topRatio,
        topRightWidth = topRightWidth * topRatio,
        topRightHeight = topRightHeight * rightRatio,
        bottomRightHeight = bottomRightHeight * rightRatio,
        bottomLeftWidth = bottomLeftWidth * bottomRatio,
        bottomRightWidth = bottomRightWidth * bottomRatio,
        topLeftHeight = topLeftHeight * leftRatio,
        bottomLeftHeight = bottomLeftHeight * leftRatio
    )
}

private fun real(value: Float): Float {
    return if (value.isInfinite() || value.isNaN()) 0f else value
}

private infix fun Float.ratio(second: Float): Float {
    return if (second != 0f) this / second else 0f
}