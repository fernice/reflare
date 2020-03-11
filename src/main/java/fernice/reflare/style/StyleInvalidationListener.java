/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.style;

import org.jetbrains.annotations.NotNull;

public interface StyleInvalidationListener {

    void styleChanged(@NotNull StyleMatcher styleMatcher);
}
