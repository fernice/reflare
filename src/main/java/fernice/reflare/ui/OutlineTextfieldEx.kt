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
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class FTextField : JTextField() {

    var placeholder: String
        get() = placeholderLabel.text
        set(value) {
            placeholderLabel.text = value
        }

    private val placeholderLabel: JLabel

    init {
        layout = OutlinedLayout()
        classes.add("outlined")
        columns = 20

        placeholderLabel = JLabel()
        placeholderLabel.classes.add("outlined-placeholder")
        add(placeholderLabel, OutlinedLayout.PLACEHOLDER)

        installPlaceholderHandling(this)
    }
}

class FPasswordField : JPasswordField() {

    var placeholder: String
        get() = placeholderLabel.text
        set(value) {
            placeholderLabel.text = value
        }

    private val placeholderLabel: JLabel

    init {
        layout = OutlinedLayout()
        classes.add("outlined")
        columns = 20

        placeholderLabel = JLabel()
        placeholderLabel.classes.add("outlined-placeholder")
        add(placeholderLabel, OutlinedLayout.PLACEHOLDER)

        installPlaceholderHandling(this)
    }
}

class FTextArea : JTextArea() {

    var placeholder: String
        get() = placeholderLabel.text
        set(value) {
            placeholderLabel.text = value
        }

    private val placeholderLabel: JLabel

    init {
        layout = OutlinedLayout()
        classes.add("outlined")
        columns = 20

        placeholderLabel = JLabel()
        placeholderLabel.classes.add("outlined-placeholder")
        add(placeholderLabel, OutlinedLayout.PLACEHOLDER)

        installPlaceholderHandling(this)
    }
}

private fun installPlaceholderHandling(component: JTextComponent) {
    val updateState = {
        if (component.text.isEmpty() && !component.hasFocus()) {
            component.classes.remove("outlined-filled")
            component.element.reapplyCSS()
        } else {
            component.classes.add("outlined-filled")
            component.element.reapplyCSS()
        }
    }

    component.document.addDocumentListener(object : DocumentListener {
        override fun changedUpdate(e: DocumentEvent?) = updateState()
        override fun insertUpdate(e: DocumentEvent?) = updateState()
        override fun removeUpdate(e: DocumentEvent?) = updateState()
    })

    component.addFocusListener(object : FocusListener {
        override fun focusLost(e: FocusEvent?) = updateState()
        override fun focusGained(e: FocusEvent?) = updateState()
    })
}

class OutlinedLayout : LayoutManager2 {

    private var placeholder: Component? = null

    override fun invalidateLayout(target: Container) {}

    override fun layoutContainer(parent: Container) {
        val component = parent as? JComponent ?: return
        val placeholder = placeholder as? Container ?: return
        val border = component.border as? FlareBorder ?: return

        val borderInsets = border.getMarginAndBorderInsets()

        val bounds = parent.bounds
        val insets = parent.insets

        val height = bounds.height - insets.top - insets.bottom

        val size = placeholder.preferredSize

        if (isActive(parent)) {
            val placeholderInsets = placeholder.insets

            placeholder.setBounds(insets.left - placeholderInsets.left, borderInsets.top - size.height / 2 - 3, size.width, size.height)
        } else {
            placeholder.setBounds(insets.left, insets.top, size.width, size.height)
        }
    }

    fun isActive(parent: Container): Boolean {
        return parent.hasFocus() || (parent as? JTextComponent)?.text?.isNotEmpty() == true
    }

    override fun getLayoutAlignmentY(target: Container): Float = 0.5f
    override fun getLayoutAlignmentX(target: Container): Float = 0f

    override fun maximumLayoutSize(parent: Container): Dimension {
        val insets = parent.insets
        val size = parent.maximumSize
        size.width += insets.left + insets.right
        size.height += insets.top + insets.bottom

        return size
    }

    override fun preferredLayoutSize(parent: Container): Dimension {
        val insets = parent.insets
        val size = parent.preferredSize
        size.width += insets.left + insets.right
        size.height += insets.top + insets.bottom

        return size
    }

    override fun minimumLayoutSize(parent: Container): Dimension {
        val insets = parent.insets
        val size = parent.minimumSize
        size.width += insets.left + insets.right
        size.height += insets.top + insets.bottom

        return size
    }

    override fun addLayoutComponent(comp: Component, constraints: Any?) {
        if (constraints is String) {
            addLayoutComponent(constraints, comp)
        }
    }

    override fun addLayoutComponent(name: String, comp: Component) {
        when (name) {
            PLACEHOLDER -> placeholder = comp
        }
    }

    override fun removeLayoutComponent(comp: Component) {
        when (comp) {
            placeholder -> placeholder = null
        }
    }

    companion object {

        const val PLACEHOLDER = "placeholder"
    }
}