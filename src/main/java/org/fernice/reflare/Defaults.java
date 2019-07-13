/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare;


import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

public interface Defaults {

    Font FONT_SERIF = new FlareFontResource("serif", Font.PLAIN, 16);

    Color COLOR_TRANSPARENT = new FlareColorResource(0, 0, 0, 0);
    Color COLOR_GRAY_TRANSLUCENT = new FlareColorResource(0, 0, 0, 40);
    Color COLOR_BLACK = new FlareColorResource(255, 255, 255, 255);
    Color COLOR_WHITE = new FlareColorResource(255, 255, 255, 255);

    Insets INSETS_EMPTY = new FlareInsetsResource(0, 0, 0, 0);
}

