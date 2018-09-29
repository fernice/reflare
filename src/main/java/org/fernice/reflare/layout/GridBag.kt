/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.layout

import java.awt.GridBagConstraints
import java.awt.Insets

internal class GridBagBuilder(private val origin: GridBagConstraints) {
    private var current: GridBagConstraints? = null

    init {
        current = origin.clone() as GridBagConstraints
    }

    fun reset(): GridBagBuilder {
        current = origin.clone() as GridBagConstraints
        return this
    }

    fun newLineReset(): GridBagBuilder {
        val theNewCurrent = origin.clone() as GridBagConstraints
        theNewCurrent.gridy = current!!.gridy + 1
        current = theNewCurrent
        return this
    }

    fun setX(inX: Int): GridBagBuilder {
        current!!.gridx = inX
        return this
    }

    fun resetX(): GridBagBuilder {
        current!!.gridx = origin.gridx
        return this
    }

    fun nextX(): GridBagBuilder {
        current!!.gridx++
        return this
    }

    fun skipNextX(): GridBagBuilder {
        return nextX().nextX()
    }

    fun skipWidthX(): GridBagBuilder {
        current!!.gridx += current!!.gridwidth
        return this
    }

    fun setY(inY: Int): GridBagBuilder {
        current!!.gridy = inY
        return this
    }

    fun nextY(): GridBagBuilder {
        current!!.gridy++
        return this
    }

    fun nextLine(): GridBagBuilder {
        return nextY()
    }

    fun resetY(): GridBagBuilder {
        current!!.gridy = origin.gridy
        return this
    }

    fun setWidth(inWidth: Int): GridBagBuilder {
        current!!.gridwidth = inWidth
        return this
    }

    fun resetWidth(): GridBagBuilder {
        current!!.gridwidth = origin.gridwidth
        return this
    }

    fun setHeight(inHeight: Int): GridBagBuilder {
        current!!.gridheight = inHeight
        return this
    }

    fun resetHeight(): GridBagBuilder {
        current!!.gridheight = origin.gridheight
        return this
    }

    fun setWeightX(inWeightX: Double): GridBagBuilder {
        current!!.weightx = inWeightX
        return this
    }

    fun fullWeightX(): GridBagBuilder {
        return setWeightX(1.0)
    }

    fun noWeightX(): GridBagBuilder {
        return setWeightX(0.0)
    }

    fun resetWeightX(): GridBagBuilder {
        return setWeightX(origin.weightx)
    }

    fun setWeightY(inWeightY: Double): GridBagBuilder {
        current!!.weighty = inWeightY
        return this
    }

    fun fullWeightY(): GridBagBuilder {
        return setWeightY(1.0)
    }

    fun noWeightY(): GridBagBuilder {
        return setWeightY(0.0)
    }

    fun resetWeightY(): GridBagBuilder {
        return setWeightY(origin.weighty)
    }

    fun fillNone(): GridBagBuilder {
        current!!.fill = GridBagConstraints.NONE
        return this
    }

    fun fillVertical(): GridBagBuilder {
        current!!.fill = GridBagConstraints.VERTICAL
        return this
    }

    fun fillHorizontal(): GridBagBuilder {
        current!!.fill = GridBagConstraints.HORIZONTAL
        return this
    }

    fun fillBoth(): GridBagBuilder {
        current!!.fill = GridBagConstraints.BOTH
        return this
    }

    fun resetFill(): GridBagBuilder {
        current!!.fill = origin.fill
        return this
    }

    fun anchorCenter(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.CENTER
        return this
    }

    fun anchorWest(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.WEST
        return this
    }

    fun anchorNorthWest(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.NORTHWEST
        return this
    }

    fun anchorNorth(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.NORTH
        return this
    }

    fun anchorNorthEast(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.NORTHEAST
        return this
    }

    fun anchorEast(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.EAST
        return this
    }

    fun anchorSouthEast(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.SOUTHEAST
        return this
    }

    fun anchorSouth(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.SOUTH
        return this
    }

    fun anchorSouthWest(): GridBagBuilder {
        current!!.anchor = GridBagConstraints.SOUTHWEST
        return this
    }

    fun resetAnchor(): GridBagBuilder {
        current!!.anchor = origin.anchor
        return this
    }

    fun setInsets(inTop: Int, inLeft: Int, inBottom: Int, inRight: Int): GridBagBuilder {
        return setInsets(Insets(inTop, inLeft, inBottom, inRight))
    }

    fun setInsets(inInsets: Insets): GridBagBuilder {
        current!!.insets = inInsets
        return this
    }

    fun resetInsets(): GridBagBuilder {
        current!!.insets = origin.insets
        return this
    }

    fun noInsets(): GridBagBuilder {
        current!!.insets = Insets(0, 0, 0, 0)
        return this
    }

    fun insets(inTop: Int, inLeft: Int, inBottom: Int, inRight: Int): GridBagBuilder {
        return setInsets(Insets(inTop, inLeft, inBottom, inRight))
    }

    fun setIpadX(inIpadX: Int): GridBagBuilder {
        current!!.ipadx = inIpadX
        return this
    }

    fun resetIpadX(): GridBagBuilder {
        current!!.ipadx = origin.ipadx
        return this
    }

    fun noIpadX(): GridBagBuilder {
        current!!.ipadx = 0
        return this
    }

    fun setIpadY(inIpadY: Int): GridBagBuilder {
        current!!.ipady = inIpadY
        return this
    }

    fun resetIpadY(): GridBagBuilder {
        current!!.ipady = origin.ipady
        return this
    }

    fun noIpadY(): GridBagBuilder {
        current!!.ipady = 0
        return this
    }

    fun gbc(): GridBagConstraints? {
        return current
    }

    companion object {

        @JvmOverloads
        fun components(insets: Insets = Insets(0, 0, 0, 0)): GridBagBuilder {
            return GridBagBuilder(GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0))
        }
    }
}