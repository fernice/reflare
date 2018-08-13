package modern.reflare.element

import de.krall.flare.std.Option
import java.awt.Component
import java.io.File
import javax.swing.JFrame

val Component.classes: MutableList<String>
    get() = this.into().classes

var Component.id: Option<String>
    get() = this.into().id
    set(value) {
        this.into().id = value
    }

fun <E> MutableList<E>.addAll(vararg values: E) {
    for (value in values) {
        this.add(value)
    }
}

fun JFrame.addStylesheetResource(resource: String) {
    this.into().addStylesheetResource(resource)
}

fun JFrame.addStylesheet(file: File) {
    this.into().addStylesheet(file)
}

fun JFrame.removeStylesheet(file: File) {
    this.into().removeStylesheet(file)
}
