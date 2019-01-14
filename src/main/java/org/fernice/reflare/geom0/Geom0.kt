/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.geom0

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double) : Comparable<Point> {

    operator fun minus(point: Point): Point {
        return Point(x - point.x, y - point.y)
    }

    operator fun minus(vec: Vec): Point {
        return Point(x - vec.x, y - vec.y)
    }

    operator fun plus(vec: Vec): Point {
        return Point(x + vec.x, y + vec.y)
    }

    operator fun times(vec: Vec): Point {
        return Point(x * vec.x, y * vec.y)
    }

    val distance: Double by lazy {
        sqrt(x * x + y * y)
    }

    fun normalized(): Point {
        val x = x / distance
        val y = y / distance
        return Point(x, y)
    }

    fun rotatedAround(other: Point, angle: Double): Point {
        val theta = Math.toRadians(angle)
        val x = x - other.x
        val y = y - other.y

        return Point(
            (cos(theta) * x - sin(theta) * y) + other.x,
            (sin(theta) * x + cos(theta) * y) + other.y
        )
    }

    fun toLine(angle: Double): Line {
        val vec = Vec.normalized(cos(angle), sin(angle))

        val second = this * vec

        return Line(this, second)
    }

    fun toLine(direction: Vec): Line {
        return Line(this, this * direction)
    }

    override fun compareTo(other: Point): Int {
        val c = x.compareTo(other.x)

        return if (c != 0) {
            c
        } else {
            y.compareTo(other.y)
        }
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    companion object {

        fun normalized(x: Double, y: Double): Point {
            val distance = sqrt(x * x + y * y)
            return Point(x / distance, y / distance)
        }
    }
}

data class Line(val first: Point, val second: Point) {

    val normal: Vec by lazy {
        Vec.normalized(
            second.y - first.y,
            first.x - second.x
        )
    }

    val direction: Vec by lazy {
        Vec.normalized(
            second.x - first.x,
            second.y - first.y
        )
    }

    fun height(x: Double): Double {
        val difference = x - first.x

        return (first + direction * difference).y
    }

    val angle: Double by lazy {
        atan2(
            second.y - first.y,
            second.x - first.x
        )
    }

    fun intersection(line: Line): Point {
        val scalarFirst = this.normal x this.first
        val scalarSecond = line.normal x line.first

        val delta = this.normal.x * line.normal.y - line.normal.x * this.normal.y
        // If lines are parallel, intersection point will contain infinite values
        return Point(
            (line.normal.y * scalarFirst - this.normal.y * scalarSecond) / delta,
            (this.normal.x * scalarSecond - line.normal.x * scalarFirst) / delta
        )
    }

    override fun toString(): String {
        return "[$first - $second]"
    }
}

data class Vec(val x: Double, val y: Double) {

    val normal: Vec by lazy {
        Vec(y, x)
    }

    val distance: Double by lazy {
        sqrt(x * x + y + y)
    }

    fun normalized(): Vec {
        val x = x / distance
        val y = y / distance
        return Vec(x, y)
    }

    operator fun plus(other: Vec): Vec {
        return Vec(x + other.x, y + other.y)
    }

    operator fun times(scalar: Double): Vec {
        return Vec(x * scalar, y * scalar)
    }

    operator fun div(scalar: Double): Vec {
        return Vec(x / scalar, y / scalar)
    }

    infix fun x(other: Point): Double {
        return this.x * other.x + this.y * other.y
    }

    companion object {

        fun normalized(x: Double, y: Double): Vec {
            val length = sqrt(x * x + y * y)
            return Vec(x / length, y / length)
        }
    }
}