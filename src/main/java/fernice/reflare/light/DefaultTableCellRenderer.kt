/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import java.awt.Component
import java.awt.Rectangle
import java.io.Serializable
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

open class DefaultTableCellRenderer : Label(), TableCellRenderer, Serializable {

    /**
     * Creates a default table cell renderer.
     */
    init {
        isOpaque = true
        name = "Table.cellRenderer"
    }

    // implements javax.swing.table.TableCellRenderer
    /**
     *
     * Returns the default table cell renderer.
     *
     *
     * During a printing operation, this method will be called with
     * `isSelected` and `hasFocus` values of
     * `false` to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * [javax.swing.JComponent.isPaintingForPrint].
     *
     * @param table  the `JTable`
     * @param value  the value to assign to the cell at
     * `[row, column]`
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent.isPaintingForPrint
     */
    override fun getTableCellRendererComponent(
        table: JTable?, value: Any,
        isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
    ): Component {
        var isSelected = isSelected
        if (table == null) {
            return this
        }

        val dropLocation = table.dropLocation
        if (dropLocation != null
            && !dropLocation.isInsertRow
            && !dropLocation.isInsertColumn
            && dropLocation.row == row
            && dropLocation.column == column
        ) {
            isSelected = true
        }

        setValue(value)

        return this
    }

    /*
     * The following methods are overridden as a performance measure to
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and
     * drawbacks of overriding methods like these.
     */

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun isOpaque(): Boolean {
        val back = background
        var p: Component? = parent
        if (p != null) {
            p = p.parent
        }

        // p should now be the JTable.
        val colorMatch = back != null && p != null &&
                back == p.background &&
                p.isOpaque
        return !colorMatch && super.isOpaque()
    }

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
     */
    override fun validate() {}

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
     *
     * @since 1.5
     */
    override fun repaint() {}

    /**
     * Overridden for performance reasons.
     * See the [Implementation Note](#override)
     * for more information.
     */
    override fun firePropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        // Strings get interned...
        if (propertyName === "text"
            || propertyName === "labelFor"
            || propertyName === "displayedMnemonic"
            || ((propertyName === "font" || propertyName === "foreground")
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
    override fun firePropertyChange(propertyName: String, oldValue: Boolean, newValue: Boolean) {}


    /**
     * Sets the `String` object for the cell being rendered to
     * `value`.
     *
     * @param value  the string value for this cell; if value is
     * `null` it sets the text value to an empty string
     * @see JLabel.setText
     */
    protected open fun setValue(value: Any?) {
        text = value?.toString() ?: ""
    }


    /**
     * A subclass of `DefaultTableCellRenderer` that
     * implements `UIResource`.
     * `DefaultTableCellRenderer` doesn't implement
     * `UIResource`
     * directly so that applications can safely override the
     * `cellRenderer` property with
     * `DefaultTableCellRenderer` subclasses.
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
    open class UIResource : DefaultTableCellRenderer(), javax.swing.plaf.UIResource
}