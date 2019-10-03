/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlarePasswordFieldUI
import javax.swing.JPasswordField
import javax.swing.text.Document

@Suppress("UNUSED")
open class PasswordField : JPasswordField {

    constructor()
    constructor(document: Document, text: String?, columns: Int) : super(document, text, columns)
    constructor(columns: Int) : super(columns)
    constructor(text: String?) : super(text)
    constructor(text: String?, columns: Int) : super(text, columns)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlarePasswordFieldUI() })
    }
}