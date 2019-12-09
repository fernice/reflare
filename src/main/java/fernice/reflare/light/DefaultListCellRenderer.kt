/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import java.awt.Component
import java.awt.Rectangle
import java.io.Serializable
import javax.swing.Icon
import javax.swing.JList
import javax.swing.ListCellRenderer

open class DefaultListCellRenderer : FLabel(), ListCellRenderer<Any>, Serializable {

    init {
        isOpaque = true
        name = "List.cellRenderer"
    }

    override fun getListCellRendererComponent(
        list: JList<*>,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        componentOrientation = list.componentOrientation

        if (value is Icon) {
            icon = value
            text = ""
        } else {
            icon = null
            text = value?.toString() ?: ""
        }

        isEnabled = list.isEnabled

        return this
    }

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     *
     * @since 1.5
     * @return `true` if the background is completely opaque
     * and differs from the JList's background;
     * `false` otherwise
     */
    override fun isOpaque(): Boolean {
        val back = background
        var p: Component? = parent
        if (p != null) {
            p = p.parent
        }
        // p should now be the JList.
        val colorMatch = back != null && p != null &&
                back == p.background &&
                p.isOpaque
        return !colorMatch && super.isOpaque()
    }

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun validate() {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     *
     * @since 1.5
     */
    override fun invalidate() {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     *
     * @since 1.5
     */
    override fun repaint() {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun revalidate() {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun repaint(tm: Long, x: Int, y: Int, width: Int, height: Int) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun repaint(r: Rectangle) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        // Strings get interned...
        if (propertyName === "text" || ((propertyName === "font" || propertyName === "foreground")
                    && oldValue !== newValue
                    && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)
        ) {

            super.firePropertyChange(propertyName, oldValue, newValue)
        }
    }

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Byte, newValue: Byte) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Char, newValue: Char) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Short, newValue: Short) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Int, newValue: Int) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Long, newValue: Long) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Float, newValue: Float) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Double, newValue: Double) {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Boolean, newValue: Boolean) {}

    /**
     * A subclass of DefaultListCellRenderer that implements UIResource.
     * DefaultListCellRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with DefaultListCellRenderer subclasses.
     *
     *
     * **Warning:**
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans
     * has been added to the `java.beans` package.
     * Please see [java.beans.XMLEncoder].
     */
    open class UIResource : DefaultListCellRenderer(), javax.swing.plaf.UIResource
}