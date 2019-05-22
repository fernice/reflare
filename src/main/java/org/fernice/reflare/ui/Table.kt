/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import org.fernice.reflare.Defaults
import org.fernice.reflare.element.StyleTreeElementLookup
import org.fernice.reflare.element.TableElement
import org.fernice.reflare.element.TableHeaderElement
import org.fernice.reflare.element.element
import org.fernice.reflare.render.CellRendererPane
import sun.swing.DefaultLookup
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
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
import javax.swing.plaf.UIResource
import javax.swing.plaf.basic.BasicTableHeaderUI
import javax.swing.plaf.basic.BasicTableUI
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn

class FlareTableUI(table: JTable) : BasicTableUI(), FlareUI {

    override val element = TableElement(table)

    override fun installDefaults() {
        super.installDefaults()

        installDefaults(table)
        table.selectionBackground = Defaults.COLOR_TRANSPARENT
        table.selectionForeground = Defaults.COLOR_TRANSPARENT

        table.remove(rendererPane)
        rendererPane = CellRendererPane()
        table.add(rendererPane)

        StyleTreeElementLookup.registerElement(table, this)
    }

    override fun uninstallDefaults() {
        super.uninstallDefaults()

        StyleTreeElementLookup.deregisterElement(table)
    }

    override fun paint(g: Graphics?, c: JComponent?) {
        val clip = g!!.clipBounds

        val bounds = table.bounds
        // account for the fact that the graphics has already been translated
        // into the table's bounds
        bounds.y = 0
        bounds.x = bounds.y

        if (table.rowCount <= 0 || table.columnCount <= 0 ||
            // this check prevents us from painting the entire table
            // when the clip doesn't intersect our bounds at all
            !bounds.intersects(clip)
        ) {

            paintDropLines(g)
            return
        }

        val ltr = table.componentOrientation.isLeftToRight

        val upperLeft = clip.location
        val lowerRight = Point(
            clip.x + clip.width - 1,
            clip.y + clip.height - 1
        )

        var rMin = table.rowAtPoint(upperLeft)
        var rMax = table.rowAtPoint(lowerRight)
        // This should never happen (as long as our bounds intersect the clip,
        // which is why we bail above if that is the case).
        if (rMin == -1) {
            rMin = 0
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // (We could also get -1 if our bounds don't intersect the clip,
        // which is why we bail above if that is the case).
        // Replace this with the index of the last row.
        if (rMax == -1) {
            rMax = table.rowCount - 1
        }

        var cMin = table.columnAtPoint(if (ltr) upperLeft else lowerRight)
        var cMax = table.columnAtPoint(if (ltr) lowerRight else upperLeft)
        // This should never happen.
        if (cMin == -1) {
            cMin = 0
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = table.columnCount - 1
        }

        // Paint the grid.
        paintGrid(g, rMin, rMax, cMin, cMax)

        // Paint the cells.
        paintCells(g, rMin, rMax, cMin, cMax)

        paintDropLines(g)
    }

    private fun paintDropLines(g: Graphics) {
        val loc = table.dropLocation ?: return

        val color = UIManager.getColor("Table.dropLineColor")
        val shortColor = UIManager.getColor("Table.dropLineShortColor")
        if (color == null && shortColor == null) {
            return
        }

        var rect: Rectangle?

        rect = getHDropLineRect(loc)
        if (rect != null) {
            val x = rect.x
            val w = rect.width
            if (color != null) {
                extendRect(rect, true)
                g.color = color
                g.fillRect(rect.x, rect.y, rect.width, rect.height)
            }
            if (!loc.isInsertColumn && shortColor != null) {
                g.color = shortColor
                g.fillRect(x, rect.y, w, rect.height)
            }
        }

        rect = getVDropLineRect(loc)
        if (rect != null) {
            val y = rect.y
            val h = rect.height
            if (color != null) {
                extendRect(rect, false)
                g.color = color
                g.fillRect(rect.x, rect.y, rect.width, rect.height)
            }
            if (!loc.isInsertRow && shortColor != null) {
                g.color = shortColor
                g.fillRect(rect.x, y, rect.width, h)
            }
        }
    }


    private fun getHDropLineRect(loc: JTable.DropLocation): Rectangle? {
        if (!loc.isInsertRow) {
            return null
        }

        var row = loc.row
        var col = loc.column
        if (col >= table.columnCount) {
            col--
        }

        val rect = table.getCellRect(row, col, true)

        if (row >= table.rowCount) {
            row--
            val prevRect = table.getCellRect(row, col, true)
            rect.y = prevRect.y + prevRect.height
        }

        if (rect.y == 0) {
            rect.y = -1
        } else {
            rect.y -= 2
        }

        rect.height = 3

        return rect
    }

    private fun getVDropLineRect(loc: JTable.DropLocation): Rectangle? {
        if (!loc.isInsertColumn) {
            return null
        }

        val ltr = table.componentOrientation.isLeftToRight
        var col = loc.column
        var rect = table.getCellRect(loc.row, col, true)

        if (col >= table.columnCount) {
            col--
            rect = table.getCellRect(loc.row, col, true)
            if (ltr) {
                rect.x = rect.x + rect.width
            }
        } else if (!ltr) {
            rect.x = rect.x + rect.width
        }

        if (rect.x == 0) {
            rect.x = -1
        } else {
            rect.x -= 2
        }

        rect.width = 3

        return rect
    }

    private fun extendRect(rect: Rectangle?, horizontal: Boolean): Rectangle? {
        if (rect == null) {
            return rect
        }

        if (horizontal) {
            rect.x = 0
            rect.width = table.width
        } else {
            rect.y = 0

            if (table.rowCount != 0) {
                val lastRect = table.getCellRect(table.rowCount - 1, 0, true)
                rect.height = lastRect.y + lastRect.height
            } else {
                rect.height = table.height
            }
        }

        return rect
    }

    /*
     * Paints the grid lines within <I>aRect</I>, using the grid
     * color set with <I>setGridColor</I>. Paints vertical lines
     * if <code>getShowVerticalLines()</code> returns true and paints
     * horizontal lines if <code>getShowHorizontalLines()</code>
     * returns true.
     */
    private fun paintGrid(g: Graphics, rMin: Int, rMax: Int, cMin: Int, cMax: Int) {
        g.color = table.gridColor

        val minCell = table.getCellRect(rMin, cMin, true)
        val maxCell = table.getCellRect(rMax, cMax, true)
        val damagedArea = minCell.union(maxCell)

        if (table.showHorizontalLines) {
            val tableWidth = damagedArea.x + damagedArea.width
            var y = damagedArea.y
            for (row in rMin..rMax) {
                y += table.getRowHeight(row)
                g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1)
            }
        }
        if (table.showVerticalLines) {
            val cm = table.columnModel
            val tableHeight = damagedArea.y + damagedArea.height
            var x: Int
            if (table.componentOrientation.isLeftToRight) {
                x = damagedArea.x
                for (column in cMin..cMax) {
                    val w = cm.getColumn(column).width
                    x += w
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1)
                }
            } else {
                x = damagedArea.x
                for (column in cMax downTo cMin) {
                    val w = cm.getColumn(column).width
                    x += w
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1)
                }
            }
        }
    }

    private fun viewIndexForColumn(aColumn: TableColumn): Int {
        val cm = table.columnModel
        for (column in 0 until cm.columnCount) {
            if (cm.getColumn(column) === aColumn) {
                return column
            }
        }
        return -1
    }

    private fun paintCells(g: Graphics, rMin: Int, rMax: Int, cMin: Int, cMax: Int) {
        val header = table.tableHeader
        val draggedColumn = header?.draggedColumn

        val cm = table.columnModel
        val columnMargin = cm.columnMargin

        var cellRect: Rectangle
        var aColumn: TableColumn
        var columnWidth: Int
        if (table.componentOrientation.isLeftToRight) {
            for (row in rMin..rMax) {
                cellRect = table.getCellRect(row, cMin, false)
                for (column in cMin..cMax) {
                    aColumn = cm.getColumn(column)
                    columnWidth = aColumn.width
                    cellRect.width = columnWidth - columnMargin
                    if (aColumn !== draggedColumn) {
                        paintCell(g, cellRect, row, column)
                    }
                    cellRect.x += columnWidth
                }
            }
        } else {
            for (row in rMin..rMax) {
                cellRect = table.getCellRect(row, cMin, false)
                aColumn = cm.getColumn(cMin)
                if (aColumn !== draggedColumn) {
                    columnWidth = aColumn.width
                    cellRect.width = columnWidth - columnMargin
                    paintCell(g, cellRect, row, cMin)
                }
                for (column in cMin + 1..cMax) {
                    aColumn = cm.getColumn(column)
                    columnWidth = aColumn.width
                    cellRect.width = columnWidth - columnMargin
                    cellRect.x -= columnWidth
                    if (aColumn !== draggedColumn) {
                        paintCell(g, cellRect, row, column)
                    }
                }
            }
        }

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
            paintDraggedArea(g, rMin, rMax, draggedColumn, header.draggedDistance)
        }

        // Remove any renderers that may be left in the rendererPane.
        rendererPane.removeAll()
    }

    private fun paintDraggedArea(g: Graphics, rMin: Int, rMax: Int, draggedColumn: TableColumn, distance: Int) {
        val draggedColumnIndex = viewIndexForColumn(draggedColumn)

        val minCell = table.getCellRect(rMin, draggedColumnIndex, true)
        val maxCell = table.getCellRect(rMax, draggedColumnIndex, true)

        val vacatedColumnRect = minCell.union(maxCell)

        // Paint a gray well in place of the moving column.
        g.color = table.parent.background
        g.fillRect(
            vacatedColumnRect.x, vacatedColumnRect.y,
            vacatedColumnRect.width, vacatedColumnRect.height
        )

        // Move to the where the cell has been dragged.
        vacatedColumnRect.x += distance

        // Fill the background.
        g.color = table.background
        g.fillRect(
            vacatedColumnRect.x, vacatedColumnRect.y,
            vacatedColumnRect.width, vacatedColumnRect.height
        )

        // Paint the vertical grid lines if necessary.
        if (table.showVerticalLines) {
            g.color = table.gridColor
            val x1 = vacatedColumnRect.x
            val y1 = vacatedColumnRect.y
            val x2 = x1 + vacatedColumnRect.width - 1
            val y2 = y1 + vacatedColumnRect.height - 1
            // Left
            g.drawLine(x1 - 1, y1, x1 - 1, y2)
            // Right
            g.drawLine(x2, y1, x2, y2)
        }

        for (row in rMin..rMax) {
            // Render the cell value
            val r = table.getCellRect(row, draggedColumnIndex, false)
            r.x += distance
            paintCell(g, r, row, draggedColumnIndex)

            // Paint the (lower) horizontal grid line if necessary.
            if (table.showHorizontalLines) {
                g.color = table.gridColor
                val rcr = table.getCellRect(row, draggedColumnIndex, true)
                rcr.x += distance
                val x1 = rcr.x
                val y1 = rcr.y
                val x2 = x1 + rcr.width - 1
                val y2 = y1 + rcr.height - 1
                g.drawLine(x1, y2, x2, y2)
            }
        }
    }

    private fun paintCell(g: Graphics, cellRect: Rectangle, row: Int, column: Int) {
        if (table.isEditing && table.editingRow == row &&
            table.editingColumn == column
        ) {
            val component = table.editorComponent
            component.bounds = cellRect
            component.validate()
        } else {
            val renderer = table.getCellRenderer(row, column)
            val component = table.prepareRenderer(renderer, row, column)

            var isSelected = false
            var hasFocus = false

            // Only indicate the selection and focused cell if not printing
            if (!table.isPaintingForPrint) {
                isSelected = table.isCellSelected(row, column)

                val rowIsLead = table.selectionModel.leadSelectionIndex == row
                val colIsLead = table.columnModel.selectionModel.leadSelectionIndex == column

                hasFocus = rowIsLead && colIsLead && table.isFocusOwner
            }

            val element = component.element

            element.activeHint(isSelected)
            element.focusHint(hasFocus)

            rendererPane.paintComponent(
                g, component, table, cellRect.x, cellRect.y,
                cellRect.width, cellRect.height, true
            )
        }
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }
}

