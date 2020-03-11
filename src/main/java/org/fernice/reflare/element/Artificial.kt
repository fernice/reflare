/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element

import fernice.reflare.FlareLookAndFeel
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.style.ResolvedElementStyles
import org.fernice.flare.style.context.StyleContext
import java.awt.Component
import java.awt.Container

class ArtificialComponentElement(component: Component) : AWTComponentElement(component) {

    override fun parentChanged(old: Frame?, new: Frame?) {
    }

    override val localName get() = "component"

    override fun isEmpty(): Boolean = true

    override val previousSibling: Element? get() = null
    override val nextSibling: Element? get() = null

    override val children: List<Element> = listOf()

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        if (FlareLookAndFeel.isLightweightMode()) {
            data.setStyles(elementStyles)
        } else {
            super.finishRestyle(context, data, elementStyles)
        }
    }
}

class ArtificialContainerElement(container: Container) : AWTContainerElement(container) {

    // In theory it is possible to construct a Container meaning that needs a
    // local name to styled. In practice hopefully no one will try to do it
    // because even though the element will be considered when it comes to
    // matching, we have no means to render using its computed styles.
    override val localName get() = "container"

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        if (FlareLookAndFeel.isLightweightMode()) {
            data.setStyles(elementStyles)
        } else {
            super.finishRestyle(context, data, elementStyles)
        }
    }
}