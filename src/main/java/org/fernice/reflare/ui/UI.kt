package org.fernice.reflare.ui

import org.fernice.reflare.element.AWTComponentElement
import java.awt.Component
import java.awt.Graphics

interface FlareUI {

    val element: AWTComponentElement

    fun paintBorder(c: Component, g: Graphics)
}
