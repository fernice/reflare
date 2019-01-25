/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui

import fernice.reflare.FlareLookAndFeel
import org.fernice.reflare.element.ComponentElement
import org.fernice.reflare.element.ScrollBarElement
import org.fernice.reflare.element.deregisterElement
import org.fernice.reflare.element.registerElement
import org.fernice.reflare.render.use
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JScrollBar
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicScrollBarUI

class FlareScrollBarUI(scrollbar: JScrollBar, override val element: ComponentElement = ScrollBarElement(scrollbar)) : BasicScrollBarUI(), FlareUI {

    private var showButtons = false

    override fun installDefaults() {
        super.installDefaults()

        scrollBarWidth = if (showButtons) 16 else 12
        scrollbar.isOpaque = false
        scrollbar.border = FlareBorder(this)
        scrollbar.font = FlareLookAndFeel.DEFAULT_FONT

        registerElement(scrollbar, element)
    }

    override fun uninstallDefaults() {
        super.uninstallDefaults()

        deregisterElement(scrollbar)
    }

    override fun paint(graphics: Graphics, component: JComponent) {
        paintBackground(component, graphics)

        super.paint(graphics, component)
    }

    override fun paintTrack(g: Graphics?, c: JComponent?, trackBounds: Rectangle?) {


    }

    override fun paintThumb(g: Graphics, c: JComponent, thumbBounds: Rectangle) {
        if (thumbBounds.isEmpty || !scrollbar.isEnabled) {
            return
        }

        val w = thumbBounds.width
        val h = thumbBounds.height
        val radii = if (showButtons) 3 else w - 2
        val padding = if (showButtons) 0 else 2

        g.use { g2 ->
            g2.color = scrollbar.foreground
            g2.fillRoundRect(thumbBounds.x + padding, thumbBounds.y, w - (padding * 2), h, radii, radii)
        }
    }

    private fun paintBackground(component: JComponent, g: Graphics) {
        element.paintBackground(component, g)
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        element.paintBorder(c, g)
    }

    override fun layoutVScrollbar(sb: JScrollBar) {
        val sbSize = sb.size
        val sbInsets = sb.insets

        /*
         * Width and left edge of the buttons and thumb.
         */
        val itemW = sbSize.width - (sbInsets.left + sbInsets.right)
        val itemX = sbInsets.left

        /* Nominal locations of the buttons, assuming their preferred
         * size will fit.
         */
        var decrButtonH = if (showButtons) itemW else 0
        val decrButtonY = sbInsets.top

        var incrButtonH = if (showButtons) itemW else 0
        var incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH)

        /* The thumb must fit within the height left over after we
         * subtract the preferredSize of the buttons and the insets
         * and the gaps
         */
        val sbInsetsH = sbInsets.top + sbInsets.bottom
        val sbButtonsH = decrButtonH + incrButtonH
        val gaps = decrGap + incrGap
        val trackH = (sbSize.height - (sbInsetsH + sbButtonsH) - gaps).toFloat()

        /* Compute the height and origin of the thumb.   The case
         * where the thumb is at the bottom edge is handled specially
         * to avoid numerical problems in computing thumbY.  Enforce
         * the thumbs min/max dimensions.  If the thumb doesn't
         * fit in the track (trackH) we'll hide it later.
         */
        val min = sb.minimum.toFloat()
        val extent = sb.visibleAmount.toFloat()
        val range = sb.maximum - min
        val value = sb.value.toFloat()

        var thumbH = if (range <= 0)
            getMaximumThumbSize().height
        else
            (trackH * (extent / range)).toInt()
        thumbH = Math.max(thumbH, getMinimumThumbSize().height)
        thumbH = Math.min(thumbH, getMaximumThumbSize().height)

        var thumbY = incrButtonY - incrGap - thumbH
        if (value < sb.maximum - sb.visibleAmount) {
            val thumbRange = trackH - thumbH
            thumbY = (0.5f + thumbRange * ((value - min) / (range - extent))).toInt()
            thumbY += decrButtonY + decrButtonH + decrGap
        }

