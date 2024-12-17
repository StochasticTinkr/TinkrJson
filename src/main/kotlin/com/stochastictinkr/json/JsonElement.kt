package com.stochastictinkr.json

/**
 * Sealed interface representing a JSON element.
 */
sealed interface JsonElement {
    /**
     * Returns the JSON object if this element is a JSON object, or null otherwise.
     */
    val jsonObjectOrNull: JsonObject? get() = null

    /**
     * Returns the JSON array if this element is a JSON array, or null otherwise.
     */
    val jsonArrayOrNull: JsonArray? get() = null

    /**
     * Returns the JSON string if this element is a JSON string, or null otherwise.
     */
    val jsonStringOrNull: JsonString? get() = null

    /**
     * Returns the JSON number if this element is a JSON number, or null otherwise.
     */
    val jsonNumberOrNull: JsonNumber? get() = null

    /**
     * Returns the JSON boolean if this element is a JSON boolean, or null otherwise.
     */
    val jsonBooleanOrNull: JsonBoolean? get() = null

    /**
     * Returns the JSON object if this element is a JSON object, or throws an error otherwise.
     */
    val jsonObject: JsonObject get() = error("Expected JsonObject, but was $this")

    /**
     * Returns the JSON array if this element is a JSON array, or throws an error otherwise.
     */
    val jsonArray: JsonArray get() = error("Expected JsonArray, but was $this")

    /**
     * Returns the JSON string if this element is a JSON string, or throws an error otherwise.
     */
    val jsonString: JsonString get() = error("Expected JsonString, but was $this")

    /**
     * Returns the JSON number if this element is a JSON number, or throws an error otherwise.
     */
    val jsonNumber: JsonNumber get() = error("Expected JsonNumber, but was $this")

    /**
     * Returns the JSON boolean if this element is a JSON boolean, or throws an error otherwise.
     */
    val jsonBoolean: JsonBoolean get() = error("Expected JsonBoolean, but was $this")

    /**
     * Returns true if this element is null, false otherwise.
     */
    val isNull get() = false
}

/**
 * Represents a literal JSON value. A literal value is either a string, number, boolean, or null.
 */
sealed interface JsonLiteral : JsonElement

/**
 * Represents a JSON string literal.
 * @property value The string value.
 */
data class JsonString(val value: String) : JsonLiteral {
    override val jsonStringOrNull: JsonString get() = this
    override val jsonString: JsonString get() = this
}

/**
 * Represents a JSON number literal.
 */
sealed interface JsonNumber : JsonLiteral {
    /**
     * Returns the value as a [Number].
     */
    fun toNumber(): Number

    /**
     * Returns the value as a [Double], possibly losing precision.
     */
    fun toDouble(): Double

    /**
     * Returns the value as a [Float], possibly losing precision.
     */
    fun toFloat(): Float

    /**
     * Returns the value as an [Int], possibly truncating the value or overflowing.
     */
    fun toInt(): Int

    /**
     * Returns the value as a [Long], possibly truncating the value or overflowing.
     */
    fun toLong(): Long

    /**
     * Returns the value as an [Int], or null if the value cannot be represented as an [Int].
     */
    fun toIntOrNull(): Int?

    /**
     * Returns the value as a [Long], or null if the value cannot be represented as a [Long].
     */
    fun toLongOrNull(): Long?

    /**
     * Returns the value as a [Float], or null if the value cannot be represented as a [Float].
     */
    fun toFloatOrNull(): Float?

    /**
     * Returns the value as a [Double], or null if the value cannot be represented as a [Double].
     */
    fun toDoubleOrNull(): Double?

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

private sealed class AbstractJsonNumber<T : Number> : JsonNumber {
    abstract val value: T
    override val jsonNumberOrNull: JsonNumber get() = this
    override val jsonNumber: JsonNumber get() = this
    override fun toDouble() = value.toDouble()
    override fun toFloat() = value.toFloat()
    override fun toInt() = value.toInt()
    override fun toLong() = value.toLong()

    override fun toIntOrNull(): Int? = null
    override fun toLongOrNull(): Long? = null
    override fun toFloatOrNull(): Float? = null
    override fun toDoubleOrNull(): Double? = null
    override fun toNumber(): Number = value

