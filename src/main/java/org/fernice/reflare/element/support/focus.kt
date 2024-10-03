/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element.support

import java.awt.Component
import java.awt.Window
import javax.swing.FocusManager

internal val Component.isFocusWithin: Boolean
    get() {
        var current: Component? = FocusManager.getCurrentManager().focusOwner
        while (current != null && current !is Window) {
            if (current === this) return true
            current = current.parent
        }
        return false
    }
