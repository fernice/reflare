/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("ScrollBarHelper")

package fernice.reflare

import javax.swing.JScrollBar

internal const val SCROLL_BAR_MINIMALISTIC_PROPERTY = "ScrollBar.reflare.minimalistic"
internal var JScrollBar.isMinimalistic: Boolean
    get() = getClientProperty(SCROLL_BAR_MINIMALISTIC_PROPERTY) == true
    set(lightweight) {
        putClientProperty(SCROLL_BAR_MINIMALISTIC_PROPERTY, lightweight)
    }
