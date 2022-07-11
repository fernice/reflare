/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

interface Ref<T> {

    fun get(): T?
}

class WeakRef<T>(value: T) : WeakReference<T>(value), Ref<T>

class VacatingRef<T : Any>(value: T) : WeakReference<T>(value), Ref<T>, VacatingReferenceHolder {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get() ?: throw VacatedReferenceException()

    fun deref(): T = get() ?: throw VacatedReferenceException()

    override fun hasVacated(): Boolean {
        return get() == null
    }

    override fun toString(): String {
        val value = get()
        return if (value != null) "ref<$value>" else "ref<vacated>"
    }
}

interface VacatingReferenceHolder {

    fun hasVacated(): Boolean
}

class VacatedReferenceException : RuntimeException("reference has vacated")

operator fun <T> WeakReference<T>.component1(): T? {
    return get()
}