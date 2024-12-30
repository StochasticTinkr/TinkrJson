package com.stochastictinkr.json.properties

import kotlin.properties.*
import kotlin.reflect.*

/**
 * A bidirectional converter between two types `A` and `B`.
 *
 * @param A The source type.
 * @param B The target type.
 * @property forward The transformer to convert from `A` to `B`.
 * @property reverse The transformer to convert from `B` to `A`.
 */
class Converter<A, B>(
    val forward: Transformer<A, B>,
    val reverse: Transformer<B, A>,
) {

    fun forward(a: A): B = forward.transform(a)
    fun reverse(b: B): A = reverse.transform(b)

    /**
     * Swap the forward and reverse transformers.
     */
    fun swap(): Converter<B, A> = Converter(reverse, forward)
}

/**
 * Compose two converters.
 */
infix fun <A, B, C> Converter<A, B>.then(next: Converter<B, C>): Converter<A, C> =
    Converter(
        forward = this.forward then next.forward,
        reverse = next.reverse then this.reverse,
    )

infix fun <T, A, B> ReadWriteProperty<T, A>.convertWith(converter: Converter<A, B>): ReadWriteProperty<T, B> {
    val outer = this
    return object : ReadWriteProperty<T, B> {
        override fun getValue(thisRef: T, property: KProperty<*>) = converter.forward(outer.getValue(thisRef, property))
        override fun setValue(thisRef: T, property: KProperty<*>, value: B) =
            outer.setValue(thisRef, property, converter.reverse(value))
    }
}

fun <T, A : Any, B : Any> ReadWriteProperty<T, A?>.convertNullable(converter: Converter<A, B>): ReadWriteProperty<T, B?> {
    val outer = this
    return object : ReadWriteProperty<T, B?> {
        override fun getValue(thisRef: T, property: KProperty<*>) =
            outer.getValue(thisRef, property)?.let { converter.forward(it) }

        override fun setValue(thisRef: T, property: KProperty<*>, value: B?) =
            outer.setValue(thisRef, property, value?.let { converter.reverse(it) })
    }
}

fun <T, A : Any> ReadWriteProperty<T, A?>.nonNull(): ReadWriteProperty<T, A> {
    val outer = this
    return object : ReadWriteProperty<T, A> {
        override fun getValue(thisRef: T, property: KProperty<*>) = requireNotNull(outer.getValue(thisRef, property)) {
            "Property ${property.name} should not be null."
        }
        override fun setValue(thisRef: T, property: KProperty<*>, value: A) = outer.setValue(thisRef, property, value)
    }
}

infix fun <T, A, B> ReadOnlyProperty<T, A>.convertWith(converter: Converter<A, B>): ReadOnlyProperty<T, B> {
    val outer = this
    return object : ReadOnlyProperty<T, B> {
        override fun getValue(thisRef: T, property: KProperty<*>) = converter.forward(outer.getValue(thisRef, property))
    }
}

