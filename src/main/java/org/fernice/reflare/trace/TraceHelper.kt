/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.trace

import org.fernice.flare.std.systemFlag


class TraceHelper private constructor() {

    val origins = mutableListOf<String>()

    fun traceReapplyOrigin(origin: String) {
        origins += origin
    }

    fun resetReapplyOrigins() {
        origins.clear()
    }

    companion object {

        @JvmStatic
        val TRACE_ENABLED = systemFlag("fernice.reflare.trace")

        fun createTraceHelper(): TraceHelper? {
            return if (TRACE_ENABLED) TraceHelper() else null
        }

        fun traceReapplyOrigin(helper: TraceHelper?, origin: String) {
            helper?.traceReapplyOrigin(origin)
        }

        fun resetReapplyOrigins(helper: TraceHelper?) {
            helper?.resetReapplyOrigins()
        }
    }
}