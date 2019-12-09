/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.resource

internal interface Owner {

    val thread: Thread
}

internal interface Owned {

    val owner: Owner
}

internal inline fun checkResourceAccess(owned: Owned) {
    check(Thread.currentThread() == owned.owner.thread) { "resource may only be accessed by their owner's thread: owner ${owned.owner} <> current ${Thread.currentThread()}" }
}

internal inline fun checkResourceOperationAccess(first: Owned) {
    check(Thread.currentThread() == first.owner.thread) { "operations may only be performed by the resource's owner's thread: owner ${first.owner} <> current ${Thread.currentThread()}" }
}

internal inline fun checkResourceOperationAccess(first: Owned, second: Owned) {
    check(Thread.currentThread() == first.owner.thread) { "operations may only be performed by the resource's owner's thread (first argument): owner ${first.owner} <> current ${Thread.currentThread()}" }
    check(Thread.currentThread() == second.owner.thread) { "operations may only be performed by the resource's owner's thread (second argument): owner ${second.owner} <> current ${Thread.currentThread()}" }
}