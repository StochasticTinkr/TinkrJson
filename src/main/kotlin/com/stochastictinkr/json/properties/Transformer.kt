package com.stochastictinkr.json.properties

import kotlin.properties.*
import kotlin.reflect.*

/**
 * A unidirectional transformer from type `A` to type `B`.
 */
fun interface Transformer<in A, out B> {
    /**
     * Convert the argument.
     */
    fun transform(a: A): B

    /**
     * Try to convert the argument, returning a [Result] of the conversion.
     */
    fun tryTransform(a: A): Result<B> = runCatching { transform(a) }
}

infix fun <A, B, C> Transformer<A, B>.then(next: Transformer<B, C>): Transformer<A, C> =
    ChainTransformer(this, next)

private class ChainTransformer<A, B, C>(
    private val inner: Transformer<A, B>,
    private val outer: Transformer<B, C>,
) : Transformer<A, C> {
    override fun transform(a: A): C = outer.transform(inner.transform(a))
    override fun tryTransform(a: A): Result<C> =
        inner
            .tryTransform(a)
            .fold(
                onSuccess = { outer.tryTransform(it) },
                onFailure = { Result.failure(it) },
            )
}

infix fun <T, A, B> ReadOnlyProperty<T, A>.transformWith(transformer: Transformer<A, B>): ReadOnlyProperty<T, B> {
    val outer = this
    return object : ReadOnlyProperty<T, B> {
        override fun getValue(thisRef: T, property: KProperty<*>) =
            transformer.transform(outer.getValue(thisRef, property))
    }
}
