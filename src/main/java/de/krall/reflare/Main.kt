package de.krall.reflare

import de.krall.flare.std.Some
import de.krall.reflare.element.into
import java.io.File
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    FlareLookAndFeel.init()

    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.title = "Mdrn Flr"
    frame.setSize(600, 400)

    val panel = JPanel()

    val title = JLabel("Form")
    title.into().classes.add("title")

    panel.add(title)

    val textField = JPasswordField()
    textField.columns = 15
    textField.into().id = Some("t1")

    panel.add(textField)

    val textField2 = JTextArea()
    textField2.columns = 15
    textField2.rows = 6
    textField2.into().id = Some("t2")

    panel.add(textField2)

    var dark = false

    val lightMode = File(FlareLookAndFeel::class.java.getResource("/test.css").file)
    val darkMode = File(FlareLookAndFeel::class.java.getResource("/darkmode.css").file)

    val button = JButton("Submit")
    button.addActionListener {
        if (dark) {
            frame.into().addStylesheet(lightMode)
            frame.into().removeStylesheet(darkMode)
        } else {
            frame.into().addStylesheet(darkMode)
            frame.into().removeStylesheet(lightMode)
        }
        dark = !dark
    }

    panel.add(button)

    val cancel = JButton("Cancel")

    panel.add(cancel)

    val comboBox = JComboBox<String>(arrayOf("Value 1", "Value 2"))

    panel.add(comboBox)

    frame.contentPane = panel

    frame.isVisible = true
}