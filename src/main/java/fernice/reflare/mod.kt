package fernice.reflare

import fernice.std.Option
import org.fernice.reflare.element.into
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