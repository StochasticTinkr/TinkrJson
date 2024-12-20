package com.stochastictinkr.json

/**
 * Represents a JSON null literal.
 */
data object JsonNull : JsonLiteral {
    override val isNull get() = true
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
