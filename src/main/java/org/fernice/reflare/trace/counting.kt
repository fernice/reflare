/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.trace

import mu.KotlinLogging
import org.fernice.reflare.element.AWTComponentElement
import java.util.concurrent.atomic.AtomicInteger

private val totalElementRestyleCount = AtomicInteger()

class CountingTrace(private val name: String, private val pass: Int) : RestyleTrace {

    private var count: Int = 0

    private val originCounts = mutableMapOf<String, Int>()

    private var rootElement: AWTComponentElement? = null

    override fun beginCSSPass() {
    }

    override fun traceRestyledElement(element: AWTComponentElement) {
        count++

        element.debug_traceHelper?.origins?.forEach { origin ->
            originCounts.merge(origin, 1, Int::plus)
        }
    }

    override fun traceRootElement(element: AWTComponentElement) {
        rootElement = element
    }

    override fun endCSSPass() {
        if (TraceHelper.TRACE_ENABLED) {
            val totalRestyledElements = totalElementRestyleCount.get()
            val origins = originCounts.entries.joinToString { (name, count) -> "$name=$count" }
            val rootElementName = rootElement?.javaClass?.simpleName ?: "none"
            LOG.debug { "[$name $pass] $totalRestyledElements+$count restyled from $rootElementName: origins $origins" }
        }
        totalElementRestyleCount.getAndAdd(count)
        count = 0
        originCounts.clear()
        rootElement = null
    }

    companion object {
        private val LOG = KotlinLogging.logger { }
    }
}