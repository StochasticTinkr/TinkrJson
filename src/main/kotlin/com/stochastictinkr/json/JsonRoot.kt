package com.stochastictinkr.json

/**
 * A mutable reference to an arbitrary JsonElement.
 */
@TinkrJsonDsl
data class JsonRoot(var jsonElement: JsonElement = JsonNull) {
    /**
     * Sets the value of the [jsonElement] to a [JsonString] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: String) {
        jsonElement = value.toJsonString()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: Int) {
        jsonElement = value.toJsonNumber()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: Long) {
        jsonElement = value.toJsonNumber()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: Float) {
        jsonElement = value.toJsonNumber()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: Double) {
        jsonElement = value.toJsonNumber()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonBoolean] with the given value.
     */
    @TinkrJsonDsl
    fun set(value: Boolean) {
        jsonElement = value.toJsonBoolean()
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonString], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableString")
    @TinkrJsonDsl
    fun set(value: String?) {
        set(value.toJsonStringOrNull())
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableInt")
    @TinkrJsonDsl
    fun set(value: Int?) {
        set(value.toJsonNumberOrNull())
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableLong")
    @TinkrJsonDsl
    fun set(value: Long?) {
        set(value.toJsonNumberOrNull())
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonNumber], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableFloat")
    @TinkrJsonDsl
    fun set(value: Float?) {
        set(value.toJsonNumberOrNull())
    }

    /**
     *  Sets the value of the [jsonElement] to a [JsonNumber], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableDouble")
    @TinkrJsonDsl
    fun set(value: Double?) {
        set(value.toJsonNumberOrNull())
    }

    /**
     * Sets the value of the [jsonElement] to a [JsonBoolean], or [JsonNull] if the value is null.
     */
    @JvmName("setNullableBoolean")
    @TinkrJsonDsl
    fun set(value: Boolean?) {
        set(value.toJsonBooleanOrNull())
    }

    /**
     * Sets the value of the [jsonElement] to [JsonNull].
     */
    @TinkrJsonDsl
    fun setNull() {
        jsonElement = JsonNull
    }

    /**
     * Sets the value of the [jsonElement] to the given [JsonElement].
     */
    @TinkrJsonDsl
    fun set(element: JsonElement) {
        jsonElement = element
    }

    /**
     * Sets the value to a new [JsonArray], and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun makeArray(build: JsonArray.() -> Unit = {}) {
        jsonElement = jsonArray(build)
    }

    /**
     * Sets the value to a new [JsonObject], and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun makeObject(build: JsonObject.() -> Unit = {}) {
        jsonElement = jsonObject(build)
    }

    override fun toString(): String {
        return "JsonRoot($jsonElement)"
    }

    /**
     * Makes a deep copy of this element. Throws an error on circular references.
     *
     * If the same element is encountered multiple times, it will be copied multiple times, not shared.
     * This results in a tree of elements where each [JsonObject] and [JsonArray] is a unique instance.
     *
     * [JsonLiteral] instances are immutable and may be shared between copies.
     */
    fun deepCopy() = JsonRoot(jsonElement.deepCopy())
}