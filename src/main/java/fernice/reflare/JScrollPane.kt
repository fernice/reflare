/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("ScrollPaneHelper")

package fernice.reflare

import javax.swing.JScrollPane

internal const val SCROLL_PANE_INLINE_PROPERTY = "ScrollPane.reflare.inline"
var JScrollPane.isInline: Boolean
    get() = getClientProperty(SCROLL_PANE_INLINE_PROPERTY) == true
    set(inline) {
        putClientProperty(SCROLL_PANE_INLINE_PROPERTY, inline)
    }