class FlareTableHeaderUI(tableHeader: JTableHeader) : BasicTableHeaderUI(), FlareUI {

    override val element = TableHeaderElement(tableHeader)

    override fun installDefaults() {
        installDefaults(header)
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

        rendererPane.paintComponent(
            g, component, header, cellRect.x, cellRect.y,
            cellRect.width, cellRect.height, true
        )
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

                comp.element.restyleImmediately()

                val rendererHeight = comp.preferredSize.height
                height = Math.max(height, rendererHeight)

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
        return Dimension(width.toInt(), getHeaderHeight())
    }


    /**
     * Return the minimum size of the header. The minimum width is the sum
     * of the minimum widths of each column (plus inter-cell spacing).
     */
    override fun getMinimumSize(c: JComponent): Dimension {
        var width: Long = 0
        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.minWidth
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
        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.preferredWidth
        }
        return createHeaderSize(width)
    }

    /**
     * Return the maximum size of the header. The maximum width is the sum
     * of the maximum widths of each column (plus inter-cell spacing).
     */
    override fun getMaximumSize(c: JComponent): Dimension {
        var width: Long = 0
        val enumeration = header.columnModel.columns
        while (enumeration.hasMoreElements()) {
            val aColumn = enumeration.nextElement() as TableColumn
            width += aColumn.maxWidth
        }
        return createHeaderSize(width)
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }
}

open class DefaultTableCellHeaderRenderer : DefaultTableCellRenderer(), UIResource {
    private var horizontalTextPositionSet: Boolean = false
    private var sortArrow: Icon? = null
    private val emptyIcon = EmptyIcon()

    init {
        this.horizontalAlignment = 0
    }

    override fun setHorizontalTextPosition(var1: Int) {
        this.horizontalTextPositionSet = true
        super.setHorizontalTextPosition(var1)
    }

    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, isFocus: Boolean, row: Int, column: Int): Component {
        if (table == null) {
            return this
        }

        setValue(value)

        return this
    }

    public override fun paintComponent(var1: Graphics) {
        val var2 = DefaultLookup.getBoolean(this, this.ui, "TableHeader.rightAlignSortArrow", false)
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
