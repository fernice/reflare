/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.statistics

import org.fernice.flare.std.systemFlag
import org.fernice.logging.FLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

private val PRINT_STATISTICS = systemFlag("fernice.reflare.printStatistics", default = false)

object Statistics {

    private val counters = ConcurrentHashMap<String, Int>()

    @JvmStatic
    fun increment(counter: String) {
        counters.merge(counter, 1, Int::plus)
    }

    init {
        thread(start = PRINT_STATISTICS) {
            while (true) {
                Thread.sleep(5000)

                val counters = counters.entries.joinToString { (counter, count) -> "$counter: $count" }

                LOG.trace(counters)
            }
        }
    }

    private val LOG = FLogging.logger { }
}