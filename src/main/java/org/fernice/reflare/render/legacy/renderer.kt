/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render.legacy

import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.element.AWTComponentElement
import org.fernice.reflare.render.Renderer
import java.awt.Component
import java.awt.Graphics

class LegacyRenderer(private val component: Component, private val element: AWTComponentElement) : Renderer {

    override fun renderBackground(g: Graphics, style: ComputedValues?) {
    }

    override fun renderBorder(g: Graphics, style: ComputedValues?) {
    }
}