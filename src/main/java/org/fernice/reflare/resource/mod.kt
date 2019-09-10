/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

import org.fernice.flare.cssparser.RGBA
import org.fernice.flare.style.properties.stylestruct.Border
import org.fernice.flare.style.properties.stylestruct.Margin
import org.fernice.flare.style.properties.stylestruct.Padding
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.Style
import org.fernice.reflare.toAWTColor
import java.awt.Rectangle

@RequiresResourceContext
internal fun Margin.toTInsets(bounds: Rectangle): TInsets {
    return ResourceContext.TInsets(
        this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

@RequiresResourceContext
internal fun Padding.toTInsets(bounds: Rectangle): TInsets {
    return ResourceContext.TInsets(
        this.top.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.right.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottom.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.left.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}

@RequiresResourceContext
internal fun Border.toTInsets(): TInsets {
    return ResourceContext.TInsets(
        if (topStyle != Style.None) this.topWidth.length.px() else 0f,
        if (rightStyle != Style.None) this.rightWidth.length.px() else 0f,
        if (bottomStyle != Style.None) this.bottomWidth.length.px() else 0f,
        if (leftStyle != Style.None) this.leftWidth.length.px() else 0f
    )
}

@RequiresResourceContext
internal fun Border.toTColors(currentColor: RGBA): TColors {
    return ResourceContext.TColors(
        this.topColor.toAWTColor(currentColor),
        this.rightColor.toAWTColor(currentColor),
        this.bottomColor.toAWTColor(currentColor),
        this.leftColor.toAWTColor(currentColor)
    )
}

@RequiresResourceContext
internal fun Border.toTRadii(bounds: Rectangle): TRadii {
    return ResourceContext.TRadii(
        this.topLeftRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.topLeftRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.topRightRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.topRightRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottomRightRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottomRightRadius.height.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottomLeftRadius.width.toPixelLength(Au.fromPx(bounds.width)).px(),
        this.bottomLeftRadius.height.toPixelLength(Au.fromPx(bounds.width)).px()
    )
}