        /* If the buttons don't fit, allocate half of the available
         * space to each and move the lower one (incrButton) down.
         */
        val sbAvailButtonH = sbSize.height - sbInsetsH
        if (sbAvailButtonH < sbButtonsH) {
            decrButtonH = sbAvailButtonH / 2
            incrButtonH = decrButtonH
            incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH)
        }
        decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH)
        incrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH)

        /* Update the trackRect field.
         */
        val itrackY = decrButtonY + decrButtonH + decrGap
        val itrackH = incrButtonY - incrGap - itrackY
        trackRect.setBounds(itemX, itrackY, itemW, itrackH)

        /* If the thumb isn't going to fit, zero it's bounds.  Otherwise
         * make sure it fits between the buttons.  Note that setting the
         * thumbs bounds will cause a repaint.
         */
        if (thumbH >= trackH.toInt()) {
            if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
                // This is used primarily for GTK L&F, which expands the
                // thumb to fit the track when it would otherwise be hidden.
                setThumbBounds(itemX, itrackY, itemW, itrackH)
            } else {
                // Other L&F's simply hide the thumb in this case.
                setThumbBounds(0, 0, 0, 0)
            }
        } else {
            if (thumbY + thumbH > incrButtonY - incrGap) {
                thumbY = incrButtonY - incrGap - thumbH
            }
            if (thumbY < decrButtonY + decrButtonH + decrGap) {
                thumbY = decrButtonY + decrButtonH + decrGap + 1
            }
            setThumbBounds(itemX, thumbY, itemW, thumbH)
        }
    }

    override fun layoutHScrollbar(sb: JScrollBar) {
        val sbSize = sb.size
        val sbInsets = sb.insets

        /* Height and top edge of the buttons and thumb.
         */
        val itemH = sbSize.height - (sbInsets.top + sbInsets.bottom)
        val itemY = sbInsets.top

        val ltr = sb.componentOrientation.isLeftToRight

        /* Nominal locations of the buttons, assuming their preferred
         * size will fit.
         */
        var leftButtonW = if (showButtons) itemH else 0
        var rightButtonW = if (showButtons) itemH else 0
        if (!ltr) {
            val temp = leftButtonW
            leftButtonW = rightButtonW
            rightButtonW = temp
        }
        val leftButtonX = sbInsets.left
        var rightButtonX = sbSize.width - (sbInsets.right + rightButtonW)
        val leftGap = if (ltr) decrGap else incrGap
        val rightGap = if (ltr) incrGap else decrGap

        /* The thumb must fit within the width left over after we
         * subtract the preferredSize of the buttons and the insets
         * and the gaps
         */
        val sbInsetsW = sbInsets.left + sbInsets.right
        val sbButtonsW = leftButtonW + rightButtonW
        val trackW = (sbSize.width - (sbInsetsW + sbButtonsW) - (leftGap + rightGap)).toFloat()

        /* Compute the width and origin of the thumb.  Enforce
         * the thumbs min/max dimensions.  The case where the thumb
         * is at the right edge is handled specially to avoid numerical
         * problems in computing thumbX.  If the thumb doesn't
         * fit in the track (trackH) we'll hide it later.
         */
        val min = sb.minimum.toFloat()
        val max = sb.maximum.toFloat()
        val extent = sb.visibleAmount.toFloat()
        val range = max - min
        val value = sb.value.toFloat()

        var thumbW = if (range <= 0)
            getMaximumThumbSize().width
        else
            (trackW * (extent / range)).toInt()
        thumbW = Math.max(thumbW, getMinimumThumbSize().width)
        thumbW = Math.min(thumbW, getMaximumThumbSize().width)

        var thumbX = if (ltr) rightButtonX - rightGap - thumbW else leftButtonX + leftButtonW + leftGap
        if (value < max - sb.visibleAmount) {
            val thumbRange = trackW - thumbW
            thumbX = if (ltr) {
                (0.5f + thumbRange * ((value - min) / (range - extent))).toInt()
            } else {
                (0.5f + thumbRange * ((max - extent - value) / (range - extent))).toInt()
            }
            thumbX += leftButtonX + leftButtonW + leftGap
        }

        /* If the buttons don't fit, allocate half of the available
         * space to each and move the right one over.
         */
        val sbAvailButtonW = sbSize.width - sbInsetsW
        if (sbAvailButtonW < sbButtonsW) {
            leftButtonW = sbAvailButtonW / 2
            rightButtonW = leftButtonW
            rightButtonX = sbSize.width - (sbInsets.right + rightButtonW + rightGap)
        }

        (if (ltr) decrButton else incrButton).setBounds(leftButtonX, itemY, leftButtonW, itemH)
        (if (ltr) incrButton else decrButton).setBounds(rightButtonX, itemY, rightButtonW, itemH)

        /* Update the trackRect field.
         */
        val itrackX = leftButtonX + leftButtonW + leftGap
        val itrackW = rightButtonX - rightGap - itrackX
        trackRect.setBounds(itrackX, itemY, itrackW, itemH)

        /* Make sure the thumb fits between the buttons.  Note
         * that setting the thumbs bounds causes a repaint.
         */
        if (thumbW >= trackW.toInt()) {
            if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
                // This is used primarily for GTK L&F, which expands the
                // thumb to fit the track when it would otherwise be hidden.
                setThumbBounds(itrackX, itemY, itrackW, itemH)
            } else {
                // Other L&F's simply hide the thumb in this case.
                setThumbBounds(0, 0, 0, 0)
            }
        } else {
            if (thumbX + thumbW > rightButtonX - rightGap) {
                thumbX = rightButtonX - rightGap - thumbW
            }
            if (thumbX < leftButtonX + leftButtonW + leftGap) {
                thumbX = leftButtonX + leftButtonW + leftGap + 1
            }
            setThumbBounds(thumbX, itemY, thumbW, itemH)
        }
    }

    override fun createIncreaseButton(orientation: Int): JButton = FlareArrowButton(orientation)

    override fun createDecreaseButton(orientation: Int): JButton = FlareArrowButton(orientation)
}

