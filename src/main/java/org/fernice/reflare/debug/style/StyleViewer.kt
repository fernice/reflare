/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.debug.style

import org.fernice.flare.style.properties.longhand.background.BackgroundColorId
import org.fernice.flare.style.properties.longhand.border.BorderBottomColorId
import org.fernice.flare.style.properties.longhand.border.BorderBottomLeftRadiusId
import org.fernice.flare.style.properties.longhand.border.BorderBottomRightRadiusId
import org.fernice.flare.style.properties.longhand.border.BorderBottomStyleId
import org.fernice.flare.style.properties.longhand.border.BorderBottomWidthId
import org.fernice.flare.style.properties.longhand.border.BorderLeftColorId
import org.fernice.flare.style.properties.longhand.border.BorderLeftStyleId
import org.fernice.flare.style.properties.longhand.border.BorderLeftWidthId
import org.fernice.flare.style.properties.longhand.border.BorderRightColorId
import org.fernice.flare.style.properties.longhand.border.BorderRightStyleId
import org.fernice.flare.style.properties.longhand.border.BorderRightWidthId
import org.fernice.flare.style.properties.longhand.border.BorderTopColorId
import org.fernice.flare.style.properties.longhand.border.BorderTopLeftRadiusId
import org.fernice.flare.style.properties.longhand.border.BorderTopRightRadiusId
import org.fernice.flare.style.properties.longhand.border.BorderTopStyleId
import org.fernice.flare.style.properties.longhand.border.BorderTopWidthId
import org.fernice.flare.style.properties.longhand.font.FontFamilyId
import org.fernice.flare.style.properties.longhand.font.FontSizeId
import org.fernice.flare.style.properties.longhand.margin.MarginBottomId
import org.fernice.flare.style.properties.longhand.margin.MarginLeftId
import org.fernice.flare.style.properties.longhand.margin.MarginRightId
import org.fernice.flare.style.properties.longhand.margin.MarginTopId
import org.fernice.flare.style.properties.longhand.padding.PaddingBottomId
import org.fernice.flare.style.properties.longhand.padding.PaddingLeftId
import org.fernice.flare.style.properties.longhand.padding.PaddingRightId
import org.fernice.flare.style.properties.longhand.padding.PaddingTopId
import org.fernice.reflare.layout.GridBagBuilder
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class StyleViewerPanel : JPanel() {

    init {

    }
}

private class BackgroundViewerPanel : ViewerPanel() {

    private val backgroundColorValue: JLabel = property(BackgroundColorId.name)
}

private class BorderViewerPanel : ViewerPanel() {

    private val borderTopWidthValue: JLabel = property(BorderTopWidthId.name)
    private val borderTopColorValue: JLabel = property(BorderTopColorId.name)
    private val borderTopStyleValue: JLabel = property(BorderTopStyleId.name)

    private val borderTopLeftRadiusValue: JLabel = property(BorderTopLeftRadiusId.name)
    private val borderTopRightRadiusValue: JLabel = property(BorderTopRightRadiusId.name)

    private val borderRightWidthValue: JLabel = property(BorderRightWidthId.name)
    private val borderRightColorValue: JLabel = property(BorderRightColorId.name)
    private val borderRightStyleValue: JLabel = property(BorderRightStyleId.name)

    private val borderBottomWidthValue: JLabel = property(BorderBottomWidthId.name)
    private val borderBottomColorValue: JLabel = property(BorderBottomColorId.name)
    private val borderBottomStyleValue: JLabel = property(BorderBottomStyleId.name)

    private val borderLeftWidthValue: JLabel = property(BorderLeftWidthId.name)
    private val borderLeftColorValue: JLabel = property(BorderLeftColorId.name)
    private val borderLeftStyleValue: JLabel = property(BorderLeftStyleId.name)

    private val borderBottomRightRadiusValue: JLabel = property(BorderBottomRightRadiusId.name)
    private val borderBottomLeftRadiusValue: JLabel = property(BorderBottomLeftRadiusId.name)
}

private class FontViewerPanel : ViewerPanel() {

    private val fontSizeValue: JLabel = property(FontSizeId.name)
    private val fontFamilyValue: JLabel = property(FontFamilyId.name)
}

private class MarginViewerPanel : ViewerPanel() {

    private val marginTopValue: JLabel = property(MarginTopId.name)
    private val marginRightValue: JLabel = property(MarginRightId.name)
    private val marginBottomValue: JLabel = property(MarginBottomId.name)
    private val marginLeftValue: JLabel = property(MarginLeftId.name)
}

private class PaddingViewerPanel : ViewerPanel() {

    private val paddingTopValue: JLabel = property(PaddingTopId.name)
    private val paddingRightValue: JLabel = property(PaddingRightId.name)
    private val paddingBottomValue: JLabel = property(PaddingBottomId.name)
    private val paddingLeftValue: JLabel = property(PaddingLeftId.name)
}

private abstract class ViewerPanel : JPanel() {

    private val gridBagBuilder: GridBagBuilder = GridBagBuilder.components()

    init {
        layout = GridBagLayout()
    }

    protected fun property(property: String): JLabel {
        gridBagBuilder.noWeightX()

        val propertyLabel = JLabel()
        propertyLabel.text = property
        add(propertyLabel, gridBagBuilder.gbc())

        gridBagBuilder.nextX().fullWeightX()

        val propertyValue = JLabel()
        add(propertyLabel, gridBagBuilder.gbc())

        gridBagBuilder.newLineReset()

        return propertyValue
    }
}