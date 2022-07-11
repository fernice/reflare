/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render

import org.fernice.flare.panic
import org.fernice.flare.style.value.computed.Angle
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.ColorStop
import org.fernice.flare.style.value.computed.Gradient
import org.fernice.flare.style.value.computed.GradientItem
import org.fernice.flare.style.value.computed.GradientKind
import org.fernice.flare.style.value.computed.LengthOrPercentage
import org.fernice.flare.style.value.computed.LineDirection
import org.fernice.flare.style.value.computed.RGBAColor
import org.fernice.flare.style.value.specified.X
import org.fernice.flare.style.value.specified.Y
import org.fernice.reflare.geom0.Point
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.util.peekIterator
import java.awt.Color
import java.awt.Dimension
import java.awt.LinearGradientPaint
import java.awt.MultipleGradientPaint
import kotlin.math.abs
import java.awt.Point as AWTPoint

fun BackgroundLayer.Gradient.Companion.computeGradient(gradient: Gradient, size: Dimension): BackgroundLayer {
    val (start, end) = when (val kind = gradient.kind) {
        is GradientKind.Linear -> {
            when (val direction = kind.lineDirection) {
                is LineDirection.Corner -> {
                    size.pointAt(direction.x.opposite(), direction.y.opposite()) to size.pointAt(direction.x, direction.y)
                }
                is LineDirection.Horizontal -> {
                    size.pointAt(direction.x.opposite(), null) to size.pointAt(direction.x, null)
                }
                is LineDirection.Vertical -> {
                    size.pointAt(null, direction.y.opposite()) to size.pointAt(null, direction.y)
                }
                is LineDirection.Angle -> {
                    val angle = Angle(direction.angle.degrees() + 90)

                    val degrees = abs(angle.degrees()) % 360
                    val (startCorner, endCorner) = when {
                        degrees > 0.0 && degrees <= 90.0 -> Point(size.width.toDouble(), size.height.toDouble()) to Point(0.0, 0.0)
                        degrees > 90.0 && degrees <= 180.0 -> Point(0.0, size.height.toDouble()) to Point(size.width.toDouble(), 0.0)
                        degrees > 180.0 && degrees <= 270.0 -> Point(0.0, 0.0) to Point(size.width.toDouble(), size.height.toDouble())
                        degrees > 270.0 && degrees < 360.0 || degrees == 0f -> Point(size.width.toDouble(), 0.0) to Point(0.0, size.height.toDouble())
                        else -> panic("$degrees degrees")
                    }

                    val midpoint = Point(size.width / 2.0, size.height / 2.0)

                    val line = midpoint.toLine(angle.radians64())
                    val normal = line.normal

                    val startLine = startCorner.toLine(normal)
                    val endLine = endCorner.toLine(normal)

                    val start = startLine.intersection(line)
                    val end = endLine.intersection(line)

                    start to end
                }
            }
        }
        is GradientKind.Radial -> TODO()
    }

    val containingLength = abs((end - start).distance).toFloat()
    val (colors, fractions) = computeColorFractions(gradient.items, containingLength)
    val cycleMethod = if (gradient.repeating) MultipleGradientPaint.CycleMethod.REPEAT else MultipleGradientPaint.CycleMethod.NO_CYCLE

    val gradientPaint = when (gradient.kind) {
        is GradientKind.Linear -> LinearGradientPaint(
            start.toAWTPoint(),
            end.toAWTPoint(),
            fractions,
            colors,
            cycleMethod
        )
        is GradientKind.Radial -> TODO()
    }

    return BackgroundLayer.Gradient(gradientPaint)
}

private fun Dimension.pointAt(x: X?, y: Y?): Point {
    return Point(
        x?.let { width.toDouble().takeUnless { x.isStart() } ?: 0.0 } ?: width / 2.0,
        y?.let { height.toDouble().takeUnless { y.isStart() } ?: 0.0 } ?: height / 2.0
    )
}

private fun Point.toAWTPoint(): AWTPoint {
    return AWTPoint(this.x.toInt(), this.y.toInt())
}

private fun computeColorFractions(gradientItems: List<GradientItem>, containingLength: Float): Pair<Array<Color>, FloatArray> {
    val gradientStops: MutableList<GradientStop> = mutableListOf()
    val iterator = gradientItems.peekIterator()

    var intervalStart = iterator.next().expectColorStop()
    var startPosition = (intervalStart.position ?: LengthOrPercentage.zero).toPercentage(containingLength)
    var lastPosition = startPosition

    gradientStops.add(GradientStop(intervalStart.color, startPosition))

    loop@
    while (iterator.hasNext()) {
        val blankStops: MutableList<ColorStop> = mutableListOf()
        blanks@
        do {
            val next = iterator.peek()

            if (next is GradientItem.ColorStop) {
                if (next.colorStop.position == null) {
                    blankStops.add(next.colorStop)
                } else {
                    break@blanks
                }
            }

            iterator.next()
        } while (iterator.hasNext())

        val intervalEnd = when {
            iterator.hasNext() -> iterator.next().expectColorStop()
            blankStops.isNotEmpty() -> blankStops.removeAt(blankStops.lastIndex)
            else -> break@loop
        }
        var endPosition = (intervalEnd.position ?: LengthOrPercentage.Hundred).toPercentage(containingLength)

        if (endPosition < lastPosition) {
            endPosition = lastPosition + 0.00001f
        } else if (lastPosition == endPosition) {
            endPosition += 0.00001f
        }

        if (blankStops.isNotEmpty()) {
            val distance = endPosition - startPosition
            val section = distance / (blankStops.size + 1)

            for ((i, blankStop) in blankStops.withIndex()) {
                gradientStops.add(GradientStop(blankStop.color, startPosition + section + (section * i)))
            }
        }

        gradientStops.add(GradientStop(intervalEnd.color, endPosition))

        lastPosition = endPosition
        intervalStart = intervalEnd
        startPosition = (intervalStart.position ?: LengthOrPercentage.zero).toPercentage(containingLength)

        if (!iterator.hasNext()) {
            break
        }
    }

    val colors = gradientStops.map(GradientStop::color).map(RGBAColor::toAWTColor).toTypedArray()
    val fractions = gradientStops.map(GradientStop::position).toFloatArray()

    return colors to fractions
}

private fun GradientItem.expectColorStop(): ColorStop {
    return when (this) {
        is GradientItem.ColorStop -> this.colorStop
        else -> panic("expecting color stop")
    }
}

private fun LengthOrPercentage.toPercentage(containingLength: Float): Float {
    return when (this) {
        is LengthOrPercentage.Percentage -> this.percentage.value
        is LengthOrPercentage.Calc -> {
            val length = this.calc.toPixelLength(Au.fromPx(containingLength))  ?: error("calculation should have resulted in length")

            length.px() / containingLength
        }
        is LengthOrPercentage.Length -> {
            this.length.px() / containingLength
        }
    }
}

private data class GradientStop(val color: RGBAColor, val position: Float)

