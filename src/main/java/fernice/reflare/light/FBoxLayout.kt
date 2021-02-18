/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package fernice.reflare.light

import org.fernice.reflare.element.element
import sun.swing.MenuItemLayoutHelper
import sun.swing.SwingUtilities2
import java.awt.Container
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPopupMenu

open class FBoxLayout(target: Container?, axis: Int) : BoxLayout(target, axis) {
    override fun maximumLayoutSize(target: Container): Dimension {
        restyleNecessaryComponents(target)
        return super.maximumLayoutSize(target)
    }

    override fun preferredLayoutSize(target: Container): Dimension {
        if (target is JPopupMenu) {
            target.putClientProperty(MenuItemLayoutHelper.MAX_ARROW_WIDTH, null)
            target.putClientProperty(MenuItemLayoutHelper.MAX_CHECK_WIDTH, null)
            target.putClientProperty(MenuItemLayoutHelper.MAX_ACC_WIDTH, null)
            target.putClientProperty(MenuItemLayoutHelper.MAX_TEXT_WIDTH, null)
            target.putClientProperty(MenuItemLayoutHelper.MAX_ICON_WIDTH, null)
            target.putClientProperty(MenuItemLayoutHelper.MAX_LABEL_WIDTH, null)
            target.putClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, null)
            if (target.componentCount == 0) {
                return Dimension(0, 0)
            }
        }
        restyleNecessaryComponents(target)
        // Make BoxLayout recalculate cached preferred sizes
        super.invalidateLayout(target)
        return super.preferredLayoutSize(target)
    }

    override fun minimumLayoutSize(target: Container): Dimension {
        restyleNecessaryComponents(target)
        return super.minimumLayoutSize(target)
    }

    override fun layoutContainer(target: Container) {
        restyleNecessaryComponents(target)
        super.layoutContainer(target)
    }

    private fun restyleNecessaryComponents(target: Container) {
        for (i in 0 until target.componentCount) {
            val component = target.getComponent(i)
            val element = component.element

            element.restyleIfNecessary()
        }
    }
}
