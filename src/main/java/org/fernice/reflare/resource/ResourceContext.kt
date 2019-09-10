/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

import org.fernice.flare.panic
import java.awt.Color
import java.awt.Rectangle

internal class ResourceContext {

    private val owner = ResourceContextOwner()

    private var parentContext: ResourceContext? = null
    private var ownerThread: Thread? = null

    fun enter() {
        ownerThread = Thread.currentThread()
        parentContext = currentContext.get()
        currentContext.set(this)
    }

    fun leave() {
        currentContext.set(parentContext)
        parentContext = null
        ownerThread = null

        deallocateAll()
    }

    private val allocatedResources = mutableListOf<Any>()

    private inline fun <T : Any> allocate(allocator: (Owner) -> T): T {
        val resource = allocator(owner)
        allocatedResources.add(resource)
        return resource
    }

    private fun deallocateAll() {
        val allocatedResourcesCopy = allocatedResources.toList()

        allocatedResourcesCopy.forEach(::deallocate)
    }

    private fun deallocate(resource: Any) {
        require(allocatedResources.remove(resource)) { "cannot deallocate resource that was not allocated by this context" }

        ResourceAllocator.deallocate(resource, owner)
    }

    private inner class ResourceContextOwner : Owner {
        override val thread: Thread
            get() = ownerThread ?: panic("accessing resource with an unbound resource context")

        override fun toString(): String {
            return "ResourceContext:Owner[thread=$ownerThread]"
        }
    }

    companion object {

        private val currentContext = ThreadLocal<ResourceContext>()

        fun getCurrentContext(): ResourceContext {
            return this.currentContext.get() ?: error("cannot allocate resources without resource context")
        }

        fun isWithinContext(): Boolean {
            return this.currentContext.get() != null
        }

        internal fun TInsets(top: Float = 0f, right: Float = 0f, bottom: Float = 0f, left: Float = 0f): TInsets {
            return getCurrentContext().allocate { owner ->
                ResourceAllocator.allocateTInsets(top, right, bottom, left, owner)
            }
        }

        internal fun TColors(top: Color, right: Color, bottom: Color, left: Color): TColors {
            return getCurrentContext().allocate { owner ->
                ResourceAllocator.allocateTColors(top, right, bottom, left, owner)
            }
        }

        internal fun TBounds(
            x: Float,
            y: Float,
            width: Float,
            height: Float
        ): TBounds {
            return getCurrentContext().allocate { owner ->
                ResourceAllocator.allocateTBounds(x, y, width, height, owner)
            }
        }

        internal fun TRadii(
            topLeftWidth: Float,
            topLeftHeight: Float,
            topRightWidth: Float,
            topRightHeight: Float,
            bottomRightWidth: Float,
            bottomRightHeight: Float,
            bottomLeftWidth: Float,
            bottomLeftHeight: Float
        ): TRadii {
            return getCurrentContext().allocate { owner ->
                ResourceAllocator.allocateTRadii(
                    topLeftWidth,
                    topLeftHeight,
                    topRightWidth,
                    topRightHeight,
                    bottomRightWidth,
                    bottomRightHeight,
                    bottomLeftWidth,
                    bottomLeftHeight,
                    owner
                )
            }
        }

        internal fun Rectangle(x: Int = 0, y: Int = 0, width: Int = 0, height: Int = 0): Rectangle {
            return getCurrentContext().allocate { owner ->
                ResourceAllocator.allocateRectangle(x, y, width, height, owner)
            }
        }
    }
}

internal inline fun <T> withResourceContext(block: ResourceContext.() -> T): T {
    val resourceContext = ResourceContext()
    resourceContext.enter()
    try {
        return resourceContext.block()
    } finally {
        resourceContext.leave()
    }
}

internal inline fun <T> withAuxiliaryResourceContext(block: ResourceContext.() -> T): T {
    return if (ResourceContext.isWithinContext()) {
        ResourceContext.getCurrentContext().block()
    } else {
        val resourceContext = ResourceContext()
        resourceContext.enter()
        try {
            resourceContext.block()
        } finally {
            resourceContext.leave()
        }
    }
}