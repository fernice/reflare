package org.fernice.reflare.element

import fernice.std.None
import fernice.std.Some
import org.fernice.flare.dom.Element
import org.fernice.flare.style.ComputedValues
import kotlin.reflect.KProperty

class CSSProperty<T>(private val element: Element, private val defaultValue: T, private val computation: (ComputedValues) -> T) {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            val data = element.getData()
            val styles = when (data) {
                is Some -> data.value
                is None -> return defaultValue
            }.styles.primary

            val values = when (styles) {
                is Some -> styles.value
                is None -> return defaultValue
            }

            value = computation(values)
        }
        return value!!
    }

    fun invalidate() {
        value = null
    }
}

fun <T> Element.cssProperty(defaultValue: T, computation: (ComputedValues) -> T): CSSProperty<T> {
    return CSSProperty(this, defaultValue, computation)
}
