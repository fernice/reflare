/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element

import org.fernice.flare.EngineContext
import org.fernice.flare.dom.Element
import java.awt.Component
import java.awt.Container
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JMenuItem


abstract class AWTContainerElement(container: Container) : AWTComponentElement(container) {

    private val _children: MutableList<AWTComponentElement> = CopyOnWriteArrayList()
    override val children: List<AWTComponentElement> get() = _children

    init {
        container.addContainerListener(object : ContainerListener {
            override fun componentAdded(e: ContainerEvent) {
                childAdded(e.child)
            }

            override fun componentRemoved(e: ContainerEvent) {
                childRemoved(e.child)
            }
        })

        for (child in container.components) {
            childAdded(child)
        }
    }

    private fun childAdded(child: Component) {
        val childElement = child.element

        val container = component as Container
        val index = container.getComponentZOrder(child)

        val oldFrame = childElement.frame

        childElement.frame = frame
        childElement.parent = this
        _children.add(index, childElement)

        // If the child had no parent before this and it is going to have
        // one now, then we have to check the style state of the child.
        // If it has already been marked as dirty, apply the styles
        // immediately, because calls to reapplyCss() won't request next
        // pulse, as it is only done if the style state is clean.
        //
        // Also if the child is a JMenuItem apply style immediately
        // independently of the current style state.
        if (oldFrame == null && frame != null && childElement.cssFlag != StyleState.CLEAN || child is JMenuItem) {
            childElement.applyCSS(origin = "child:added")
        } else {
            childElement.reapplyCSS(origin = "child:added")
        }
    }

    private fun childRemoved(child: Component) {
        val childElement = child.element

        childElement.frame = null
        childElement.parent = null
        _children.remove(childElement)

        //traceReapplyOrigin("child:removed")
        //reapplyCSS()
    }

    final override fun isEmpty(): Boolean = children.isEmpty()

    override val firstChild: Element?
        get() = children.firstOrNull()

    override val lastChild: Element?
        get() = children.lastOrNull()

    final override fun parentChanged(old: Frame?, new: Frame?) {
        for (child in children) {
            child.frame = new
        }
    }

    final override fun processCSS(context: EngineContext) {
        if (cssFlag == StyleState.CLEAN) return

        super.processCSS(context)

        for (child in children) {
            val childParent = child.parent
            if (childParent != null && childParent != this) {
                continue
            }

            child.processCSS(context)
        }
    }
}
