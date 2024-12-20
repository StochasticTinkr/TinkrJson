package com.stochastictinkr.json

import com.stochastictinkr.json.walker.*

@TinkrJsonDsl
class JsonArray(
    private val content: MutableList<JsonElement>,
) : JsonElement, MutableList<JsonElement> by content {
    constructor() : this(mutableListOf())
    constructor(vararg elements: JsonElement) : this(elements.toMutableList())
    constructor(elements: Iterable<JsonElement>) : this(elements.toMutableList())

    override val jsonArrayOrNull: JsonArray? get() = this
    override val jsonArray: JsonArray get() = this

    /**
     * Sets the value at the given index to a string, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: String?) {
        content[index] = value.toJsonStringOrNull()
    }

    /**
     * Sets the value at the given index to an integer, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Int?) {
        content[index] = value.toJsonNumberOrNull()
    }

    /**
     * Sets the value at the given index to a long, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Long?) {
        content[index] = value.toJsonNumberOrNull()
    }

    /**
     * Sets the value at the given index to a float, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Float?) {
        content[index] = value.toJsonNumberOrNull()
    }

    /**
     * Sets the value at the given index to a double, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Double?) {
        content[index] = value.toJsonNumberOrNull()
    }

    /**
     * Sets the value at the given index to a boolean, or null if the value is null.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Boolean?) {
        content[index] = value.toJsonBooleanOrNull()
    }

    /**
     * Sets the value at the given index to a null value.
     */
    @TinkrJsonDsl
    operator fun set(index: Int, value: Nothing?) {
        content[index] = value.toJsonNull()
    }

    /**
     * Adds a string to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: String?) = add(value.toJsonStringOrNull())

    /**
     * Adds an integer to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Int?) = add(value.toJsonNumberOrNull())

    /**
     * Adds a long to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Long?) = add(value.toJsonNumberOrNull())

    /**
     * Adds a float to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Float?) = add(value.toJsonNumberOrNull())

    /**
     * Adds a double to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Double?) = add(value.toJsonNumberOrNull())

    /**
     * Adds a boolean to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Boolean?) = add(value.toJsonBooleanOrNull())

    /**
     * Adds a null value to the array.
     */
    @TinkrJsonDsl
    fun add(value: Nothing?) = add(value.toJsonNull())

    /**
     * Adds a new JsonObject to the array, and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun addObject(build: JsonObject.() -> Unit = {}) = add(JsonObject().apply(build))

    /**
     * Adds a new JsonArray to the array, and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun addArray(build: JsonArray.() -> Unit = {}) = add(JsonArray().apply(build))


    /**
     * Removes the first occurrence of the given string from the array.
     */
    @TinkrJsonDsl
    fun remove(value: String) = remove(JsonString(value))

    /**
     * Removes the first occurrence of the given integer from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Int) = remove(JsonNumber(value))

    /**
     * Removes the first occurrence of the given long from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Long) = remove(JsonNumber(value))

    /**
     * Removes the first occurrence of the given float from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Float) = remove(JsonNumber(value))

    /**
     * Removes the first occurrence of the given double from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Double) = remove(JsonNumber(value))

    /**
     * Removes the first occurrence of the given boolean from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Boolean) = remove(JsonBoolean(value))

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: JsonElement) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: String?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: Int?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: Long?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: Float?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: Double?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    @TinkrJsonDsl
    operator fun plusAssign(value: Boolean?) {
        add(value)
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @TinkrJsonDsl
    @JvmName("addAllString")
    fun addAll(values: Iterable<String?>) {
        values.mapTo(content) { it.toJsonStringOrNull() }
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllInt")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Int?>) {
        values.mapTo(content) { it.toJsonNumberOrNull() }
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllLong")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Long?>) {
        values.mapTo(content) { it.toJsonNumberOrNull() }
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllFloat")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Float?>) {
        values.mapTo(content) { it.toJsonNumberOrNull() }
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllDouble")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Double?>) {
        values.mapTo(content) { it.toJsonNumberOrNull() }
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllBoolean")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Boolean?>) {
        values.mapTo(content) { it.toJsonBooleanOrNull() }
    }

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignStrings")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<String?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignInts")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<Int?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignLongs")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<Long?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignFloats")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<Float?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignDoubles")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<Double?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignBooleans")
    @TinkrJsonDsl
    operator fun plusAssign(values: Iterable<Boolean?>) = addAll(values)


    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: String?) = JsonArray(content + value.toJsonStringOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: Int?) = JsonArray(content + value.toJsonNumberOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: Long?) = JsonArray(content + value.toJsonNumberOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: Float?) = JsonArray(content + value.toJsonNumberOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: Double?) = JsonArray(content + value.toJsonNumberOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: Boolean?) = JsonArray(content + value.toJsonBooleanOrNull())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(value: JsonElement) = JsonArray(content + value)

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusStrings")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<String?>) = JsonArray(content + values.map { it.toJsonStringOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusInts")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<Int?>) = JsonArray(content + values.map { it.toJsonNumberOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusLongs")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<Long?>) = JsonArray(content + values.map { it.toJsonNumberOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusFloats")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<Float?>) = JsonArray(content + values.map { it.toJsonNumberOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusDoubles")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<Double?>) = JsonArray(content + values.map { it.toJsonNumberOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusBooleans")
    @TinkrJsonDsl
    operator fun plus(values: Iterable<Boolean?>) = JsonArray(content + values.map { it.toJsonBooleanOrNull() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @TinkrJsonDsl
    operator fun plus(values: Iterable<JsonElement>) = JsonArray(content + values)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonArray) return false
        return content
            .zip(other.content)
            .all { (a, b) -> treeCompare(ElementStack(a) to ElementStack(b)) }
    }

    override fun hashCode(): Int {
        return treeHash(ElementStack(this))
    }

    override fun toString(): String {
        return "JsonArray(${content.joinToString(", ") { it.typeName }})"
    }

    override fun deepCopy() = JsonArray().also {
        content.mapTo(it) { deepCopyFunction(it) }
    }
}

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonString]s. Null elements will be converted to [JsonNull].
 */
@JvmName("stringsToJsonArray")
fun Iterable<String?>.toJsonArray() = JsonArray(map { it.toJsonStringOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("intsToJsonArray")
fun Iterable<Int?>.toJsonArray() = JsonArray(map { it.toJsonNumberOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("longsToJsonArray")
fun Iterable<Long?>.toJsonArray() = JsonArray(map { it.toJsonNumberOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("floatsToJsonArray")
fun Iterable<Float?>.toJsonArray() = JsonArray(map { it.toJsonNumberOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("doublesToJsonArray")
fun Iterable<Double?>.toJsonArray() = JsonArray(map { it.toJsonNumberOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonBoolean]s. Null elements will be converted to [JsonNull].
 */
@JvmName("booleansToJsonArray")
fun Iterable<Boolean?>.toJsonArray() = JsonArray(map { it.toJsonBooleanOrNull() })

/**
 * Convert this [Iterable] to a [JsonArray].
 * The elements will be used as-is.
 */
@JvmName("iterableToJsonArray")
fun Iterable<JsonElement>.toJsonArray() = JsonArray(this)
