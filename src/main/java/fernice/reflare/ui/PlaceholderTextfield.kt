/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.ui

import fernice.reflare.classes
import org.fernice.reflare.element.element
import org.fernice.reflare.ui.FlareBorder
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager2
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PlaceholderTextfield : JTextField() {

    private val placeholderLabel: JLabel

    init {
        layout = PlaceholderLayout()
        classes.add("placeholder-text")

        placeholderLabel = JLabel()
        placeholderLabel.classes.add("placeholder-label")
        placeholderLabel.text = "Username"
        add(placeholderLabel, PlaceholderLayout.PLACEHOLDER)

        document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                checkEmpty()
            }

            override fun insertUpdate(e: DocumentEvent) {
                checkEmpty()
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkEmpty()
            }

            fun checkEmpty() {
                if (text.isEmpty()) {
                    classes.remove("placeholder-text-filled")
                    element.restyleImmediately()
                } else {
                    classes.add("placeholder-text-filled")
                    element.restyleImmediately()
                }
            }
        })
    }
}

class PlaceholderLayout : LayoutManager2 {

    private var placeholder: Component? = null

    override fun invalidateLayout(target: Container) {}

    override fun layoutContainer(parent: Container) {
        val textfield = parent as? JTextField ?: return
        val placeholder = placeholder ?: return
        val border = parent.border as? FlareBorder ?: return

        val prefSize = placeholder.preferredSize
        val insets = textfield.insets
        val borderInsets = border.getMarginAndBorderInsets()

        val width = parent.width - insets.left - insets.right

        if (textfield.text.isEmpty()) {
            val parentHeight = textfield.height - borderInsets.top - borderInsets.bottom
            val offset = (parentHeight - prefSize.height) / 2

            placeholder.setBounds(insets.left, borderInsets.top + offset, width, prefSize.height)
        } else {
            placeholder.setBounds(insets.left, borderInsets.top, width, prefSize.height)
        }
    }

    override fun getLayoutAlignmentY(target: Container): Float = 0.5f
    override fun getLayoutAlignmentX(target: Container): Float = 0f

    override fun maximumLayoutSize(target: Container): Dimension {
        return placeholder?.maximumSize ?: Dimension()
    }

    override fun preferredLayoutSize(parent: Container): Dimension {
        return placeholder?.preferredSize ?: Dimension()
    }

    override fun minimumLayoutSize(parent: Container): Dimension {
        return placeholder?.minimumSize ?: Dimension()
    }

    override fun addLayoutComponent(comp: Component, constraints: Any?) {
        if (constraints is String) {
            addLayoutComponent(constraints, comp)
        }
    }

    override fun addLayoutComponent(name: String, comp: Component) {
        if (name == PLACEHOLDER) {
            placeholder = comp
        }
    }

    override fun removeLayoutComponent(comp: Component) {
        if (comp == placeholder) {
            placeholder = null
        }
    }

    companion object {

        const val PLACEHOLDER = "placeholder"
    }
}