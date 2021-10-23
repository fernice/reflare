/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import fernice.reflare.light.DefaultTableCellRenderer
import org.fernice.reflare.element.StyleTreeElementLookup
import org.fernice.reflare.element.TableHeaderElement
import org.fernice.reflare.element.element
import org.fernice.reflare.render.CellRendererPane
import java.awt.BasicStroke
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.io.Serializable
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicTableHeaderUI
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import kotlin.math.max

@Suppress("ACCIDENTAL_OVERRIDE")
open class FlareTableHeaderUI(tableHeader: JTableHeader) : BasicTableHeaderUI(), FlareUI {

    override val element = TableHeaderElement(tableHeader)

    override fun installDefaults() {
        installDefaultProperties(header)
        header.defaultRenderer = DefaultTableCellHeaderRenderer()

        header.remove(rendererPane)
        rendererPane = CellRendererPane()
        header.add(rendererPane)

        StyleTreeElementLookup.registerElement(header, this)
    }

    override fun uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(header)
    }

    override fun paint(g: Graphics, c: JComponent) {
        if (header.columnModel.columnCount <= 0) {
            return
        }
        val ltr = header.componentOrientation.isLeftToRight

        val clip = g.clipBounds
        val left = clip.location
        val right = Point(clip.x + clip.width - 1, clip.y)
        val cm = header.columnModel
        var cMin = header.columnAtPoint(if (ltr) left else right)
        var cMax = header.columnAtPoint(if (ltr) right else left)
        // This should never happen.
        if (cMin == -1) {
            cMin = 0
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = cm.columnCount - 1
        }

        val draggedColumn = header.draggedColumn
        var columnWidth: Int
        val cellRect = header.getHeaderRect(if (ltr) cMin else cMax)
        var aColumn: TableColumn
        val table = header.table
        if (ltr) {
            for (column in cMin..cMax) {
                aColumn = cm.getColumn(column)
                columnWidth = aColumn.width
                cellRect.width = columnWidth
                if (aColumn !== draggedColumn) {
                    paintCell(g, cellRect, column)
                }
                cellRect.x += columnWidth
            }
        } else {
            for (column in cMax downTo cMin) {
                aColumn = cm.getColumn(column)
                columnWidth = aColumn.width
                cellRect.width = columnWidth
                if (aColumn !== draggedColumn) {
                    paintCell(g, cellRect, column)
                }
                cellRect.x += columnWidth
            }
        }

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
            val draggedColumnIndex = viewIndexForColumn(draggedColumn)
            val draggedCellRect = header.getHeaderRect(draggedColumnIndex)

            // Draw a gray well in place of the moving column.
            g.color = header.parent.background
            g.fillRect(
                draggedCellRect.x, draggedCellRect.y,
                draggedCellRect.width, draggedCellRect.height
            )

            draggedCellRect.x += header.draggedDistance

            // Fill the background.
            g.color = header.background
            g.fillRect(
                draggedCellRect.x, draggedCellRect.y,
                draggedCellRect.width, draggedCellRect.height
            )

            paintCell(g, draggedCellRect, draggedColumnIndex)
        }
        // Remove all components in the rendererPane.
        rendererPane.removeAll()
    }

    private fun getHeaderRenderer(columnIndex: Int): Component {
        val aColumn = header.columnModel.getColumn(columnIndex)
        var renderer: TableCellRenderer? = aColumn.headerRenderer
        if (renderer == null) {
            renderer = header.defaultRenderer
        }

        val hasFocus = (!header.isPaintingForPrint
                && columnIndex == getSelectedColumnIndex()
                && header.hasFocus())

        val component = renderer!!.getTableCellRendererComponent(
            header.table,
            aColumn.headerValue,
            false, hasFocus,
            -1, columnIndex
        )

        val element = component.element

        element.activeHint(false)
        element.hoverHint(columnIndex == rolloverColumn)
        element.focusHint(hasFocus)

        return component
    }

    private var selectedColumnIndex: Int = 0

    private fun getSelectedColumnIndex(): Int {
        val numCols = header.columnModel.columnCount
        if (numCols in 1..selectedColumnIndex) {
            selectedColumnIndex = numCols - 1
        }
        return selectedColumnIndex
    }

    private fun paintCell(g: Graphics, cellRect: Rectangle, columnIndex: Int) {
        val component = getHeaderRenderer(columnIndex)

        val table = header.table

        val verticalGap = if (table?.showVerticalLines == true) 1 else 0
        val horizontalGap = 1 // if (table?.showHorizontalLines == true) 1 else 0

        rendererPane.paintComponent(
            g, component, header, cellRect.x, cellRect.y,
            cellRect.width - verticalGap, cellRect.height - horizontalGap, true
        )

        paintCellGrid(g, table, columnIndex, cellRect)
    }

    private fun paintCellGrid(g: Graphics, table: JTable?, column: Int, cellRect: Rectangle) {
        if (table != null) {
            val g2 = g as Graphics2D

            g2.stroke = BasicStroke(1f)

            val draggedColumn = header.draggedColumn
            if (draggedColumn != null && table.showVerticalLines && column == viewIndexForColumn(draggedColumn)) {
                g.color = table.gridColor
                g.drawLine(cellRect.x - 1, cellRect.y, cellRect.x - 1, cellRect.y + cellRect.height - 1)
            }

            if (table.showVerticalLines) {
                g.color = table.gridColor
                g.drawLine(cellRect.x + cellRect.width - 1, cellRect.y, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height - 1)
            }

            // if (table.showHorizontalLines) {
            g.color = table.gridColor
            g.drawLine(cellRect.x, cellRect.y + cellRect.height - 1, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height - 1)
            // }
        }
    }

    private fun viewIndexForColumn(aColumn: TableColumn): Int {
        val cm = header.columnModel
        for (column in 0 until cm.columnCount) {
            if (cm.getColumn(column) === aColumn) {
                return column
            }
        }
        return -1
    }

    private fun getHeaderHeight(): Int {
        var height = 0
        var accomodatedDefault = false
        val columnModel = header.columnModel
        for (column in 0 until columnModel.columnCount) {
            val aColumn = columnModel.getColumn(column)
            val isDefault = aColumn.headerRenderer == null

            if (!isDefault || !accomodatedDefault) {
                val comp = getHeaderRenderer(column)

                rendererPane.add(comp)

                val rendererHeight = comp.preferredSize.height
                height = max(height, rendererHeight)

                // Configuring the header renderer to calculate its preferred size
                // is expensive. Optimise this by assuming the default renderer
                // always has the same height as the first non-zero height that
                // it returns for a non-null/non-empty value.
                if (isDefault && rendererHeight > 0) {
                    var headerValue: Any? = aColumn.headerValue
                    if (headerValue != null) {
                        headerValue = headerValue.toString()

                        if (headerValue != "") {
                            accomodatedDefault = true
                        }
                    }
                }
            }
        }
        rendererPane.removeAll()

        return height
    }

    private fun createHeaderSize(width: Long): Dimension {
        var width = width
        // None of the callers include the intercell spacing, do it here.
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE.toLong()
        }

        val verticalGap = 1 // if (header.table?.showHorizontalLines == true) 1 else 0

        return Dimension(width.toInt(), getHeaderHeight() + verticalGap)
    }


    /**
     * Return the minimum size of the header. The minimum width is the sum
     * of the minimum widths of each column (plus inter-cell spacing).
     */
    override fun getMinimumSize(c: JComponent): Dimension {
        var width: Long = 0
        val horizontalGap = if (header.table?.showVerticalLines == true) 1 else 0

        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.minWidth + horizontalGap
        }
        return createHeaderSize(width)
    }

    /**
     * Return the preferred size of the header. The preferred height is the
     * maximum of the preferred heights of all of the components provided
     * by the header renderers. The preferred width is the sum of the
     * preferred widths of each column (plus inter-cell spacing).
     */
    override fun getPreferredSize(c: JComponent?): Dimension {
        var width: Long = 0
        val horizontalGap = if (header.table?.showVerticalLines == true) 1 else 0

        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.preferredWidth + horizontalGap
        }
        return createHeaderSize(width)
    }

    /**
     * Return the maximum size of the header. The maximum width is the sum
     * of the maximum widths of each column (plus inter-cell spacing).
     */
    override fun getMaximumSize(c: JComponent): Dimension {
        var width: Long = 0
        val horizontalGap = if (header.table?.showVerticalLines == true) 1 else 0

        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.maxWidth + horizontalGap
        }
        return createHeaderSize(width)
    }

    override fun rolloverColumnUpdated(oldColumn: Int, newColumn: Int) {
        header.repaint(header.getHeaderRect(oldColumn))
        header.repaint(header.getHeaderRect(newColumn))
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }

    companion object {
        @JvmStatic
        fun createUI(component: JComponent): ComponentUI {
            return FlareTableHeaderUI(component as JTableHeader)
        }
    }
}

