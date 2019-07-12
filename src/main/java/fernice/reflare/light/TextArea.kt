/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareTextAreaUI
import javax.swing.JTextArea
import javax.swing.text.Document

@Suppress("UNUSED")
class TextArea : JTextArea {

    constructor()
    constructor(document: Document) : super(document)
    constructor(document: Document, text: String, columns: Int, rows: Int) : super(document, text, columns, rows)
    constructor(columns: Int, rows: Int) : super(columns, rows)
    constructor(text: String) : super(text)
    constructor(text: String, columns: Int, rows: Int) : super(text, columns, rows)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareTextAreaUI() })
    }
}