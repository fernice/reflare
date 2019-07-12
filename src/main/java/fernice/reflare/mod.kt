@file:JvmName("ElementHelper")

package fernice.reflare

import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import fernice.std.into
import org.fernice.reflare.element.element
import java.awt.Component

val Component.classes: MutableSet<String>
    get() = this.element.classes

var Component.id: String?
    get() = this.element.id.toNullable()
    set(value) {
        this.element.id = value.into()
    }

var Component.style: String
    get() = this.element.styleAttribute
    set(value) {
        this.element.styleAttribute = value
    }

fun <E> MutableSet<E>.addAll(vararg values: E) {
    for (value in values) {
        this.add(value)
    }
}

private fun <T> Option<T>.toNullable(): T? {
    return when (this) {
        is Some -> this.value
        is None -> null
    }
}