    override fun toString() = value.toString()
}

private data class JsonInt(override val value: Int) : AbstractJsonNumber<Int>() {
    override fun toIntOrNull() = value
    override fun toLongOrNull() = value.toLong()
}

private data class JsonLong(override val value: Long) : AbstractJsonNumber<Long>() {
    override fun toIntOrNull() = if (value in Int.MIN_VALUE..Int.MAX_VALUE) value.toInt() else null
    override fun toLongOrNull() = value
}

private data class JsonFloat(override val value: Float) : AbstractJsonNumber<Float>() {
    override fun toFloatOrNull() = value
    override fun toDoubleOrNull() = value.toDouble()
}

private data class JsonDouble(override val value: Double) : AbstractJsonNumber<Double>() {
    override fun toDoubleOrNull() = value
}

data class JsonBoolean(val value: Boolean) : JsonLiteral {
    override val jsonBooleanOrNull: JsonBoolean get() = this
    override val jsonBoolean: JsonBoolean get() = this
}

data object JsonNull : JsonLiteral {
    val value: Nothing? = null
    override val isNull get() = true
}

/**
 * A mutable reference to a JSON element.
 */
@TinkrJsonDsl
data class JsonRoot(var jsonElement: JsonElement = JsonNull) {
    /**
     * Sets the value of the JSON element to a string.
     */
    fun set(value: String) {
        jsonElement = JsonString(value)
    }

    /**
     * Sets the value of the JSON element to an integer.
     */
    fun set(value: Int) {
        jsonElement = JsonInt(value)
    }

    /**
     * Sets the value of the JSON element to a long.
     */
    fun set(value: Long) {
        jsonElement = JsonLong(value)
    }

    /**
     * Sets the value of the JSON element to a float.
     */
    fun set(value: Float) {
        jsonElement = JsonFloat(value)
    }

    /**
     * Sets the value of the JSON element to a double.
     */
    fun set(value: Double) {
        jsonElement = JsonDouble(value)
    }

    /**
     * Sets the value of the JSON element to a boolean.
     */
    fun set(value: Boolean) {
        jsonElement = JsonBoolean(value)
    }

