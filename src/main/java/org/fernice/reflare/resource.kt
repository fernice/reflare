/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare

import java.awt.Color
import java.awt.Font
import java.awt.Insets
import javax.swing.plaf.UIResource

internal class FlareFontResource(name: String, style: Int, size: Int) : Font(name, style, size), UIResource
internal class FlareColorResource(r: Int, g: Int, b: Int, a: Int) : Color(r, g, b, a), UIResource
internal class FlareInsetsResource(top: Int, right: Int, bottom: Int, left: Int) : Insets(top, left, bottom, right), UIResource