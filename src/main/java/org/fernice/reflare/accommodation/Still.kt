/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.accommodation

import fernice.reflare.classes
import org.fernice.reflare.layout.VerticalLayout
import javax.swing.JLabel
import javax.swing.JPanel

private val test_still = """
<html>
The sky is falling in. A world came to an end.<br/>
<br/>
<font color='#FF6542'>[Him]</font><br/>
I believed it would never come this far.<br/>
<br/>
It should not have come this far.<br/>
<br/>
<font color='#779FA1'>[Her]</font><br/>
We can't change what happened.<br/>
<br/>
<font color='#FF6542'>[Him]</font><br/>
We should have stopped it.<br/>
<br/>
<br/>
[We did not]<br/>
[I know]<br/>
</html>
""".trimIndent()

class StillPanel : JPanel() {

    private val textLabel: JLabel

    init {
        layout = VerticalLayout()
        classes.add("still")

        textLabel = JLabel()
        textLabel.text = test_still
        add(textLabel)
    }
}