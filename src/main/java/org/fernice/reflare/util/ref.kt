/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class VacatingReference<T : Any>(value: T) : WeakReference<T>(value), VacatingReferenceHolder {

    fun deref(): T = get() ?: throw VacatedReferenceException()

    override fun hasVacated(): Boolean = get() == null

    override fun toString(): String {
        val value = get()
        return if (value != null) "ref<$value>" else "ref<vacated>"
    }
}

interface VacatingReferenceHolder {

    fun hasVacated(): Boolean
}

class VacatedReferenceException : RuntimeException("reference has vacated")
