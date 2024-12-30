package com.stochastictinkr.json

/**
 * Represents a JSON null literal.
 */
data object JsonNull : JsonLiteral {
    override val isNull get() = true
    override val jsonObjectUnlessNull get() = null
    override val jsonArrayUnlessNull get() = null
    override val stringUnlessNull get() = null
    override val booleanUnlessNull get() = null
    override val numberUnlessNull get() = null
    override val intUnlessNull get() = null
    override val longUnlessNull get() = null
    override val floatUnlessNull get() = null

    override fun <R> unlessNull(block: JsonElement.() -> R): R? = null
}

/**
 * Convert the `null` receiver to a [JsonNull].
 */
fun Nothing?.toJsonNull() = JsonNull

/**
 * Convert the receiver to a [JsonNull] when it is `null`, otherwise convert it to a [JsonElement] using the provided
 * transformation function.
 */
inline fun <T> T?.toJsonNullOr(transform: (T) -> JsonElement): JsonElement = this?.let(transform) ?: JsonNull