    /**
     * Sets the value of the JSON element to a string, or null if the value is null.
     */
    @JvmName("setNullableString")
    fun set(value: String?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to an integer, or null if the value is null.
     */
    @JvmName("setNullableInt")
    fun set(value: Int?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to a long, or null if the value is null.
     */
    @JvmName("setNullableLong")
    fun set(value: Long?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to a float, or null if the value is null.
     */
    @JvmName("setNullableFloat")
    fun set(value: Float?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to a double, or null if the value is null.
     */
    @JvmName("setNullableDouble")
    fun set(value: Double?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to a boolean, or null if the value is null.
     */
    @JvmName("setNullableBoolean")
    fun set(value: Boolean?) {
        value?.let(::set) ?: setNull()
    }

    /**
     * Sets the value of the JSON element to null.
     */
    fun setNull() {
        set(JsonNull)
    }

    /**
     * Sets the value to the given JSON element.
     */
    fun set(element: JsonElement) {
        jsonElement = element
    }

    /**
     * Sets the value to a JSON array.
     */
    inline fun jsonArray(build: JsonArray.() -> Unit = {}) {
        jsonElement = JsonArray().apply(build)
    }

    /**
     * Sets the value to a JSON object.
     */
    inline fun jsonObject(build: JsonObject.() -> Unit = {}) {
        jsonElement = JsonObject()
    }
}

/**
 * Represents a JSON object. The object is mutable and can be modified after creation.
 */
@TinkrJsonDsl
class JsonObject private constructor(
    private val content: MutableMap<String, JsonElement>,
    unit: Unit = Unit,
) : JsonElement, MutableMap<String, JsonElement> by content {
    /**
     * Creates an empty JSON object.
     */
    constructor() : this(mutableMapOf())

    /**
     * Creates a JSON object from the given key-value pairs.
     */
    constructor(vararg pairs: Pair<String, JsonElement>) : this(mutableMapOf(*pairs))

    /**
     * Creates a JSON object from the given key-value pairs.
     */
    constructor(pairs: Iterable<Pair<String, JsonElement>>) : this(pairs.toMap().toMutableMap())

    /**
     * Creates a JSON object from the given map. The map is shallow-copied.
     */
    constructor(map: Map<String, JsonElement>) : this(map.toMutableMap())

    override val jsonObject: JsonObject get() = this
    override val jsonObjectOrNull: JsonObject? get() = this

    /**
     * Sets the value of the given key to a string, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"("value")
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: String?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to an integer, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(123)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: Int?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a long, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(123L)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: Long?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a float, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(123.45f)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: Float?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a double, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(123.45)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: Double?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a boolean, or null if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(true)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: Boolean?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to the element in the [JsonRoot].
     *
     * Example syntax:
     * ```
     * val theJsonRoot: JsonRoot = ...
     * jsonObject {
     *   "key"(theJsonRoot)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: JsonRoot) {
        set(this, value.jsonElement)
    }

    /**
     * Sets the value of the given key to the given JSON element.
     *
     * Example syntax:
     * ```
     * val theJsonElement: JsonElement = ...
     * jsonObject {
     *   "key"(theJsonElement)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    operator fun String.invoke(value: JsonElement) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a new JsonArray, and applies the given [build] block to it.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *    "key"[{
     *      add("element")
     *      add(123)
     *    }]
     * }
     * ```
     *
     * @receiver The key.
     * @param build The block to apply to the new JsonArray.
     */
    inline operator fun String.get(build: JsonArray.() -> Unit) {
        set(this, JsonArray().apply(build))
    }

    /**
     * Sets the value of the given key to a new JsonObject, and applies the given [builder] block to it.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *    "key" {
     *      "nestedKey"("nestedValue")
     *    }
     * }
     * ```
     *
     * @receiver The key.
     * @param builder The block to apply to the new JsonObject.
     */
    inline operator fun String.invoke(builder: JsonObject.() -> Unit) {
        set(this, JsonObject().apply(builder))
    }

    /**
     * Sets the value of the given key to a null value. This is a convenience method for when you don't have a
     * typed value to set, but you want to map the key to null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key"(null)
     * }
     * ```
     *
     * @receiver The key.
     * @param value null.
     */
    operator fun String.invoke(value: Nothing?) {
        set(this, JsonNull)
    }

    /**
     * Sets the value of the given key to a string, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: String?) {
        content[key] = value?.let(::JsonString) ?: JsonNull
    }

    /**
     * Sets the value of the given key to an integer, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: Int?) {
        content[key] = value?.let(::JsonInt) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a long, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: Long?) {
        content[key] = value?.let(::JsonLong) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a float, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: Float?) {
        content[key] = value?.let(::JsonFloat) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a double, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: Double?) {
        content[key] = value?.let(::JsonDouble) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a boolean, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    operator fun set(key: String, value: Boolean?) {
        content[key] = value?.let(::JsonBoolean) ?: JsonNull
    }


    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllString")
    fun putAll(map: Map<String, String?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllInt")
    fun putAll(map: Map<String, Int?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllLong")
    fun putAll(map: Map<String, Long?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllFloat")
    fun putAll(map: Map<String, Float?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllDouble")
    fun putAll(map: Map<String, Double?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllBoolean")
    fun putAll(map: Map<String, Boolean?>) {
        map.forEach { (key, value) -> key(value) }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonObject) return false
        if (content != other.content) return false
        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }
}

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
    operator fun set(index: Int, value: String?) {
        content[index] = value?.let(::JsonString) ?: JsonNull
    }

    /**
     * Sets the value at the given index to an integer, or null if the value is null.
     */
    operator fun set(index: Int, value: Int?) {
        content[index] = value?.let(::JsonInt) ?: JsonNull
    }

    /**
     * Sets the value at the given index to a long, or null if the value is null.
     */
    operator fun set(index: Int, value: Long?) {
        content[index] = value?.let(::JsonLong) ?: JsonNull
    }

    /**
     * Sets the value at the given index to a float, or null if the value is null.
     */
    operator fun set(index: Int, value: Float?) {
        content[index] = value?.let(::JsonFloat) ?: JsonNull
    }

    /**
     * Sets the value at the given index to a double, or null if the value is null.
     */
    operator fun set(index: Int, value: Double?) {
        content[index] = value?.let(::JsonDouble) ?: JsonNull
    }

    /**
     * Sets the value at the given index to a boolean, or null if the value is null.
     */
    operator fun set(index: Int, value: Boolean?) {
        content[index] = value?.let(::JsonBoolean) ?: JsonNull
    }

    /**
     * Adds a string to the array, or null if the value is null.
     */
    fun add(value: String?) = content.add(value?.let(::JsonString) ?: JsonNull)

    /**
     * Adds an integer to the array, or null if the value is null.
     */
    fun add(value: Int?) = content.add(value?.let(::JsonInt) ?: JsonNull)

    /**
     * Adds a long to the array, or null if the value is null.
     */
    fun add(value: Long?) = content.add(value?.let(::JsonLong) ?: JsonNull)

    /**
     * Adds a float to the array, or null if the value is null.
     */
    fun add(value: Float?) = content.add(value?.let(::JsonFloat) ?: JsonNull)

    /**
     * Adds a double to the array, or null if the value is null.
     */
    fun add(value: Double?) = content.add(value?.let(::JsonDouble) ?: JsonNull)

    /**
     * Adds a boolean to the array, or null if the value is null.
     */
    fun add(value: Boolean?) = content.add(value?.let(::JsonBoolean) ?: JsonNull)

    /**
     * Adds a new JsonObject to the array, and applies the given [build] block to it.
     */
    inline fun addObject(build: JsonObject.() -> Unit = {}) = add(JsonObject().apply(build))

    /**
     * Adds a new JsonArray to the array, and applies the given [build] block to it.
     */
    inline fun addArray(build: JsonArray.() -> Unit = {}) = add(JsonArray().apply(build))


    /**
     * Removes the first occurrence of the given string from the array.
     */
    fun remove(value: String) = content.remove(JsonString(value))

    /**
     * Removes the first occurrence of the given integer from the array.
     */
    fun remove(value: Int) = content.remove(JsonInt(value))

    /**
     * Removes the first occurrence of the given long from the array.
     */
    fun remove(value: Long) = content.remove(JsonLong(value))

    /**
     * Removes the first occurrence of the given float from the array.
     */
    fun remove(value: Float) = content.remove(JsonFloat(value))

    /**
     * Removes the first occurrence of the given double from the array.
     */
    fun remove(value: Double) = content.remove(JsonDouble(value))

    /**
     * Removes the first occurrence of the given boolean from the array.
     */
    fun remove(value: Boolean) = content.remove(JsonBoolean(value))


    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: JsonElement) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: String?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: Int?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: Long?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: Float?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: Double?) {
        add(value)
    }

    /**
     * Convenience method for [add].
     */
    operator fun plusAssign(value: Boolean?) {
        add(value)
    }

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllString")
    fun addAll(values: Iterable<String?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllInt")
    fun addAll(values: Iterable<Int?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllLong")
    fun addAll(values: Iterable<Long?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllFloat")
    fun addAll(values: Iterable<Float?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllDouble")
    fun addAll(values: Iterable<Double?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllBoolean")
    fun addAll(values: Iterable<Boolean?>) = values.forEach(::add)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignStrings")
    operator fun plusAssign(values: Iterable<String?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignInts")
    operator fun plusAssign(values: Iterable<Int?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignLongs")
    operator fun plusAssign(values: Iterable<Long?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignFloats")
    operator fun plusAssign(values: Iterable<Float?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignDoubles")
    operator fun plusAssign(values: Iterable<Double?>) = addAll(values)

    /**
     * Convenience method for [addAll].
     */
    @JvmName("plusAssignBooleans")
    operator fun plusAssign(values: Iterable<Boolean?>) = addAll(values)


    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: String?) = JsonArray(content + value.toJsonString())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: Int?) = JsonArray(content + value.toJsonNumber())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: Long?) = JsonArray(content + value.toJsonNumber())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: Float?) = JsonArray(content + value.toJsonNumber())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: Double?) = JsonArray(content + value.toJsonNumber())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: Boolean?) = JsonArray(content + value.toJsonBoolean())

    /**
     * Creates a shallow copy of this array, with the given element added to the end.
     */
    operator fun plus(value: JsonElement) = JsonArray(content + value)


    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusStrings")
    operator fun plus(values: Iterable<String?>) = JsonArray(content + values.map { it.toJsonString() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusInts")
    operator fun plus(values: Iterable<Int?>) = JsonArray(content + values.map { it.toJsonNumber() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusLongs")
    operator fun plus(values: Iterable<Long?>) = JsonArray(content + values.map { it.toJsonNumber() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusFloats")
    operator fun plus(values: Iterable<Float?>) = JsonArray(content + values.map { it.toJsonNumber() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusDoubles")
    operator fun plus(values: Iterable<Double?>) = JsonArray(content + values.map { it.toJsonNumber() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    @JvmName("plusBooleans")
    operator fun plus(values: Iterable<Boolean?>) = JsonArray(content + values.map { it.toJsonBoolean() })

    /**
     * Creates a shallow copy of this array, with the given elements added to the end.
     */
    operator fun plus(values: Iterable<JsonElement>) = JsonArray(content + values)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonArray) return false
        if (content != other.content) return false
        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String {
        return content.toString()
    }
}

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("stringsToJsonArray")
fun Iterable<String?>.toJsonArray() = JsonArray(map { it.toJsonString() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("intsToJsonArray")
fun Iterable<Int?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("longsToJsonArray")
fun Iterable<Long?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("floatsToJsonArray")
fun Iterable<Float?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("doublesToJsonArray")
fun Iterable<Double?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("booleansToJsonArray")
fun Iterable<Boolean?>.toJsonArray() = JsonArray(map { it.toJsonBoolean() })

/**
 * Convert this [Iterable] to a [JsonArray].
 */
@JvmName("iterableToJsonArray")
fun Iterable<JsonElement>.toJsonArray() = JsonArray(this)

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("stringsToJsonObject")
fun Map<String, String?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonString() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("intsToJsonObject")
fun Map<String, Int?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("longsToJsonObject")
fun Map<String, Long?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("floatsToJsonObject")
fun Map<String, Float?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("doublesToJsonObject")
fun Map<String, Double?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("booleansToJsonObject")
fun Map<String, Boolean?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonBoolean() })

/**
 * Convert this [Map] to a [JsonObject].
 */
@JvmName("mapToJsonObject")
fun Map<String, JsonElement>.toJsonObject() = JsonObject(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonString] or [JsonNull].
 */
fun String?.toJsonString(): JsonElement = this?.let(::JsonString) ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonNumber] or [JsonNull].
 */
fun Short?.toJsonNumber(): JsonElement = this?.toInt()?.let { JsonInt(it) } ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonNumber] or [JsonNull].
 */
fun Int?.toJsonNumber(): JsonElement = this?.let { JsonInt(it) } ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonNumber] or [JsonNull].
 */
fun Long?.toJsonNumber(): JsonElement = this?.let { JsonLong(it) } ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonNumber] or [JsonNull].
 */
fun Float?.toJsonNumber(): JsonElement = this?.let { JsonFloat(it) } ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonNumber] or [JsonNull].
 */
fun Double?.toJsonNumber(): JsonElement = this?.let { JsonDouble(it) } ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return Either a [JsonBoolean] or [JsonNull].
 */
fun Boolean?.toJsonBoolean(): JsonElement = this?.let(::JsonBoolean) ?: JsonNull

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonString].
 */
fun String.toJsonString() = JsonString(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonNumber].
 */
fun Short.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonNumber].
 */
fun Int.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonNumber].
 */
fun Long.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonNumber].
 */
fun Float.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonNumber].
 */
fun Double.toJsonNumber() = JsonNumber(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonBoolean].
 */
fun Boolean.toJsonBoolean() = JsonBoolean(this)

/**
 * Convert the receiver to a [JsonElement].
 * @return [JsonString].
 */
fun Nothing?.toJsonNull(): JsonElement = this ?: JsonNull
