/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import fernice.reflare.StyleRoot
import fernice.reflare.Stylesheet
import javax.swing.JFileChooser

private val FileChooserStyleRoot by lazy {
    val styleRoot = StyleRoot()
    styleRoot.addStylesheet(Stylesheet.fromResource("/reflare/style/file_chooser.css", CSSEngine::class.java))
    styleRoot
}

class FileChooserElement(fileChooser: JFileChooser) : ComponentElement(fileChooser) {

    override val localName get() = "filechooser"

    init {
        styleRootValue = FileChooserStyleRoot
    }
}
