package de.krall.reflare

import de.krall.flare.std.Some
import de.krall.reflare.element.into
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    FlareLookAndFeel.init()

    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.title = "Mdrn Flr"
    frame.setSize(600, 400)

    val panel = JPanel(BorderLayout(10, 10))

    val textField = JTextField()
    textField.columns = 15
    textField.into().id = Some("t1")

    val textField2 = JTextField()
    textField2.columns = 15
    textField2.into().id = Some("t2")

    panel.add(textField2, BorderLayout.NORTH)
    panel.add(textField, BorderLayout.SOUTH)

    frame.contentPane = panel

    frame.isVisible = true
}