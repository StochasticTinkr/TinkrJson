package com.stochastictinkr.json

/**
 * Represents a JSON number literal.
 */
sealed interface JsonNumber : JsonLiteral {
    companion object {
        /**
         * Creates a [JsonNumber] from a [Short]. The value will be represented as an [Int].
         */
        operator fun invoke(value: Short): JsonNumber = JsonInt(value.toInt())

        /**
         * Creates a [JsonNumber] from an [Int].
         */
        operator fun invoke(value: Int): JsonNumber = JsonInt(value)

        /**
         * Creates a [JsonNumber] from a [Long].
         */
        operator fun invoke(value: Long): JsonNumber = JsonLong(value)

        /**
         * Creates a [JsonNumber] from a [Float].
         */
        operator fun invoke(value: Float): JsonNumber = JsonFloat(value)

        /**
         * Creates a [JsonNumber] from a [Double].
         */
        operator fun invoke(value: Double): JsonNumber = JsonDouble(value)
    }
}

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Short?.toJsonNumberOrNull(): JsonElement = toJsonNullOr { JsonNumber(it) }

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Int?.toJsonNumberOrNull(): JsonElement = toJsonNullOr { JsonNumber(it) }

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Long?.toJsonNumberOrNull(): JsonElement = toJsonNullOr { JsonNumber(it) }

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Float?.toJsonNumberOrNull(): JsonElement = toJsonNullOr { JsonNumber(it) }

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Double?.toJsonNumberOrNull(): JsonElement = toJsonNullOr { JsonNumber(it) }

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Short.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Int.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Long.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Float.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Double.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonNumber].
 */
fun Boolean.toJsonBoolean() = JsonBoolean(this)

// Implementation details

private sealed class AbstractJsonNumber : JsonNumber {
    override val jsonNumber: JsonNumber get() = this
    override val jsonNumberOrNull: JsonNumber? get() = this
    override val numberOrNull: Number? get() = number
    abstract override val number: Number

    override fun toString() = "JsonNumber($number)"
}

private data class JsonInt(override val int: Int) : AbstractJsonNumber() {
    override val number: Int get() = int
    override val intOrNull: Int? get() = int
}

private data class JsonLong(override val long: Long) : AbstractJsonNumber() {
    override val number: Long get() = long
    override val longOrNull: Long? get() = long
}

private data class JsonFloat(override val float: Float) : AbstractJsonNumber() {
    override val number: Float get() = float
    override val floatOrNull: Float? get() = float
}

private data class JsonDouble(override val double: Double) : AbstractJsonNumber() {
    override val number: Double get() = double
    override val doubleOrNull: Double? get() = double
}

