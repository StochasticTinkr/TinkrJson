package com.stochastictinkr.json

/**
 * Represents a JSON string literal.
 * @property string The string value.
 */
data class JsonString(override val string: String) : JsonLiteral {
    override val jsonString: JsonString get() = this
    override val jsonStringOrNull: JsonString? get() = this
    override val stringOrNull get() = string
    override fun toString() = """JsonString("$string")"""
}

/**
 * Convert the receiver to a [JsonString].
 */
fun String.toJsonString() = JsonString(this)

/**
 * Convert the receiver to either a [JsonString] or [JsonNull].
 */
fun String?.toJsonStringOrNull(): JsonElement = toJsonNullOr { JsonString(it) }
