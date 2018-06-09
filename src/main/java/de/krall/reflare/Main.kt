package de.krall.reflare

import de.krall.flare.std.Some
import de.krall.reflare.element.into
import java.io.File
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    FlareLookAndFeel.init()

    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.title = "Mdrn Flr"
    frame.setSize(600, 400)

    frame.into().addStylesheet(File(FlareLookAndFeel::class.java.getResource("/darkmode.css").file))

    val panel = JPanel()

    val title = JLabel("Form")
    title.into().classes.add("title")

    panel.add(title)

    val textField = JTextField()
    textField.columns = 15
    textField.into().id = Some("t1")

    panel.add(textField)

    val textField2 = JTextField()
    textField2.columns = 15
    textField2.into().id = Some("t2")

    panel.add(textField2)

    val button = JButton("Submit")

    panel.add(button)

    val cancel = JButton("Cancel")

    panel.add(cancel)

    frame.contentPane = panel

    frame.isVisible = true
}