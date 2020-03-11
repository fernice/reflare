/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

import java.util.concurrent.locks.ReentrantLock

internal class Box<E>(private val value: E) {

    private val lock = ReentrantLock()

    fun <R> use(block: (E) -> R): R {
        lock.lock()
        return try {
            block(value)
        } finally {
            lock.unlock()
        }
    }
}