private class FlareArrowButton(var direction: Int) : JButton(), SwingConstants {

    init {
        isRequestFocusEnabled = false
    }

    override fun paint(g: Graphics?) {
        super.paint(g)

        val origColor: Color = g!!.color
        val isEnabled: Boolean = isEnabled
        val w: Int = size.width
        val h: Int = size.height
        var size: Int

        // If there's no room to draw arrow, bail
        if (h < 5 || w < 5) {
            g.color = origColor
            return
        }

        // Draw the arrow
        size = Math.min((h - 4) / 3, (w - 4) / 3)
        size = Math.max(size, 2)
        paintTriangle(
            g, (w - size) / 2, (h - size) / 2,
            size, direction, isEnabled
        )
    }

    /**
     * Returns the preferred size of the `BasicArrowButton`.
     *
     * @return the preferred size
     */
    override fun getPreferredSize(): Dimension {
        return Dimension(16, 16)
    }

    /**
     * Returns the minimum size of the `BasicArrowButton`.
     *
     * @return the minimum size
     */
    override fun getMinimumSize(): Dimension {
        return Dimension(5, 5)
    }

    /**
     * Returns the maximum size of the `BasicArrowButton`.
     *
     * @return the maximum size
     */
    override fun getMaximumSize(): Dimension {
        return Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)
    }

    /**
     * Returns whether the arrow button should get the focus.
     * `BasicArrowButton`s are used as a child component of
     * composite components such as `JScrollBar` and
     * `JComboBox`. Since the composite component typically gets the
     * focus, this method is overriden to return `false`.
     *
     * @return `false`
     */
    @Suppress("OverridingDeprecatedMember")
    override fun isFocusTraversable(): Boolean {
        return false
    }

    /**
     * Paints a triangle.
     *
     * @param g the `Graphics` to draw to
     * @param x the x coordinate
     * @param y the y coordinate
     * @param size the size of the triangle to draw
     * @param direction the direction in which to draw the arrow;
     * one of `SwingConstants.NORTH`,
     * `SwingConstants.SOUTH`, `SwingConstants.EAST` or
     * `SwingConstants.WEST`
     * @param isEnabled whether or not the arrow is drawn enabled
     */
    private fun paintTriangle(
        g: Graphics, x: Int, y: Int, size: Int,
        direction: Int, isEnabled: Boolean
    ) {
        var size0 = size
        val oldColor = g.color
        val mid: Int
        var i: Int
        var j: Int

        j = 0
        size0 = Math.max(size0, 2)
        mid = size0 / 2 - 1

        g.translate(x, y)
        if (isEnabled)
            g.color = foreground
        else
            g.color = foreground

        when (direction) {
            SwingConstants.NORTH -> {
                i = 0
                while (i < size0) {
                    g.drawLine(mid - i, i, mid + i, i)
                    i++
                }
                if (!isEnabled) {
                    g.color = foreground
                    g.drawLine(mid - i + 2, i, mid + i, i)
                }
            }
            SwingConstants.SOUTH -> {
                if (!isEnabled) {
                    g.translate(1, 1)
                    g.color = foreground
                    i = size0 - 1
                    while (i >= 0) {
                        g.drawLine(mid - i, j, mid + i, j)
                        j++
                        i--
                    }
                    g.translate(-1, -1)
                    g.color = foreground
                }

                j = 0
                i = size0 - 1
                while (i >= 0) {
                    g.drawLine(mid - i, j, mid + i, j)
                    j++
                    i--
                }
            }
            SwingConstants.WEST -> {
                i = 0
                while (i < size0) {
                    g.drawLine(i, mid - i, i, mid + i)
                    i++
                }
                if (!isEnabled) {
                    g.color = foreground
                    g.drawLine(i, mid - i + 2, i, mid + i)
                }
            }
            SwingConstants.EAST -> {
                if (!isEnabled) {
                    g.translate(1, 1)
                    g.color = foreground
                    i = size0 - 1
                    while (i >= 0) {
                        g.drawLine(j, mid - i, j, mid + i)
                        j++
                        i--
                    }
                    g.translate(-1, -1)
                    g.color = foreground
                }

                j = 0
                i = size0 - 1
                while (i >= 0) {
                    g.drawLine(j, mid - i, j, mid + i)
                    j++
                    i--
                }
            }
        }
        g.translate(-x, -y)
        g.color = oldColor
    }

}