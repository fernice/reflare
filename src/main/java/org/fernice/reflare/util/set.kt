/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

internal fun <E> MutableCollection<E>.setAll(elements: Collection<E>) {
    clear()
    addAll(elements)
}

internal fun <E> MutableCollection<E>.setAll(vararg elements: E) {
    clear()
    addAll(elements)
}