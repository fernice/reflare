package org.fernice.reflare.element

import org.fernice.flare.dom.Element
import org.fernice.flare.style.ComputedValues
import org.fernice.reflare.util.Broadcast
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

inline fun <T> PropertyBuilder<*, T>.computeStyle(crossinline computation: (ComputedValues) -> T) {
    computeStyle { _, style -> computation(style) }
}

inline fun <E : Element, T> PropertyBuilder<E, T>.computeStyle(crossinline computation: (E, ComputedValues) -> T) {
    compute { element ->
        val data = element.getData()
        val values = data?.styles?.primary ?: return@compute null

        computation(element, values)
    }
}

fun <E : Element, T> E.property(defaultValue: T, init: PropertyBuilder<E, T>.() -> Unit): CacheProperty<T> {
    val builder = PropertyBuilder<E, T>()
    builder.init()

    val computation = builder.computation

    return if (computation != null) {
        val property = ComputableCacheProperty(this, defaultValue, computation)
        val invalidationListener = { property.invalidate() }
        builder.dependencies.forEach { dependency -> dependency.register(invalidationListener) }

        property
    } else {
        DefaultCacheProperty(defaultValue)
    }
}

fun <E : Element, T> E.property(init: PropertyBuilder<E, T>.() -> Unit): CacheProperty<T> {
    val builder = PropertyBuilder<E, T>()
    builder.init()

    val computation = builder.computation ?: throw IllegalArgumentException("missing computation for non-default property")

    val property = ComputableCacheProperty(this, null, computation)
    val invalidationListener = { property.invalidate() }
    builder.dependencies.forEach { dependency -> dependency.register(invalidationListener) }

    return property
}

fun KProperty0<*>.invalidate() {
    this.isAccessible = true
    (this.getDelegate() as ComputableCacheProperty<*, *>).invalidate()
}

class PropertyBuilder<E : Element, T>(
    val dependencies: MutableList<Dependency<*>> = mutableListOf(),
    var computation: ((E) -> T?)? = null
) {

    fun <R> dependsOn(broadcast: Broadcast<R>, block: (R) -> Any) {
        dependencies.add(Dependency(broadcast, block))
    }

    fun dependsOn(broadcast: Broadcast<*>) {
        dependencies.add(Dependency(broadcast))
    }

    fun compute(block: (E) -> T?) {
        computation = block
    }
}

class Dependency<R>(private val broadcast: Broadcast<R>, private val mapper: ((R) -> Any)? = null) {

    fun register(invalidationListener: () -> Unit) {
        if (mapper != null) {
            broadcast.addListener(ChangeAwareInvalidationListener(mapper, invalidationListener))
        } else {
            broadcast.addInvalidationListener(invalidationListener)
        }
    }
}

private class ChangeAwareInvalidationListener<R>(val mapper: (R) -> Any, val invalidationListener: () -> Unit) : (R) -> Unit {

    private var last: Any? = null

    override fun invoke(value: R) {
        val new = mapper(value)

        if (new != last) {
            last = new

            invalidationListener()
        }
    }
}

interface CacheProperty<T> {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

class DefaultCacheProperty<T>(private val defaultValue: T) : CacheProperty<T> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = defaultValue
}

class ComputableCacheProperty<E : Element, T>(
    private val element: E,
    private val defaultValue: T?,
    private val computation: (E) -> T?
) : CacheProperty<T> {

    private var value: T? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = this.value

        return if (value == null) {
            val computed = computation(element)

            if (computed != null) {
                this.value = computed
                computed
            } else {
                defaultValue ?: throw IllegalStateException("non-default property must compute a value")
            }
        } else {
            value
        }
    }

    fun invalidate() {
        value = null
    }
}