package com.stochastictinkr.json.properties

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
     * Compose two converters.
     */
    infix fun <C> then(next: Converter<B, C>): Converter<A, C> =
        Converter(
            forward = this.forward then next.forward,
            reverse = next.reverse then this.reverse,
        )
}


