/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui;

import fernice.reflare.light.FBoxLayout;
import java.awt.Container;
import javax.swing.plaf.UIResource;

public class FlareMenuLayout extends FBoxLayout implements UIResource {

    public FlareMenuLayout(Container target, int axis) {
        super(target, axis);
    }
}
