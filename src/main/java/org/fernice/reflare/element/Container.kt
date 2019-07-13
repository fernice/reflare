/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element

import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import org.fernice.flare.dom.Element
import java.awt.Component
import java.awt.Container
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.util.concurrent.CopyOnWriteArrayList


open class AWTContainerElement(container: Container) : AWTComponentElement(container) {

    private val children: MutableList<AWTComponentElement> = CopyOnWriteArrayList()

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

        childElement.frame = frame
        childElement.parent = Some(this)
        children.add(index, childElement)

        invalidateStyle()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.element

        childElement.frame = None
        childElement.parent = None
        children.remove(childElement)

        invalidateStyle()
    }

    fun addVirtualChild(childElement: AWTComponentElement) {
        childElement.frame = frame
        childElement.parent = Some(this)
    }

    fun removeVirtualChild(childElement: AWTComponentElement) {
        childElement.frame = None
        childElement.parent = None
    }

    final override fun parentChanged(old: Option<Frame>, new: Option<Frame>) {
        for (child in children) {
            child.frame = new
        }
    }

    override fun children(): List<Element> {
        return children
    }

    override fun previousSibling(): Option<Element> {
        val parent = parent()

        return when (parent) {
            is Some -> {
                val children = parent.value.children()

                val index = children.indexOf(this) - 1

                if (index >= 0) {
                    Some(children[index])
                } else {
                    None
                }
            }
            is None -> parent
        }
    }

    override fun nextSibling(): Option<Element> {
        val parent = parent()

        return when (parent) {
            is Some -> {
                val children = parent.value.children()

                val index = children.indexOf(this) + 1

                if (index < children.size) {
                    Some(children[index])
                } else {
                    None
                }
            }
            is None -> parent
        }
    }

    override fun isEmpty(): Boolean {
        return children.isEmpty()
    }

    // ***************************** Matching ***************************** //

    // In theory it is possible to construct a Container meaning that needs a
    // local name to styled. In practice hopefully no one will try to do it
    // because even though the element will be considered when it comes to
    // matching, we have no means to render using its computed styles.
    override fun localName(): String {
        return "container"
    }
}