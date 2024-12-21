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
    fun add(value: String?) = value.toJsonStringOrNull().also(::add)

    /**
     * Adds an integer to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Int?) = value.toJsonNumberOrNull().also(::add)

    /**
     * Adds a long to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Long?) = value.toJsonNumberOrNull().also(::add)

    /**
     * Adds a float to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Float?) = value.toJsonNumberOrNull().also(::add)

    /**
     * Adds a double to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Double?) = value.toJsonNumberOrNull().also(::add)

    /**
     * Adds a boolean to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Boolean?) = value.toJsonBooleanOrNull().also(::add)

    /**
     * Adds a null value to the array.
     */
    @TinkrJsonDsl
    fun add(value: Nothing?) = JsonNull.also(::add)

    /**
     * Adds a new JsonObject to the array, and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun addObject(build: JsonObject.() -> Unit = {}) =
        jsonObject { build() }.also(::add)

    /**
     * Adds a new JsonArray to the array, and applies the given [build] block to it.
     */
    @TinkrJsonDsl
    inline fun addArray(build: JsonArray.() -> Unit = {}) =
        jsonArray { build() }.also(::add)

    /**
     * Returns true if the array contains the given [String] value.
     */
    operator fun contains(element: String?): Boolean {
        return element.toJsonStringOrNull() in this
    }

    /**
     * Returns true if the array contains the given [Int] value.
     */
    operator fun contains(element: Int?): Boolean {
        return element.toJsonNumberOrNull() in this
    }

    /**
     * Returns true if the array contains the given [Long] value.
     */
    operator fun contains(element: Long?): Boolean {
        return element.toJsonNumberOrNull() in this
    }

    /**
     * Returns true if the array contains the given [Float] value.
     */
    operator fun contains(element: Float?): Boolean {
        return element.toJsonNumberOrNull() in this
    }

    /**
     * Returns true if the array contains the given [Double] value.
     */
    operator fun contains(element: Double?): Boolean {
        return element.toJsonNumberOrNull() in this
    }

    /**
     * Returns true if the array contains the given [Boolean] value.
     */
    operator fun contains(element: Boolean?): Boolean {
        return element.toJsonBooleanOrNull() in this
    }

    /**
     * Returns true if the array contains null.
     */
    operator fun contains(element: Nothing?): Boolean {
        return JsonNull in this
    }

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
fun Iterable<String?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonStringOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("intsToJsonArray")
fun Iterable<Int?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonNumberOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("longsToJsonArray")
fun Iterable<Long?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonNumberOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("floatsToJsonArray")
fun Iterable<Float?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonNumberOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonNumber]s. Null elements will be converted to [JsonNull].
 */
@JvmName("doublesToJsonArray")
fun Iterable<Double?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonNumberOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The non-null elements will be converted to [JsonBoolean]s. Null elements will be converted to [JsonNull].
 */
@JvmName("booleansToJsonArray")
fun Iterable<Boolean?>.toJsonArray() = mapTo(JsonArray()) { it.toJsonBooleanOrNull() }

/**
 * Convert this [Iterable] to a [JsonArray].
 * The elements will be used as-is.
 */
@JvmName("iterableToJsonArray")
fun Iterable<JsonElement>.toJsonArray() = JsonArray(this)

/**
 * Filters the elements of this [Iterable] to only include the [String] elements that match the given [predicate].
 */
inline fun Iterable<JsonElement>.filterStrings(predicate: (String) -> Boolean = { true }) =
    mapNotNull { it.stringOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [Int] elements that match the given [predicate].
 * Note, if the element was not stored as an [Int], it will be excluded from the result. Use [filterNumbers] to include
 * all numbers.
 */
inline fun Iterable<JsonElement>.filterInts(predicate: (Int) -> Boolean = { true }) =
    mapNotNull { it.intOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [Long] elements that match the given [predicate].
 * Note, if the element was not stored as a [Long], it will be excluded from the result. Use [filterNumbers] to include
 * all numbers.
 */
inline fun Iterable<JsonElement>.filterLongs(predicate: (Long) -> Boolean = { true }) =
    mapNotNull { it.longOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [Float] elements that match the given [predicate].
 * Note, if the element was not stored as a [Float], it will be excluded from the result. Use [filterNumbers] to include
 * all numbers.
 */
inline fun Iterable<JsonElement>.filterFloats(predicate: (Float) -> Boolean = { true }) =
    mapNotNull { it.floatOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [Double] elements that match the given [predicate].
 * Note, if the element was not stored as a [Double], it will be excluded from the result. Use [filterNumbers] to include
 */
inline fun Iterable<JsonElement>.filterDoubles(predicate: (Double) -> Boolean = { true }) =
    mapNotNull { it.doubleOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the Number elements that match the given [predicate].
 * If you want to treat the numbers as their specific types, use the [filterNumbers] overload that takes a [JsonNumberTypeSelector].
 */
inline fun Iterable<JsonElement>.filterNumbers(predicate: (Number) -> Boolean = { true }) =
    mapNotNull { it.numberOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the numeric elements that match the given [predicate].
 * The [selector] is used to determine the type of number to return, and the [predicate] is used to filter the numbers.
 * The [selector] can be one of [AsInt], [AsLong], [AsFloat], [AsDouble], or [AsNumber].
 */
inline fun <N : Number> Iterable<JsonElement>.filterNumbers(
    selector: JsonNumberTypeSelector<N>,
    predicate: (N) -> Boolean = { true },
) = mapNotNull { it.jsonNumberOrNull?.let { selector.select(it) }?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [Boolean] elements that match the given [predicate].
 */
inline fun Iterable<JsonElement>.filterBooleans(predicate: (Boolean) -> Boolean = { true }) =
    mapNotNull { it.booleanOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [JsonObject] elements that match the given [predicate].
 */
inline fun Iterable<JsonElement>.filterJsonObjects(predicate: (JsonObject) -> Boolean = { true }) =
    mapNotNull { it.jsonObjectOrNull?.takeIf(predicate) }

/**
 * Filters the elements of this [Iterable] to only include the [JsonArray] elements that match the given [predicate].
 */
inline fun Iterable<JsonElement>.filterJsonArrays(predicate: (JsonArray) -> Boolean = { true }) =
    mapNotNull { it.jsonArrayOrNull?.takeIf(predicate) }
