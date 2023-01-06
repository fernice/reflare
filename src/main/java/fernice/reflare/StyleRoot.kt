/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare

import org.fernice.flare.style.parser.QuirksMode
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import org.fernice.flare.style.StyleRoot as StyleRootPeer

class StyleRoot {

    private val lock = ReentrantLock()
    internal val peer = StyleRootPeer(QuirksMode.NoQuirks)

    private val mutableStylesheets = mutableListOf<Stylesheet>()

    val stylesheets: List<Stylesheet>
        get() = lock.withLock { mutableStylesheets.toList() }

    fun addStylesheet(stylesheet: Stylesheet) {
        lock.withLock {
            mutableStylesheets.add(stylesheet)
            peer.addStylesheet(stylesheet.peer)
        }
        CSSEngine.invalidate()
    }

    fun removeStylesheet(stylesheet: Stylesheet) {
        lock.withLock {
            mutableStylesheets.remove(stylesheet)
            peer.removeStylesheet(stylesheet.peer)
        }
        CSSEngine.invalidate()
    }
}
