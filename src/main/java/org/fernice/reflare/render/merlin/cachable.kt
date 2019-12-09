/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render.merlin

class Cachable<T : Any> {

    private var value: T? = null
    private var hash = 0
    private var dependencies: Array<out Any>? = null

    fun getOrPut(vararg dependencies: Any, block: () -> T): T {
        val hash = dependencies.contentHashCode()

        val cachedValue = value
        if (cachedValue != null && hash == this.hash && dependencies.contentEquals(this.dependencies!!)) {
            return cachedValue
        }

        val value = block()
        this.value = value
        this.hash = hash
        this.dependencies = dependencies
        return value
    }

    fun invalidate(){
        hash = 0
    }
}