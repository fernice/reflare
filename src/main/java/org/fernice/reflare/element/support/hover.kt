/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element.support

import org.fernice.reflare.element.element
import java.awt.AWTEvent
import java.awt.Component
import java.awt.Container
import java.awt.Point
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import java.lang.ref.WeakReference
import javax.swing.SwingUtilities
import kotlin.math.min

object SharedHoverHandler : AWTEventListener {

    @JvmStatic
    fun initialize() {
    }

    init {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            this,
            AWTEvent.MOUSE_MOTION_EVENT_MASK or AWTEvent.MOUSE_EVENT_MASK
        )
    }

    private var component: WeakReference<Component>? = null

    override fun eventDispatched(event: AWTEvent) {
        fun <E> MutableList<E>.removeFirst(): E {
            return this.removeAt(0)
        }

        if (event.id == MouseEvent.MOUSE_EXITED && event is MouseEvent) {
            val component = event.component
            val window = SwingUtilities.getWindowAncestor(component)
            if (window != null && !window.isInside(event.locationOnScreen)) {
                for (exitedComponent in component.selfAndAncestorsIterator()) {
                    exitedComponent.element.isHovered = false
                }
                this.component = null
            }
            return
        }

        if (event.id != MouseEvent.MOUSE_MOVED || event !is MouseEvent || event.source !is Component) {
            return
        }

        val pick = if (event.source is Container) {
            val container = event.source as Container

            container.findComponentAt(event.point)
        } else {
            event.source as Component
        }

        if (pick == component) {
            return
        }

        val component = component?.get()
        this.component = pick?.let(::WeakReference)

        val componentStack = component.selfAndAncestorsList()
        val pickStack = pick.selfAndAncestorsList()

        val maxCommon = min(componentStack.size, pickStack.size)

        for (i in maxCommon until componentStack.size) {
            val exitedComponent = componentStack.removeFirst()

            exitedComponent.element.isHovered = false
        }

        for (i in maxCommon until pickStack.size) {
            val enteredComponent = pickStack.removeFirst()

            enteredComponent.element.isHovered = true
        }

        for (i in 0 until maxCommon) {
            val exitedComponent = componentStack.removeFirst()
            val enteredComponent = pickStack.removeFirst()

            if (exitedComponent == enteredComponent) {
                return
            } else {
                exitedComponent.element.isHovered = false
                enteredComponent.element.isHovered = true
            }
        }
    }
}

private fun Component?.selfAndAncestorsList(): MutableList<Component> {
    val stack: MutableList<Component> = mutableListOf()

    if (this == null) {
        return stack
    }

    for (component in this.selfAndAncestorsIterator()) {
        stack.add(component)
    }

    return stack
}

private fun Component.selfAndAncestorsIterator(): Iterator<Component> {
    return SelfAndAncestorIterator(this)
}

private class SelfAndAncestorIterator(private var component: Component) : Iterator<Component> {

    override fun hasNext(): Boolean {
        return component.parent != null && component.parent !is Window
    }

    override fun next(): Component {
        val current = component
        component = component.parent
        return current
    }
}

private fun Window.isInside(point: Point): Boolean {
    return point.x > x && point.y > y && point.x < x + width && point.y < y + height
}