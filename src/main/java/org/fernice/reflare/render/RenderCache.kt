/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.render

import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

class RenderCache {

    private val entries: MutableMap<String, CacheEntry> = ConcurrentHashMap()

    inline fun <reified T : Any> compute(key: String, vararg hashables: Any, noinline block: () -> T): T {
        return compute(key, T::class, hashables, block = block)
    }

    fun <T : Any> compute(key: String, type: KClass<T>, vararg hashables: Any, block: () -> T): T {
        return type.cast(entries.compute(key) { _, entry ->
            val hash = Objects.hash(hashables)

            if (entry == null || hash != entry.hash) {
                CacheEntry(hash, block())
            } else {
                entry
            }
        })
    }
}

private data class CacheEntry(val hash: Int, val value: Any)