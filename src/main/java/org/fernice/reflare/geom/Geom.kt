package org.fernice.reflare.geom

import java.awt.Color
import java.awt.Dimension


data class Point(
    val x: Float,
    val y: Float
) {

    companion object {

        private val zero by lazy { Point(0f, 0f) }

        fun zero(): Point {
            return zero
        }
    }
}

@Deprecated(message = "use TRadii instead")
data class Radii(
    val topLeftWidth: Float,
    val topLeftHeight: Float,
    val topRightWidth: Float,
    val topRightHeight: Float,
    val bottomRightWidth: Float,
    val bottomRightHeight: Float,
    val bottomLeftWidth: Float,
    val bottomLeftHeight: Float
)

@Deprecated(message = "use TInsets instead")
data class Insets(
    val top: Float,
    val right: Float,
    val bottom: Float,
    val left: Float
) {

    operator fun plus(insets: Insets): Insets {
        return Insets(
            top + insets.top,
            right + insets.right,
            bottom + insets.bottom,
            left + insets.left
        )
    }

    fun isZero(): Boolean {
        return top == 0f && right == 0f && bottom == 0f && left == 0f
    }

    companion object {

        private val empty: Insets by lazy { Insets(0f, 0f, 0f, 0f) }

        fun empty(): Insets {
            return empty
        }
    }
}

@Deprecated(message = "use TBounds instead")
data class Bounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {

    fun reduce(insets: Insets): Bounds {
        return Bounds(
            x + insets.left,
            y + insets.top,
            width - insets.left - insets.right,
            height - insets.top - insets.bottom
        )
    }

    operator fun minus(insets: Insets): Bounds {
        return Bounds(
            x + insets.left,
            y + insets.top,
            width - insets.left - insets.right,
            height - insets.top - insets.bottom
        )
    }

    companion object {

        fun fromDimension(dimension: Dimension): Bounds {
            return Bounds(
                0f,
                0f,
                dimension.width.toFloat(),
                dimension.height.toFloat()
            )
        }
    }
}

@Deprecated(message = "use TColors instead")
data class Colors(
    val top: Color,
    val right: Color,
    val bottom: Color,
    val left: Color
)