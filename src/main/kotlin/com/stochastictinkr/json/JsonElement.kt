package com.stochastictinkr.json

import com.stochastictinkr.json.walker.*

/**
 * Sealed interface representing a JSON element.
 */
sealed interface JsonElement {
    /**
     * this element as a [JsonObject], or null if this element is not a JSON object.
     */
    val jsonObjectOrNull: JsonObject? get() = null

    /**
     * this element as a [JsonArray], or null if this element is not a JSON array.
     */
    val jsonArrayOrNull: JsonArray? get() = null

    /**
     * The String value of this element, or null if this element is not a [JsonString] literal.
     */
    val stringOrNull: String? get() = null

    /**
     * The Boolean value of this element, or null if this element is not a [JsonBoolean] literal.
     */
    val booleanOrNull: Boolean? get() = null

    /**
     * The Number value of this element, or null if this element is not a [JsonNumber] literal.
     */
    val numberOrNull: Number? get() = null

    /**
     * The Int value of this element, or null if this element is not a integer [JsonNumber] literal.
     */
    val intOrNull: Int? get() = null

    /**
     * The Long value of this element, or null if this element is not a long [JsonNumber] literal.
     */
    val longOrNull: Long? get() = null

    /**
     * The Float value of this element, or null if this element is not a float [JsonNumber] literal.
     */
    val floatOrNull: Float? get() = null

    /**
     * The Double value of this element, or null if this element is not a double [JsonNumber] literal.
     */
    val doubleOrNull: Double? get() = null

    /**
     * This element as a [JsonNumber], or null if this element is not a JSON number.
     */
    val jsonNumberOrNull: JsonNumber? get() = null

    /**
     * This element as a [JsonObject], or throws an error if this element is not a JSON object.
     */
    val jsonObject: JsonObject get() = error("Expected JsonObject, but was $this")

    /**
     * This element as a [JsonArray], or throws an error if this element is not a JSON array.
     */
    val jsonArray: JsonArray get() = error("Expected JsonArray, but was $this")

    /**
     * The String value of this element, or throws an error if this element is not a [JsonString] literal.
     */
    val string: String get() = error("Expected JsonString, but was $this")

    /**
     * The Boolean value of this element, or throws an error if this element is not a [JsonBoolean] literal.
     */
    val boolean: Boolean get() = error("Expected JsonBoolean, but was $this")

    /**
     * The Number value of this element, or throws an error if this element is not a [JsonNumber] literal.
     */
    val number: Number get() = error("Expected JsonNumber, but was $this")

    /**
     * The Numeric value of this element converted to an Int, or throws an error if this element is not a [JsonNumber] literal.
     */
    val int: Int get() = number.toInt()

    /**
     * The Numeric value of this element converted to a Long, or throws an error if this element is not a [JsonNumber] literal.
     */
    val long: Long get() = number.toLong()

    /**
     * The Numeric value of this element converted to a Float, or throws an error if this element is not a [JsonNumber] literal.
     */
    val float: Float get() = number.toFloat()

    /**
     * The Numeric value of this element converted to a Double, or throws an error if this element is not a [JsonNumber] literal.
     */
    val double: Double get() = number.toDouble()

    /**
     * The Numeric value of this element, or throws an error if this element is not a [JsonNumber] literal.
     */
    val jsonNumber: JsonNumber get() = error("Expected JsonNumber, but was $this")

    /**
     * Returns true if this element is null, false otherwise.
     */
    val isNull get() = false

    /**
     * Makes a deep copy of this element. Throws an error on circular references.
     *
     * If the same element is encountered multiple times, it will be copied multiple times, not shared.
     * This results in a tree of elements where each [JsonObject] and [JsonArray] is a unique instance.
     *
     * [JsonLiteral] instances are immutable and may be shared between copies.
     */
    fun deepCopy() = this
}

internal val deepCopyFunction = ElementVisitor<JsonElement> { elementStack ->
    val element = elementStack.element
    when (element) {
        is JsonLiteral -> element
        is JsonObject -> JsonObject(element.mapValues { (_, value) -> callRecursive(elementStack.push(value)) })
        is JsonArray -> JsonArray(element.map { callRecursive(elementStack.push(it)) })
    }
}

internal val JsonElement.typeName: String
    get() {
        return when (this) {
            is JsonString -> "JsonString"
            is JsonNumber -> "JsonNumber"
            is JsonBoolean -> "JsonBoolean"
            is JsonNull -> "JsonNull"
            is JsonObject -> "JsonObject"
            is JsonArray -> "JsonArray"
        }
    }
