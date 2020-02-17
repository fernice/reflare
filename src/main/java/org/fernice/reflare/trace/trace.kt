/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.trace

import org.fernice.flare.EngineContext
import org.fernice.reflare.element.AWTComponentElement
import java.util.concurrent.ConcurrentHashMap

private val traceCountHolder = ConcurrentHashMap<String, Int>()

internal inline fun trace(context: EngineContext, name: String = "unknown", block: (EngineContext) -> Unit) {
    if (!TraceHelper.TRACE_ENABLED) {
        return block(context)
    }

    val traceContext = when (context) {
        is TracingContext -> context
        else -> TracingContext(context, CountingTrace(name, traceCountHolder.merge(name, 1, Int::plus) ?: -1))
    }

    traceContext.trace.beginCSSPass()
    try {
        block(traceContext)
    } finally {
        traceContext.trace.endCSSPass()
    }
}

class TracingContext(
    delegate: EngineContext,
    val trace: RestyleTrace
) : EngineContext by delegate

private fun EngineContext.trace(): RestyleTrace? {
    return (this as? TracingContext)?.trace
}

internal fun EngineContext.traceElement(element: AWTComponentElement) {
    trace()?.traceRestyledElement(element)
}

internal fun EngineContext.traceOrigins(element: AWTComponentElement) {
    trace()?.traceElementOrigins(element)
}

internal fun EngineContext.traceRoot(element: AWTComponentElement) {
    trace()?.traceRootElement(element)
}