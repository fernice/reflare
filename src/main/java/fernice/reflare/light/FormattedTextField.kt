/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.light

import org.fernice.reflare.ui.FlareFormattedTextFieldUI
import java.text.Format
import javax.swing.JFormattedTextField

@Suppress("UNUSED")
open class FormattedTextField : JFormattedTextField {

    constructor()
    constructor(formatter: AbstractFormatter) : super(formatter)
    constructor(formatterFactory: AbstractFormatterFactory) : super(formatterFactory)
    constructor(formatterFactory: AbstractFormatterFactory, value: Any) : super(formatterFactory, value)
    constructor(format: Format) : super(format)
    constructor(value: Any) : super(value)

    override fun updateUI() {
        super.setUI(integrationDependent(this) { FlareFormattedTextFieldUI() })
    }
}