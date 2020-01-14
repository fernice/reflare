/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

fun <K, V> fullyWeakReferenceHashMap(): ConcurrentReferenceHashMap<K, V> {
    return ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.WEAK, ConcurrentReferenceHashMap.ReferenceType.WEAK)
}

fun <K, V> weakReferenceHashMap(): ConcurrentReferenceHashMap<K, V> {
    return ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.WEAK, ConcurrentReferenceHashMap.ReferenceType.STRONG)
}

fun <K, V> weakReferenceHashMap(vararg initial: Pair<K, V>): ConcurrentReferenceHashMap<K, V> {
    val map = weakReferenceHashMap<K, V>()
    map.putAll(mapOf(*initial))
    return map
}