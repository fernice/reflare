@file:JvmName("StyleHelper")

package fernice.reflare

import org.fernice.reflare.element.element
import org.intellij.lang.annotations.Language
import java.awt.Component

val Component.classes: MutableSet<String>
    get() = this.element.classes

var Component.id: String?
    get() = this.element.id
    set(value) {
        this.element.id = value
    }

@set:Language(value = "CSS", prefix = "* {", suffix = "}")
var Component.style: String
    get() = this.element.styleAttributeValue
    set(value) {
        this.element.styleAttributeValue = value
    }

fun <E> MutableSet<E>.addAll(vararg values: E) {
    for (value in values) {
        this.add(value)
    }
}