/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.util

internal fun <E> observableMutableSetOf(vararg elements: E): ObservableMutableSet<E> {
    return ObservableMutableSetWrapper(mutableSetOf(*elements))
}

internal inline fun <reified E> observableMutableSetOf(elements: Collection<E>, noinline listener: () -> Unit): ObservableMutableSet<E> {
    val list = observableMutableSetOf(*elements.toTypedArray())
    list.addInvalidationListener(listener)
    return list
}

internal fun <E> observableMutableSetOf(listener: () -> Unit): ObservableMutableSet<E> {
    val list = observableMutableSetOf<E>()
    list.addInvalidationListener(listener)
    return list
}

internal interface ObservableCollection {

    fun addInvalidationListener(listener: () -> Unit)
    fun removeInvalidationListener(listener: () -> Unit)
}

internal interface ObservableMutableSet<E> : MutableSet<E>, ObservableCollection

private abstract class ObservableCollectionWrapperBase<E, S : Collection<E>>(protected val delegate: S) : Collection<E>, ObservableCollection {

    protected fun mutableIterator(): Iterator<E> = MutableIteratorWrapper(iterator() as MutableIterator<E>)

    protected inline fun <R> withInvalidation(block: () -> R): R {
        val result = block()
        invalidated()
        return result
    }

    protected inline fun withConditionalInvalidation(block: () -> Boolean): Boolean {
        val result = block()
        if (result) {
            invalidated()
        }
        return result
    }

    private fun invalidated() {
        listeners.forEach { it() }
    }

    private val listeners = mutableListOf<() -> Unit>()

    override fun addInvalidationListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    override fun removeInvalidationListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private inner class MutableIteratorWrapper<E>(private val iterator: MutableIterator<E>) : MutableIterator<E> {

        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): E = iterator.next()

        override fun remove() = withInvalidation { iterator.remove() }
    }
}

private open class ObservableSetWrapper<E, S : Set<E>>(delegate: S) : ObservableCollectionWrapperBase<E, S>(delegate), Set<E> {

    override val size: Int
        get() = delegate.size

    override fun contains(element: E): Boolean = delegate.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = delegate.containsAll(elements)
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): Iterator<E> = delegate.iterator()
}

private class ObservableMutableSetWrapper<E>(delegate: MutableSet<E>) : ObservableSetWrapper<E, MutableSet<E>>(delegate), MutableSet<E>,
    ObservableMutableSet<E> {

    override fun add(element: E): Boolean = withConditionalInvalidation { delegate.add(element) }
    override fun addAll(elements: Collection<E>): Boolean = withConditionalInvalidation { delegate.addAll(elements) }

    override fun remove(element: E): Boolean = withConditionalInvalidation { delegate.remove(element) }
    override fun removeAll(elements: Collection<E>): Boolean = withConditionalInvalidation { delegate.removeAll(elements) }

    override fun retainAll(elements: Collection<E>): Boolean = withConditionalInvalidation { delegate.retainAll(elements) }

    override fun clear() = withInvalidation { delegate.clear() }

    override fun iterator(): MutableIterator<E> = delegate.iterator()
}

