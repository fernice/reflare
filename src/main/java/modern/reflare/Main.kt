package modern.reflare

import de.krall.flare.std.Some
import modern.reflare.element.into
import java.awt.Component
import java.io.File
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFormattedTextField
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    FlareLookAndFeel.init()

    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.title = "Mdrn Flr"
    frame.setSize(600, 400)

    val panel = JPanel()

    val title = JLabel("Form")
    title.getStyleClasses().add("title")

    panel.add(title)

    val textField = JFormattedTextField()
    textField.columns = 15

    panel.add(textField)

    val textField2 = JTextArea()
    textField2.columns = 15
    textField2.rows = 6
    textField2.into().id = Some("t2")

    panel.add(textField2)

    var dark = false

    val lightMode = File(FlareLookAndFeel::class.java.getResource("/test.css").file)
    val darkMode = File(FlareLookAndFeel::class.java.getResource("/darkmode.css").file)

    val submit = JButton("Submit")
    submit.addActionListener {
        if (dark) {
            frame.into().addStylesheet(lightMode)
            frame.into().removeStylesheet(darkMode)
        } else {
            frame.into().addStylesheet(darkMode)
            frame.into().removeStylesheet(lightMode)
        }
        dark = !dark
    }
    submit.into().classes.add("submit")

    panel.add(submit)

    val cancel = JButton("Cancel")

    panel.add(cancel)

    val comboBox = JComboBox<String>(arrayOf("Value 1", "Value 2"))

    panel.add(comboBox)

    val checkbox = JCheckBox("remember me")
    panel.add(checkbox)

    val tabbedPane = JTabbedPane()
    tabbedPane.addTab("first", panel)
    tabbedPane.addTab("second", JButton("Lol"))

    frame.contentPane = tabbedPane

    frame.isVisible = true

}


fun Component.getStyleClasses(): MutableList<String> {
    return this.into().classes
}