package com.stochastictinkr.json

sealed interface JsonElement {
    val jsonObjectOrNull: JsonObject? get() = null
    val jsonArrayOrNull: JsonArray? get() = null
    val jsonStringOrNull: JsonString? get() = null
    val jsonNumberOrNull: JsonNumber? get() = null
    val jsonBooleanOrNull: JsonBoolean? get() = null
    val jsonObject: JsonObject get() = error("Expected JsonObject, but was $this")
    val jsonArray: JsonArray get() = error("Expected JsonArray, but was $this")
    val jsonString: JsonString get() = error("Expected JsonString, but was $this")
    val jsonNumber: JsonNumber get() = error("Expected JsonNumber, but was $this")
    val jsonBoolean: JsonBoolean get() = error("Expected JsonBoolean, but was $this")
    val isNull get() = false
}

sealed interface JsonLiteral : JsonElement

data class JsonString(val value: String) : JsonLiteral {
    override val jsonStringOrNull: JsonString get() = this
    override val jsonString: JsonString get() = this
}

sealed interface JsonNumber : JsonLiteral {
    fun toNumber(): Number
    fun toDouble(): Double
    fun toFloat(): Float
    fun toInt(): Int
    fun toLong(): Long

    fun toIntOrNull(): Int?
    fun toLongOrNull(): Long?
    fun toFloatOrNull(): Float?
    fun toDoubleOrNull(): Double?

    companion object {
        operator fun invoke(value: Short): JsonNumber = JsonInt(value.toInt())
        operator fun invoke(value: Int): JsonNumber = JsonInt(value)
        operator fun invoke(value: Long): JsonNumber = JsonLong(value)
        operator fun invoke(value: Float): JsonNumber = JsonFloat(value)
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

@KsonDsl
data class JsonRoot(var jsonElement: JsonElement = JsonNull) {
    fun set(value: String) {
        jsonElement = JsonString(value)
    }

    fun set(value: Int) {
        jsonElement = JsonInt(value)
    }

    fun set(value: Long) {
        jsonElement = JsonLong(value)
    }

    fun set(value: Float) {
        jsonElement = JsonFloat(value)
    }

    fun set(value: Double) {
        jsonElement = JsonDouble(value)
    }

    fun set(value: Boolean) {
        jsonElement = JsonBoolean(value)
    }

    @JvmName("setNullableString")
    fun set(value: String?) {
        value?.let(::set) ?: setNull()
    }

    @JvmName("setNullableInt")
    fun set(value: Int?) {
        value?.let(::set) ?: setNull()
    }

    @JvmName("setNullableLong")
    fun set(value: Long?) {
        value?.let(::set) ?: setNull()
    }

    @JvmName("setNullableFloat")
    fun set(value: Float?) {
        value?.let(::set) ?: setNull()
    }

    @JvmName("setNullableDouble")
    fun set(value: Double?) {
        value?.let(::set) ?: setNull()
    }

    @JvmName("setNullableBoolean")
    fun set(value: Boolean?) {
        value?.let(::set) ?: setNull()
    }

    fun setNull() {
        set(JsonNull)
    }

    fun set(element: JsonElement) {
        jsonElement = element
    }

    fun jsonArray() {
        jsonElement = JsonArray()
    }

    fun jsonObject() {
        jsonElement = JsonObject()
    }
}

@KsonDsl
class JsonObject private constructor(
    private val content: MutableMap<String, JsonElement>,
    unit: Unit = Unit,
) : JsonElement, MutableMap<String, JsonElement> by content {
    constructor() : this(mutableMapOf())
    constructor(vararg pairs: Pair<String, JsonElement>) : this(pairs.toMap().toMutableMap())
    constructor(pairs: Iterable<Pair<String, JsonElement>>) : this(pairs.toMap().toMutableMap())
    constructor(map: Map<String, JsonElement>) : this(map.toMutableMap())

    override val jsonObject: JsonObject get() = this
    override val jsonObjectOrNull: JsonObject? get() = this

    operator fun String.invoke(value: String?) {
        set(this, value)
    }

    operator fun String.invoke(value: Int?) {
        set(this, value)
    }

    operator fun String.invoke(value: Long?) {
        set(this, value)
    }

    operator fun String.invoke(value: Float?) {
        set(this, value)
    }

    operator fun String.invoke(value: Double?) {
        set(this, value)
    }

    operator fun String.invoke(value: Boolean?) {
        set(this, value)
    }

    operator fun String.invoke(value: JsonRoot) {
        set(this, value.jsonElement)
    }

    operator fun String.invoke(value: JsonElement) {
        set(this, value)
    }

    operator fun String.invoke(value: Nothing?) {
        set(this, JsonNull)
    }

    operator fun set(key: String, value: String?) {
        content[key] = value?.let(::JsonString) ?: JsonNull
    }

    operator fun set(key: String, value: Int?) {
        content[key] = value?.let(::JsonInt) ?: JsonNull
    }

    operator fun set(key: String, value: Long?) {
        content[key] = value?.let(::JsonLong) ?: JsonNull
    }

    operator fun set(key: String, value: Float?) {
        content[key] = value?.let(::JsonFloat) ?: JsonNull
    }

    operator fun set(key: String, value: Double?) {
        content[key] = value?.let(::JsonDouble) ?: JsonNull
    }

    operator fun set(key: String, value: Boolean?) {
        content[key] = value?.let(::JsonBoolean) ?: JsonNull
    }

    operator fun String.get(build: JsonArray.() -> Unit) {
        set(this, JsonArray().apply(build))
    }

    // `putAll` methods
    @JvmName("putAllString")
    fun putAll(map: Map<String, String?>) {
        map.forEach { (key, value) -> key(value) }
    }

    @JvmName("putAllInt")
    fun putAll(map: Map<String, Int?>) {
        map.forEach { (key, value) -> key(value) }
    }

    @JvmName("putAllLong")
    fun putAll(map: Map<String, Long?>) {
        map.forEach { (key, value) -> key(value) }
    }

    @JvmName("putAllFloat")
    fun putAll(map: Map<String, Float?>) {
        map.forEach { (key, value) -> key(value) }
    }

    @JvmName("putAllDouble")
    fun putAll(map: Map<String, Double?>) {
        map.forEach { (key, value) -> key(value) }
    }

    @JvmName("putAllBoolean")
    fun putAll(map: Map<String, Boolean?>) {
        map.forEach { (key, value) -> key(value) }
    }

    inline operator fun String.invoke(builder: JsonObject.() -> Unit) {
        set(this, JsonObject().apply(builder))
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

    operator fun set(index: Int, value: String?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonString) ?: JsonNull
    }

    operator fun set(index: Int, value: Int?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonInt) ?: JsonNull
    }

    operator fun set(index: Int, value: Long?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonLong) ?: JsonNull
    }

