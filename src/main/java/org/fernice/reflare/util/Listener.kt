/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

fun <T> broadcast(): Broadcast<T> {
    return Broadcast()
}

class Broadcast<T> {

    private val invalidationListeners: MutableList<() -> Unit> = mutableListOf()
    private val listeners: MutableList<(T) -> Unit> = mutableListOf()

    fun addInvalidationListener(listener: () -> Unit) {
        invalidationListeners.add(listener)
    }

    fun removeInvalidationListener(listener: () -> Unit) {
        invalidationListeners.add(listener)
    }

    fun addListener(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    operator fun plusAssign(listener: (T) -> Unit) {
        addListener(listener)
    }

    operator fun minusAssign(listener: (T) -> Unit) {
        removeListener(listener)
    }

    fun fire(event: T) {
        invalidationListeners.forEach { it() }
        listeners.forEach { it(event) }
    }
}