/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

internal fun <K, V> fullyWeakReferenceHashMap(): ConcurrentReferenceHashMap<K, V> {
    return ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.WEAK, ConcurrentReferenceHashMap.ReferenceType.WEAK)
}

internal fun <K, V> weakReferenceHashMap(): ConcurrentReferenceHashMap<K, V> {
    return ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.WEAK, ConcurrentReferenceHashMap.ReferenceType.STRONG)
}

internal fun <K, V> weakReferenceHashMap(vararg initial: Pair<K, V>): ConcurrentReferenceHashMap<K, V> {
    val map = weakReferenceHashMap<K, V>()
    map.putAll(mapOf(*initial))
    return map
}

internal fun <K, V> concurrentMap(): MutableMap<K, V> = ConcurrentHashMap()

internal fun <E> concurrentList(): MutableList<E> = CopyOnWriteArrayList()