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

/**
 * Represents a literal JSON value. A literal value is either a string, number, boolean, or null.
 */
sealed interface JsonLiteral : JsonElement

/**
 * Represents a JSON string literal.
 * @property string The string value.
 */
data class JsonString(override val string: String) : JsonLiteral {
    override val stringOrNull get() = string

    override fun toString() = """JsonString("$string")"""
}

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
 * Represents a JSON null literal.
 */
data object JsonNull : JsonLiteral {
    override val isNull get() = true
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
    @TinkrJsonDsl
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
        content[key] = value?.let(::JsonString) ?: JsonNull
    }

    /**
     * Sets the value of the given key to an integer, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Int?) {
        content[key] = value?.let(::JsonInt) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a long, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Long?) {
        content[key] = value?.let(::JsonLong) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a float, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Float?) {
        content[key] = value?.let(::JsonFloat) ?: JsonNull
    }

    /**
     * Sets the value of the given key to a double, or null if the value is null.
     * @param key The key
     * @param value The value
     */
    @TinkrJsonDsl
    operator fun set(key: String, value: Double?) {
        content[key] = value?.let(::JsonDouble) ?: JsonNull
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
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllString")
    @TinkrJsonDsl
    fun putAll(map: Map<String, String?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllInt")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Int?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllLong")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Long?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllFloat")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Float?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllDouble")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Double?>) {
        map.forEach { (key, value) -> key(value) }
    }

    /**
     * Adds all key-value pairs from the given map to this object, converting the values to JSON elements.
     */
    @JvmName("putAllBoolean")
    @TinkrJsonDsl
    fun putAll(map: Map<String, Boolean?>) {
        map.forEach { (key, value) -> key(value) }
    }

    override fun deepCopy(): JsonObject = deepCopy(this)

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

private val treeHash = ElementVisitor<Int> {
    val element = it.element
    when (element) {
        is JsonLiteral -> element.hashCode()
        is JsonObject -> element.entries.sumOf { (key, value) -> key.hashCode() + callRecursive(it.push(value)) }
        is JsonArray -> {
            var hash = 0
            element.forEach { value ->
                hash = hash * 37 + callRecursive(it.push(value))
            }
            hash
        }
    }
}

private val treeCompare = DeepRecursiveFunction<Pair<ElementStack, ElementStack>, Boolean> {
    val (aStack, bStack) = it
    val a = aStack.element
    val b = bStack.element
    when {
        a === b -> true
        a is JsonLiteral -> a == b
        a is JsonObject -> {
            when {
                b !is JsonObject -> false
                a.size != b.size -> false
                else -> a.keys.all { key ->
                    val aNext = aStack.push(a.getValue(key))
                    val bNext = bStack.push(b.getValue(key))
                    callRecursive(aNext to bNext)
                }
            }
        }

        a is JsonArray -> {
            when {
                b !is JsonArray -> false
                a.size != b.size -> false
                else -> a.zip(b).all { (aElement, bElement) ->
                    val aNext = aStack.push(aElement)
                    val bNext = bStack.push(bElement)
                    callRecursive(aNext to bNext)
                }
            }
        }

        else -> false
    }
}

private val JsonElement.typeName: String
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
        content[index] = value?.let { JsonBoolean(it) } ?: JsonNull
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
    fun add(value: String?) = content.add(value.toJsonStringOrNull())

    /**
     * Adds an integer to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Int?) = content.add(value.toJsonNumberOrNull())

    /**
     * Adds a long to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Long?) = content.add(value.toJsonNumberOrNull())

    /**
     * Adds a float to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Float?) = content.add(value.toJsonNumberOrNull())

    /**
     * Adds a double to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Double?) = content.add(value.toJsonNumberOrNull())

    /**
     * Adds a boolean to the array, or null if the value is null.
     */
    @TinkrJsonDsl
    fun add(value: Boolean?) = content.add(value.toJsonBooleanOrNull())

    /**
     * Adds a null value to the array.
     */
    @TinkrJsonDsl
    fun add(value: Nothing?) = content.add(value.toJsonNull())

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
    fun remove(value: String) = content.remove(JsonString(value))

    /**
     * Removes the first occurrence of the given integer from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Int) = content.remove(JsonInt(value))

    /**
     * Removes the first occurrence of the given long from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Long) = content.remove(JsonLong(value))

    /**
     * Removes the first occurrence of the given float from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Float) = content.remove(JsonFloat(value))

    /**
     * Removes the first occurrence of the given double from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Double) = content.remove(JsonDouble(value))

    /**
     * Removes the first occurrence of the given boolean from the array.
     */
    @TinkrJsonDsl
    fun remove(value: Boolean) = content.remove(JsonBoolean(value))


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
    fun addAll(values: Iterable<String?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllInt")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Int?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllLong")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Long?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllFloat")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Float?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllDouble")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Double?>) = values.forEach(::add)

    /**
     * Adds the contents of the given iterable to this array.
     */
    @JvmName("addAllBoolean")
    @TinkrJsonDsl
    fun addAll(values: Iterable<Boolean?>) = values.forEach(::add)

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
        return content.hashCode()
    }

    override fun toString(): String {
        return "JsonArray(${content.joinToString(", ") { it.typeName }})"
    }

    override fun deepCopy() = deepCopy(this)
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

/**
 * Convert the receiver to either a [JsonString] or [JsonNull].
 */
fun String?.toJsonStringOrNull(): JsonElement = this?.let { JsonString(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Short?.toJsonNumberOrNull(): JsonElement = this?.toInt()?.let { JsonNumber(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Int?.toJsonNumberOrNull(): JsonElement = this?.let { JsonNumber(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Long?.toJsonNumberOrNull(): JsonElement = this?.let { JsonNumber(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Float?.toJsonNumberOrNull(): JsonElement = this?.let { JsonNumber(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonNumber] or [JsonNull].
 */
fun Double?.toJsonNumberOrNull(): JsonElement = this?.let { JsonNumber(it) } ?: JsonNull

/**
 * Convert the receiver to either a [JsonBoolean] or [JsonNull].
 */
fun Boolean?.toJsonBooleanOrNull(): JsonElement = when (this) {
    true -> JsonBoolean.True
    false -> JsonBoolean.False
    null -> JsonNull
}

/**
 * Convert the receiver to a [JsonString].
 */
fun String.toJsonString() = JsonString(this)

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

/**
 * Convert the `null` receiver to a [JsonNull].
 */
fun Nothing?.toJsonNull() = JsonNull


/**
 * Makes a deep copy of this element. Throws an error on circular references.
 * If the same element is encountered multiple times, it will be copied multiple times, not shared. This results in a
 * tree of elements where each [JsonObject] and [JsonArray] is unique.
 */
fun <T : JsonElement> deepCopy(element: T): T = deepCopyFunction(element) as T

/**
 * Makes a deep copy of this element. Throws an error on circular references.
 * If the same element is encountered multiple times, it will be copied multiple times, not shared. This results in a
 * tree of elements where each [JsonObject] and [JsonArray] is unique.
 */
fun deepCopy(root: JsonRoot) = JsonRoot(deepCopy(root.jsonElement))

private val deepCopyFunction = ElementVisitor<JsonElement> { elementStack ->
    val element = elementStack.element
    when (element) {
        is JsonLiteral -> element
        is JsonObject -> JsonObject(element.mapValues { (_, value) -> callRecursive(elementStack.push(value)) })
        is JsonArray -> JsonArray(element.map { callRecursive(elementStack.push(it)) })
    }
}
