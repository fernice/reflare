package fernice.reflare

import fernice.std.Option
import org.fernice.reflare.element.element
import java.awt.Component

val Component.classes: MutableList<String>
    get() = this.element.classes

var Component.id: Option<String>
    get() = this.element.id
    set(value) {
        this.element.id = value
    }

var Component.style: String
    get() = this.element.styleAttribute
    set(value) {
        this.element.styleAttribute = value
    }

fun <E> MutableList<E>.addAll(vararg values: E) {
    for (value in values) {
        this.add(value)
    }
}