    operator fun set(index: Int, value: Float?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonFloat) ?: JsonNull
    }

    operator fun set(index: Int, value: Double?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonDouble) ?: JsonNull
    }

    operator fun set(index: Int, value: Boolean?) {
        ensureCapacity(index)
        content[index] = value?.let(::JsonBoolean) ?: JsonNull
    }

    // `add` methods for supported types
    fun add(value: String?) = content.add(value?.let(::JsonString) ?: JsonNull)
    fun add(value: Int?) = content.add(value?.let(::JsonInt) ?: JsonNull)
    fun add(value: Long?) = content.add(value?.let(::JsonLong) ?: JsonNull)
    fun add(value: Float?) = content.add(value?.let(::JsonFloat) ?: JsonNull)
    fun add(value: Double?) = content.add(value?.let(::JsonDouble) ?: JsonNull)
    fun add(value: Boolean?) = content.add(value?.let(::JsonBoolean) ?: JsonNull)

    inline fun addObject(value: JsonObject.() -> Unit) = add(JsonObject().apply(value))
    inline fun addArray(value: JsonArray.() -> Unit) = add(JsonArray().apply(value))

    // `remove` methods for supported types
    fun remove(value: String) = content.remove(JsonString(value))
    fun remove(value: Int) = content.remove(JsonInt(value))
    fun remove(value: Long) = content.remove(JsonLong(value))
    fun remove(value: Float) = content.remove(JsonFloat(value))
    fun remove(value: Double) = content.remove(JsonDouble(value))
    fun remove(value: Boolean) = content.remove(JsonBoolean(value))

    operator fun plusAssign(value: JsonElement) {
        add(value)
    }

    operator fun plusAssign(value: String?) {
        add(value)
    }

    operator fun plusAssign(value: Int?) {
        add(value)
    }

    operator fun plusAssign(value: Long?) {
        add(value)
    }

    operator fun plusAssign(value: Float?) {
        add(value)
    }

    operator fun plusAssign(value: Double?) {
        add(value)
    }

    operator fun plusAssign(value: Boolean?) {
        add(value)
    }

    @JvmName("addAllString")
    fun addAll(values: Iterable<String?>) = values.forEach(::add)

    @JvmName("addAllInt")
    fun addAll(values: Iterable<Int?>) = values.forEach(::add)

    @JvmName("addAllLong")
    fun addAll(values: Iterable<Long?>) = values.forEach(::add)

    @JvmName("addAllFloat")
    fun addAll(values: Iterable<Float?>) = values.forEach(::add)

    @JvmName("addAllDouble")
    fun addAll(values: Iterable<Double?>) = values.forEach(::add)

    @JvmName("addAllBoolean")
    fun addAll(values: Iterable<Boolean?>) = values.forEach(::add)


    @JvmName("plusAssignStrings")
    operator fun plusAssign(values: Iterable<String?>) = addAll(values)

    @JvmName("plusAssignInts")
    operator fun plusAssign(values: Iterable<Int?>) = addAll(values)

    @JvmName("plusAssignLongs")
    operator fun plusAssign(values: Iterable<Long?>) = addAll(values)

    @JvmName("plusAssignFloats")
    operator fun plusAssign(values: Iterable<Float?>) = addAll(values)

    @JvmName("plusAssignDoubles")
    operator fun plusAssign(values: Iterable<Double?>) = addAll(values)

    @JvmName("plusAssignBooleans")
    operator fun plusAssign(values: Iterable<Boolean?>) = addAll(values)


    operator fun plus(value: String?) = JsonArray(content + value.toJsonString())
    operator fun plus(value: Int?) = JsonArray(content + value.toJsonNumber())
    operator fun plus(value: Long?) = JsonArray(content + value.toJsonNumber())
    operator fun plus(value: Float?) = JsonArray(content + value.toJsonNumber())
    operator fun plus(value: Double?) = JsonArray(content + value.toJsonNumber())
    operator fun plus(value: Boolean?) = JsonArray(content + value.toJsonBoolean())
    operator fun plus(value: JsonElement) = JsonArray(content + value)


    @JvmName("plusStrings")
    operator fun plus(values: Iterable<String?>) = JsonArray(content + values.map { it.toJsonString() })

    @JvmName("plusInts")
    operator fun plus(values: Iterable<Int?>) = JsonArray(content + values.map { it.toJsonNumber() })

    @JvmName("plusLongs")
    operator fun plus(values: Iterable<Long?>) = JsonArray(content + values.map { it.toJsonNumber() })

    @JvmName("plusFloats")
    operator fun plus(values: Iterable<Float?>) = JsonArray(content + values.map { it.toJsonNumber() })

    @JvmName("plusDoubles")
    operator fun plus(values: Iterable<Double?>) = JsonArray(content + values.map { it.toJsonNumber() })

    @JvmName("plusBooleans")
    operator fun plus(values: Iterable<Boolean?>) = JsonArray(content + values.map { it.toJsonBoolean() })

    operator fun plus(values: Iterable<JsonElement>) = JsonArray(content + values)

    private fun ensureCapacity(index: Int) {
        while (content.size <= index) {
            content.add(JsonNull)
        }
    }

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

