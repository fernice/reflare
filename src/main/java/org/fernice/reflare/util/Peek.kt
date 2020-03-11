/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

internal fun <E> List<E>.peekIterator(): PeekIterator<E> = PeekIterator(this)

internal class PeekIterator<E>(private val list: List<E>, private var index: Int = 0) : Iterator<E> {

    override fun hasNext(): Boolean = index < list.size

    override fun next(): E = list[index++]

    fun peek(): E = list[index]
}