open class DefaultTableCellHeaderRenderer : DefaultTableCellRenderer.UIResource() {
    private var horizontalTextPositionSet: Boolean = false
    private var sortArrow: Icon? = null
    private val emptyIcon = EmptyIcon()

    override fun setHorizontalTextPosition(var1: Int) {
        this.horizontalTextPositionSet = true
        super.setHorizontalTextPosition(var1)
    }

    public override fun paintComponent(var1: Graphics) {
        val var2 = UIManager.getBoolean( "TableHeader.rightAlignSortArrow")
        if (var2 && this.sortArrow != null) {
            this.emptyIcon.width = this.sortArrow!!.iconWidth
            this.emptyIcon.height = this.sortArrow!!.iconHeight
            this.icon = this.emptyIcon
            super.paintComponent(var1)
            val var3 = this.computeIconPosition(var1)
            this.sortArrow!!.paintIcon(this, var1, var3.x, var3.y)
        } else {
            super.paintComponent(var1)
        }

    }

    private fun computeIconPosition(var1: Graphics): Point {
        val var2 = var1.fontMetrics
        val var3 = Rectangle()
        val var4 = Rectangle()
        val var5 = Rectangle()
        val var6 = this.insets
        var3.x = var6.left
        var3.y = var6.top
        var3.width = this.width - (var6.left + var6.right)
        var3.height = this.height - (var6.top + var6.bottom)
        SwingUtilities.layoutCompoundLabel(
            this,
            var2,
            this.text,
            this.sortArrow,
            this.verticalAlignment,
            this.horizontalAlignment,
            this.verticalTextPosition,
            this.horizontalTextPosition,
            var3,
            var5,
            var4,
            this.iconTextGap
        )
        val var7 = this.width - var6.right - this.sortArrow!!.iconWidth
        val var8 = var5.y
        return Point(var7, var8)
    }

    private inner class EmptyIcon : Icon, Serializable {
        internal var width: Int = 0
        internal var height: Int = 0

        init {
            this.width = 0
            this.height = 0
        }

        override fun paintIcon(var1: Component, var2: Graphics, var3: Int, var4: Int) {}

        override fun getIconWidth(): Int {
            return this.width
        }

        override fun getIconHeight(): Int {
            return this.height
        }
    }

    companion object {

        fun getColumnSortOrder(var0: JTable?, var1: Int): SortOrder? {
            var var2: SortOrder? = null
            return if (var0 != null && var0.rowSorter != null) {
                val var3 = var0.rowSorter.sortKeys
                if (var3.size > 0 && (var3[0] as RowSorter.SortKey).column == var0.convertColumnIndexToModel(var1)) {
                    var2 = (var3[0] as RowSorter.SortKey).sortOrder
                }

                var2
            } else {
                var2
            }
        }
    }
}