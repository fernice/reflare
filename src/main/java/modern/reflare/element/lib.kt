package modern.reflare.element

import kotlin.reflect.KProperty

class CSSProperty<T>(private val computation: () -> T) {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = computation()
        }
        return value!!
    }

    fun invalidate() {
        value = null
    }
}

fun <T> cssProperty(computation: () -> T): CSSProperty<T> {
    return CSSProperty(computation)
}
