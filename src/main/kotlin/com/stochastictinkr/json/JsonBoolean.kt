package com.stochastictinkr.json

/**
 * Represents a JSON boolean literal.
 */
sealed class JsonBoolean : JsonLiteral {
    override val booleanOrNull get() = boolean

    override fun toString() = "JsonBoolean($boolean)"
    override fun equals(other: Any?) = this === other
    override fun hashCode() = boolean.hashCode()

    /**
     * Represents a JSON boolean literal with the value `false`.
     */
    data object False : JsonBoolean() {
        override val boolean = false
    }

    /**
     * Represents a JSON boolean literal with the value `true`.
     */
    data object True : JsonBoolean() {
        override val boolean = true
    }

    companion object {
        /**
         * Creates a [JsonBoolean] from a [Boolean].
         */
        operator fun invoke(value: Boolean): JsonBoolean = if (value) True else False
    }
}

/**
 * Convert the receiver to either a [JsonBoolean] or [JsonNull].
 */
fun Boolean?.toJsonBooleanOrNull(): JsonElement = when (this) {
    true -> JsonBoolean.True
    false -> JsonBoolean.False
    null -> JsonNull
}
