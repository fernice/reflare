package org.fernice.reflare.layout

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager

open class VerticalLayout(var gap: Int = 0) : LayoutManager {

    override fun addLayoutComponent(name: String, c: Component) {}

    override fun layoutContainer(parent: Container) {
        val insets = parent.insets
        val size = parent.size
        val width = size.width - insets.left - insets.right
        var height = insets.top

        var i = 0
        val c = parent.componentCount
        while (i < c) {
            val m = parent.getComponent(i)
            if (m.isVisible) {
                m.setBounds(insets.left, height, width, m.preferredSize.height)
                height += m.size.height + gap

            }
            i++
        }
    }

    override fun minimumLayoutSize(parent: Container): Dimension {
        return preferredLayoutSize(parent)
    }

    override fun preferredLayoutSize(parent: Container): Dimension {
        val insets = parent.insets
        val pref = Dimension(0, 0)

        var i = 0
        val c = parent.componentCount
        while (i < c) {
            val m = parent.getComponent(i)
            if (m.isVisible) {
                val componentPreferredSize = parent.getComponent(i).preferredSize
                pref.height += componentPreferredSize.height + gap
                pref.width = Math.max(pref.width, componentPreferredSize.width)
            }
            i++
        }
        pref.height = pref.height - gap

        pref.width += insets.left + insets.right
        pref.height += insets.top + insets.bottom

        return pref
    }

    override fun removeLayoutComponent(c: Component) {}

}