@JvmName("stringsToJsonArray")
fun Iterable<String?>.toJsonArray() = JsonArray(map { it.toJsonString() })

@JvmName("intsToJsonArray")
fun Iterable<Int?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

@JvmName("longsToJsonArray")
fun Iterable<Long?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

@JvmName("floatsToJsonArray")
fun Iterable<Float?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

@JvmName("doublesToJsonArray")
fun Iterable<Double?>.toJsonArray() = JsonArray(map { it.toJsonNumber() })

@JvmName("booleansToJsonArray")
fun Iterable<Boolean?>.toJsonArray() = JsonArray(map { it.toJsonBoolean() })

@JvmName("iterableToJsonArray")
fun Iterable<JsonElement>.toJsonArray() = JsonArray(this)

@JvmName("stringsToJsonObject")
fun Map<String, String?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonString() })

@JvmName("intsToJsonObject")
fun Map<String, Int?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

@JvmName("longsToJsonObject")
fun Map<String, Long?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

@JvmName("floatsToJsonObject")
fun Map<String, Float?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

@JvmName("doublesToJsonObject")
fun Map<String, Double?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonNumber() })

@JvmName("booleansToJsonObject")
fun Map<String, Boolean?>.toJsonObject() = JsonObject(mapValues { (_, value) -> value.toJsonBoolean() })

@JvmName("mapToJsonObject")
fun Map<String, JsonElement>.toJsonObject() = JsonObject(this)

fun String?.toJsonString(): JsonElement = this?.let(::JsonString) ?: JsonNull
fun Short?.toJsonNumber(): JsonElement = this?.toInt()?.let { JsonInt(it) } ?: JsonNull
fun Int?.toJsonNumber(): JsonElement = this?.let { JsonInt(it) } ?: JsonNull
fun Long?.toJsonNumber(): JsonElement = this?.let { JsonLong(it) } ?: JsonNull
fun Float?.toJsonNumber(): JsonElement = this?.let { JsonFloat(it) } ?: JsonNull
fun Double?.toJsonNumber(): JsonElement = this?.let { JsonDouble(it) } ?: JsonNull
fun Boolean?.toJsonBoolean(): JsonElement = this?.let(::JsonBoolean) ?: JsonNull

fun String.toJsonString() = JsonString(this)
fun Short.toJsonNumber() = JsonNumber(this)
fun Int.toJsonNumber() = JsonNumber(this)
fun Long.toJsonNumber() = JsonNumber(this)
fun Float.toJsonNumber() = JsonNumber(this)
fun Double.toJsonNumber() = JsonNumber(this)
fun Boolean.toJsonBoolean() = JsonBoolean(this)

fun Nothing?.toJsonNull(): JsonElement = this ?: JsonNull
