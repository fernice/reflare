/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.trace

import mu.KotlinLogging
import org.fernice.reflare.element.AWTComponentElement

class CountingTrace(val pass: Int) : RestyleTrace {

    private var count: Int = 0

    private val originCounts = mutableMapOf<String, Int>()

    override fun beginCSSPass() {
    }

    override fun traceRestyledElement(element: AWTComponentElement) {
        count++

        element.debug_traceHelper?.origins?.forEach { origin ->
            originCounts.merge(origin, 1, Int::plus)
        }
    }

    override fun endCSSPass() {
        if (TraceHelper.TRACE_ENABLED) {
            if (originCounts.containsKey("apply")) {
               // LOG.debug { "[$pass] applyCSS($count)" }
            } else {
                LOG.debug { "[$pass] $count restyled: origins " + originCounts.entries.joinToString { (name, count) -> "$name=$count" } }
            }
        }
        count = 0
        originCounts.clear()
    }

    companion object {
        private val LOG = KotlinLogging.logger { }
    }
}