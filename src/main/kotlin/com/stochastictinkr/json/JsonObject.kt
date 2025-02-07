package com.stochastictinkr.json

import com.stochastictinkr.json.walker.*

/**
 * Represents a JSON object. The object is mutable and can be modified after creation.
 */
@TinkrJsonDsl
class JsonObject private constructor(
    private val content: MutableMap<String, JsonElement>,
    unit: Unit = Unit,
) : JsonElement, MutableMap<String, JsonElement> by content {
    /**
     * Creates an empty [JsonObject].
     */
    constructor() : this(mutableMapOf())

    /**
     * Creates a [JsonObject] from the given key-value pairs.
     */
    constructor(vararg pairs: Pair<String, JsonElement>) : this(mutableMapOf(*pairs))

    /**
     * Creates a [JsonObject] from the given key-value pairs.
     */
    constructor(pairs: Iterable<Pair<String, JsonElement>>) : this(pairs.toMap().toMutableMap())

    /**
     * Creates a [JsonObject] from the given map.
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
    operator fun String.invoke(value: Double?) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to the number, or null if the value is null.
     * Value must be one of the following types: [Short], [Int], [Long], [Float], [Double], or null.
     */
    @TinkrJsonDsl
    operator fun String.invoke(value: Number?) {
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
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
    @TinkrJsonDsl
    operator fun String.invoke(value: JsonElement) {
        set(this, value)
    }

    /**
     * Sets the value of the given key to a string, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull("value")
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: String?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to an integer, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull(123)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Int?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to a long, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull(123L)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Long?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to a float, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull(123.45f)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Float?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to a double, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull(123.45)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Double?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to the number, or removes the key if the value is null.
     * Value must be one of the following types: [Short], [Int], [Long], [Float], [Double], or null.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Number?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to a boolean, or removes the key if the value is null.
     *
     * Example syntax:
     * ```
     * jsonObject {
     *   "key".nonNull(true)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: Boolean?) {
        setNonNull(this, value)
    }

    /**
     * Sets the value of the given key to the element in the [JsonRoot], or removes the key if the value is null.
     *
     * If the value contains JsonNull, it will be added to the object.
     *
     * Example syntax:
     * ```
     * val theJsonRoot: JsonRoot = ...
     * jsonObject {
     *   "key".nonNull(theJsonRoot)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: JsonRoot?) {
        setNonNull(this, value?.jsonElement)
    }

    /**
     * Sets the value of the given key to the given JSON element, or removes the key if the value is null.
     *
     * If the value contains JsonNull, it will be added to the object.
     *
     * Example syntax:
     * ```
     * val theJsonElement: JsonElement? = ...
     * jsonObject {
     *   "key".nonNull(theJsonElement)
     * }
     * ```
     * @receiver The key.
     * @param value The value.
     */
    @TinkrJsonDsl
    fun String.nonNull(value: JsonElement?) {
        setNonNull(this, value)
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
    @TinkrJsonDsl
    inline operator fun String.get(build: JsonArray.() -> Unit) = JsonArray().also {
        it.build()
        set(this, it)
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
    @TinkrJsonDsl
    inline operator fun String.invoke(builder: JsonObject.() -> Unit) = JsonObject().also {
        it.builder()
        set(this, it)
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
    @TinkrJsonDsl
    operator fun String.invoke(value: Nothing?) {
        set(this, JsonNull)
    }

    /**
     * Sets the value of the given key to a string, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: String?) {
        content[key] = value?.let { JsonString(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to an integer, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Int?) {
        content[key] = value?.let { JsonNumber(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to a long, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Long?) {
        content[key] = value?.let { JsonNumber(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to a float, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Float?) {
        content[key] = value?.let { JsonNumber(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to a double, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Double?) {
        content[key] = value?.let { JsonNumber(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to a double, or null if the value is null.
     * Value must be one of the following types: [Short], [Int], [Long], [Float], [Double], or null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Number?) {
        when (value) {
            is Short -> set(key, value.toInt())
            is Int -> set(key, value)
            is Long -> set(key, value)
            is Float -> set(key, value)
            is Double -> set(key, value)
            null -> set(key, JsonNull)
            else -> throw IllegalArgumentException("Value must be one of the following types: Short, Int, Long, Float, Double, or null.")
        }
    }

    /**
     * Sets the value of the given key to a boolean, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Boolean?) {
        content[key] = value?.let { JsonBoolean(it) } ?: JsonNull
    }

    /**
     * Sets the value of the given key to a string, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: String?) {
        if (value != null) {
            content[key] = JsonString(value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to an integer, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Int?) {
        if (value != null) {
            content[key] = JsonNumber(value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to a long, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Long?) {
        if (value != null) {
            content[key] = JsonNumber(value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to a float, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Float?) {
        if (value != null) {
            content[key] = JsonNumber(value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to a double, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Double?) {
        if (value != null) {
            content[key] = JsonNumber(value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to the number, or removes the key if the value is null.
     * Value must be one of the following types: [Short], [Int], [Long], [Float], [Double], or null.
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Number?) {
        if (value != null) {
            set(key, value)
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to a boolean, or removes the key if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: Boolean?) {
        when (value) {
            true -> content[key] = JsonBoolean.True
            false -> content[key] = JsonBoolean.False
            null -> content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to a [JsonElement], or removes the key if the value is `null`.
     * [JsonNull] values *will* be added to the object.
     *
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: JsonElement?) {
        if (value != null) {
            content[key] = value
        } else {
            content.remove(key)
        }
    }

    /**
     * Sets the value of the given key to the [JsonRoot]'s value, or removes the key if the value is `null`.
     * [JsonNull] values *will* be added to the object.
     *
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    fun setNonNull(key: String, value: JsonRoot?) {
        if (value != null) {
            content[key] = value.jsonElement
        } else {
            content.remove(key)
        }
    }


    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllString")
    @TinkrJsonDsl
    fun putAll(map: Map<String, String?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonStringOrNull() }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllInt")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Int?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonNumberOrNull() }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllLong")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Long?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonNumberOrNull() }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllFloat")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Float?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonNumberOrNull() }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllDouble")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Double?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonNumberOrNull() }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllBoolean")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Boolean?>) {
        map.mapValuesTo(this) { (_, value) -> value.toJsonBooleanOrNull() }
    }

    override fun deepCopy(): JsonObject = JsonObject().also {
        content.mapValuesTo(it) { (_, value) -> deepCopyFunction(value) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonObject) return false
        return treeCompare(ElementStack(this) to ElementStack(other))
    }

    override fun hashCode(): Int {
        return treeHash(this)
    }

    override fun toString(): String {
        return "JsonObject({${content.entries.joinToString(", ") { (key, value) -> "\"$key\": ${value.typeName}" }}})"
    }
}

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonString]s. Null values will be converted to [JsonNull].
 */
@JvmName("stringsToJsonObject")
fun Map<String, String?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonStringOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonNumber]s. Null values will be converted to [JsonNull].
 */
@JvmName("intsToJsonObject")
fun Map<String, Int?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumberOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonNumber]s. Null values will be converted to [JsonNull].
 */
@JvmName("longsToJsonObject")
fun Map<String, Long?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumberOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonNumber]s. Null values will be converted to [JsonNull].
 */
@JvmName("floatsToJsonObject")
fun Map<String, Float?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumberOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonNumber]s. Null values will be converted to [JsonNull].
 */
@JvmName("doublesToJsonObject")
fun Map<String, Double?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumberOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The non-null values will be converted to [JsonBoolean]s. Null values will be converted to [JsonNull].
 */
@JvmName("booleansToJsonObject")
fun Map<String, Boolean?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonBooleanOrNull() })

/**
 * Convert this [Map] to a [JsonObject].
 * The values will be used as-is.
 */
@JvmName("mapToJsonObject")
fun Map<String, JsonElement>.toJsonObject() = JsonObject(this)
