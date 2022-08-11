/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

import org.fernice.flare.panic
import org.fernice.logging.FLogging
import org.fernice.std.systemFlag
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

private val DUMP_RESOURCE_ALLOCATIONS = systemFlag("fernice.reflare.dumpResourceAllocations")

internal object ResourceAllocator {

    private val tInsets = ConcurrentLinkedQueue<TInsetsImpl>()

    fun allocateTInsets(
        top: Float,
        right: Float,
        bottom: Float,
        left: Float,

        owner: Owner
    ): TInsets {
        val instance = tInsets.poll() ?: TInsetsImpl()

        instance.top = top
        instance.right = right
        instance.bottom = bottom
        instance.left = left

        instance._owner = owner

        return instance
    }

    private fun deallocateTInsets(insets: TInsets) {
        val instance = insets as? TInsetsImpl ?: error("cannot deallocate an external implementation")

        instance._owner = null

        tInsets.offer(instance)
    }

    private val tColors = ConcurrentLinkedQueue<TColorsImpl>()

    fun allocateTColors(
        top: Color,
        right: Color,
        bottom: Color,
        left: Color,

        owner: Owner
    ): TColors {
        val instance = tColors.poll() ?: TColorsImpl(top, right, bottom, left)

        instance.top = top
        instance.right = right
        instance.bottom = bottom
        instance.left = left

        instance._owner = owner

        return instance
    }

    private fun deallocateTColors(colors: TColors) {
        val instance = colors as? TColorsImpl ?: error("cannot deallocate an external implementation")

        instance._owner = null

        tColors.offer(instance)
    }

    private val tRadii = ConcurrentLinkedQueue<TRadiiImpl>()

    fun allocateTRadii(
        topLeftWidth: Float,
        topLeftHeight: Float,
        topRightWidth: Float,
        topRightHeight: Float,
        bottomRightWidth: Float,
        bottomRightHeight: Float,
        bottomLeftWidth: Float,
        bottomLeftHeight: Float,

        owner: Owner
    ): TRadii {
        val instance = tRadii.poll() ?: TRadiiImpl(
            topLeftWidth,
            topLeftHeight,
            topRightWidth,
            topRightHeight,
            bottomRightWidth,
            bottomRightHeight,
            bottomLeftWidth,
            bottomLeftHeight
        )

        instance.topLeftWidth = topLeftWidth
        instance.topLeftHeight = topLeftHeight
        instance.topRightWidth = topRightWidth
        instance.topRightHeight = topRightHeight
        instance.bottomLeftWidth = bottomLeftWidth
        instance.bottomLeftHeight = bottomLeftHeight
        instance.bottomRightWidth = bottomRightWidth
        instance.bottomRightHeight = bottomRightHeight

        instance._owner = owner

        return instance
    }

    private fun deallocateTRadii(radii: TRadii) {
        val instance = radii as? TRadiiImpl ?: error("cannot deallocate an external implementation")

        instance._owner = null

        tRadii.offer(instance)
    }

    private val tBounds = ConcurrentLinkedQueue<TBoundsImpl>()

    fun allocateTBounds(
        x: Float,
        y: Float,
        width: Float,
        height: Float,

        owner: Owner
    ): TBounds {
        val instance = tBounds.poll() ?: TBoundsImpl(x, y, width, height)

        instance.x = x
        instance.y = y
        instance.width = width
        instance.height = height

        instance._owner = owner

        return instance
    }

    private fun deallocateTBounds(bounds: TBounds) {
        val instance = bounds as? TBoundsImpl ?: error("cannot deallocate an external implementation")

        instance._owner = null

        tBounds.offer(instance)
    }

    private val rectangles = ConcurrentLinkedQueue<Rectangle>()

    fun allocateRectangle(
        x: Int,
        y: Int,
        width: Int,
        height: Int,

        owner: Owner
    ): Rectangle {
        val rectangle = rectangles.poll() ?: Rectangle()

        rectangle.x = x
        rectangle.y = y
        rectangle.width = width
        rectangle.height = height

        return rectangle
    }

    private fun deallocateRectangle(rectangle: Rectangle) {
        rectangles.offer(rectangle)
    }

    private val dimensions = ConcurrentLinkedQueue<Dimension>()

    fun allocateDimension(width: Int, height: Int, owner: Owner): Dimension {
        val dimension = dimensions.poll() ?: Dimension()

        dimension.width = width
        dimension.height = height

        return dimension
    }

    private fun deallocateDimension(dimension: Dimension) {
        dimensions.offer(dimension)
    }

    fun deallocate(resource: Any, owner: Owner) {
        require(resource !is Owned || resource.owner == owner) { "cannot deallocate resource that is owned by a different context" }

        when (resource) {
            is TInsets -> deallocateTInsets(resource)
            is TColors -> deallocateTColors(resource)
            is TRadii -> deallocateTRadii(resource)
            is TBounds -> deallocateTBounds(resource)
            is Rectangle -> deallocateRectangle(resource)
            is Dimension -> deallocateDimension(resource)
            else -> error("cannot deallocate unknown resource: $resource")
        }
    }

    init {
        thread(start = DUMP_RESOURCE_ALLOCATIONS) {
            while (true) {
                Thread.sleep(5000)

                LOG.trace("TInsets: ${tInsets.size}  TColors: ${tColors.size}  TRadii: ${tRadii.size}  TBounds: ${tBounds.size}  Rectangle: ${rectangles.size}  Dimension: ${dimensions.size}")
            }
        }
    }

    private val LOG = FLogging.logger { }
}

private data class TInsetsImpl(
    override var top: Float = 0f,
    override var right: Float = 0f,
    override var bottom: Float = 0f,
    override var left: Float = 0f
) : TInsets {

    internal var _owner: Owner? = null
    override val owner: Owner
        get() = _owner ?: panic("accessing resource without an owner")
}

private data class TColorsImpl(
    override var top: Color,
    override var right: Color,
    override var bottom: Color,
    override var left: Color
) : TColors {

    internal var _owner: Owner? = null
    override val owner: Owner
        get() = _owner ?: panic("accessing resource without an owner")
}

private data class TRadiiImpl(
    override var topLeftWidth: Float,
    override var topLeftHeight: Float,
    override var topRightWidth: Float,
    override var topRightHeight: Float,
    override var bottomRightWidth: Float,
    override var bottomRightHeight: Float,
    override var bottomLeftWidth: Float,
    override var bottomLeftHeight: Float
) : TRadii {

    internal var _owner: Owner? = null
    override val owner: Owner
        get() = _owner ?: panic("accessing resource without an owner")
}

private data class TBoundsImpl(
    override var x: Float,
    override var y: Float,
    override var width: Float,
    override var height: Float
) : TBounds {

    internal var _owner: Owner? = null
    override val owner: Owner
        get() = _owner ?: panic("accessing resource without an owner")
}