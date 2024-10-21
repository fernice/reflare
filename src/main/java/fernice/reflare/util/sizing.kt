/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.util

import org.fernice.reflare.element.element
import java.awt.Component
import java.awt.Container
import javax.swing.JLabel
import javax.swing.JTextPane
import javax.swing.text.View

fun Component.preferredHeight(width: Int): Int {
    element.restyle()
    setSize(width, height)

    if (this is Container) layoutRecursively()

    sizeViews(this)
    try {
        return this.preferredSize.height
    } finally {
        resetViews(this)
    }
}

fun Container.layoutRecursively() {
    doLayout()
    for (index in 0 until componentCount) {
        val component = getComponent(index)
        if (component is Container) {
            component.layoutRecursively()
        }
    }
}

fun Component.validateRecursively() {
    validate()
    if (this is Container) {
        for (index in 0 until componentCount) {
            val component = getComponent(index)
            if (component is Container) {
                component.validateRecursively()
            }
        }
    }
}

private fun sizeViews(component: Component) {
    if (component is Container) {
        for (child in component.components) {
            sizeViews(child)
        }
    }
    if (component is JLabel) {
        val view = component.getClientProperty("html") as View?
        view?.setSize(component.getWidth().toFloat(), 0f)
    }
    if (component is JTextPane) {
        val view = component.ui.getRootView(component)
        view?.setSize(component.getWidth().toFloat(), 0f)
    }
}

private fun resetViews(component: Component) {
    if (component is Container) {
        for (child in component.components) {
            resetViews(child)
        }
    }
    if (component is JLabel) {
        val view = component.getClientProperty("html") as View?
        if (view != null) {
            val delegate = view.getView(0)
            view.setSize(delegate.getPreferredSpan(View.X_AXIS), delegate.getPreferredSpan(View.Y_AXIS))
        }
    }
    if (component is JTextPane) {
        val view = component.ui.getRootView(component)
        if (view != null) {
            val delegate = view.getView(0)
            view.setSize(delegate.getPreferredSpan(View.X_AXIS), delegate.getPreferredSpan(View.Y_AXIS))
        }
    }
}
