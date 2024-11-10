package com.stochastictinkr.json

import kotlinx.serialization.json.boolean as kxBoolean
import kotlinx.serialization.json.booleanOrNull as kxBooleanOrNull
import kotlinx.serialization.json.contentOrNull as kxContentOrNull
import kotlinx.serialization.json.double as kxDouble
import kotlinx.serialization.json.doubleOrNull as kxDoubleOrNull
import kotlinx.serialization.json.float as kxFloat
import kotlinx.serialization.json.floatOrNull as kxFloatOrNull
import kotlinx.serialization.json.int as kxInt
import kotlinx.serialization.json.intOrNull as kxIntOrNull
import kotlinx.serialization.json.jsonArray as kxJsonArray
import kotlinx.serialization.json.jsonNull as kxJsonNull
import kotlinx.serialization.json.jsonObject as kxJsonObject
import kotlinx.serialization.json.jsonPrimitive as kxJsonPrimitive
import kotlinx.serialization.json.long as kxLong
import kotlinx.serialization.json.longOrNull as kxLongOrNull

typealias JsonElement = kotlinx.serialization.json.JsonElement
typealias JsonObject = kotlinx.serialization.json.JsonObject
typealias JsonArray = kotlinx.serialization.json.JsonArray
typealias JsonPrimitive = kotlinx.serialization.json.JsonPrimitive
typealias JsonNull = kotlinx.serialization.json.JsonNull

fun JsonPrimitive(value: String?) = kotlinx.serialization.json.JsonPrimitive(value)

fun JsonPrimitive(value: Number?) = kotlinx.serialization.json.JsonPrimitive(value)

fun JsonPrimitive(value: Boolean?) = kotlinx.serialization.json.JsonPrimitive(value)

val JsonElement.jsonObject: JsonObject get() = kxJsonObject
val JsonElement.jsonArray: JsonArray get() = kxJsonArray
val JsonElement.jsonPrimitive: JsonPrimitive get() = kxJsonPrimitive
val JsonElement.jsonNull: JsonNull get() = kxJsonNull

val JsonPrimitive.contentOrNull: String? get() = kxContentOrNull
val JsonPrimitive.booleanOrNull: Boolean? get() = kxBooleanOrNull
val JsonPrimitive.doubleOrNull: Double? get() = kxDoubleOrNull
val JsonPrimitive.floatOrNull: Float? get() = kxFloatOrNull
val JsonPrimitive.longOrNull: Long? get() = kxLongOrNull
val JsonPrimitive.intOrNull: Int? get() = kxIntOrNull

val JsonPrimitive.int get() = kxInt
val JsonPrimitive.long get() = kxLong
val JsonPrimitive.float get() = kxFloat
val JsonPrimitive.double get() = kxDouble
val JsonPrimitive.boolean get() = kxBoolean

@DslMarker
annotation class KsonDsl

@KsonDsl
class Kson {
    private val map: MutableMap<String, JsonElement> = mutableMapOf()

    @KsonDsl
    operator fun String.divAssign(value: JsonElement) {
        set(this, value)
    }

    @KsonDsl
    operator fun set(
        key: String,
        value: JsonElement,
    ) {
        map[key] = value
    }

    @KsonDsl
    operator fun set(
        key: String,
        value: String?,
    ) {
        map[key] = JsonPrimitive(value)
    }

    @KsonDsl
    operator fun set(
        key: String,
        value: Number?,
    ) {
        map[key] = JsonPrimitive(value)
    }

    @KsonDsl
    operator fun set(
        key: String,
        value: Boolean?,
    ) {
        map[key] = JsonPrimitive(value)
    }

    @KsonDsl
    fun putAll(map: Map<String, JsonElement>) {
        this.map.putAll(map)
    }

    inline fun <V> putAll(
        map: Map<out CharSequence, V>,
        transform: JsonElementFactory.(V) -> JsonElement,
    ) {
        map.forEach { (key, value) ->
            JsonElementFactory.transform(value).field(key.toString())
        }
    }

    @KsonDsl
    fun remove(key: String) {
        map.remove(key)
    }

    @KsonDsl
    operator fun String.divAssign(value: String?) {
        this /= JsonPrimitive(value)
    }

    @KsonDsl
    operator fun String.divAssign(value: Number?) {
        this /= JsonPrimitive(value)
    }

    @KsonDsl
    operator fun String.divAssign(value: Boolean?) {
        this /= JsonPrimitive(value)
    }

    @KsonDsl
    fun Boolean?.field(field: String) {
        field /= this
    }

    @KsonDsl
    fun String?.field(field: String) {
        field /= this
    }

    @KsonDsl
    fun Number?.field(field: String) {
        field /= this
    }

    @KsonDsl
    fun JsonElement?.field(field: String) {
        field /= this ?: JsonNull
    }

    @KsonDsl
    inline operator fun String.invoke(block: Kson.() -> Unit) {
        this /= kson(block)
    }

    @KsonDsl
    inline fun obj(
        field: String,
        block: Kson.() -> Unit,
    ) = kson(block).field(field)

    @KsonDsl
    inline fun array(
        field: String,
        block: KsonArray.() -> Unit,
    ) = ksonArray(block).field(field)

    @PublishedApi
    internal fun toJsonObject(): JsonObject = JsonObject(map)

    companion object {
        val Null = JsonNull
    }
}

@KsonDsl
class KsonArray {
    private val elements = mutableListOf<JsonElement>()

    @KsonDsl
    fun add(value: JsonElement) {
        elements.add(value)
    }

    @KsonDsl
    fun addAll(values: Collection<JsonElement>) {
        elements.addAll(values)
    }

    @KsonDsl
    fun addAll(vararg values: JsonElement) {
        elements.addAll(values)
    }

    inline fun <T> addAll(
        iterable: Iterable<T>,
        transform: JsonElementFactory.(T) -> JsonElement,
    ) {
        iterable.forEach {
            JsonElementFactory.transform(it).include()
        }
    }

    inline fun <T> addAll(
        iterable: Sequence<T>,
        transform: JsonElementFactory.(T) -> JsonElement,
    ) {
        iterable.forEach {
            JsonElementFactory.transform(it).include()
        }
    }

    @KsonDsl
    inline fun obj(block: Kson.() -> Unit) = add(kson(block))

    @KsonDsl
    fun value(value: String?) = add(JsonPrimitive(value))

    @KsonDsl
    fun value(value: Number?) = add(JsonPrimitive(value))

    @KsonDsl
    fun value(value: Boolean?) = add(JsonPrimitive(value))

    @KsonDsl
    fun Boolean?.include() {
        value(this)
    }

    @KsonDsl
    fun String?.include() {
        value(this)
    }

    @KsonDsl
    fun Number?.include() {
        value(this)
    }

    @KsonDsl
    fun JsonElement?.include() {
        add(this ?: JsonNull)
    }

    @KsonDsl
    fun addNull() = add(JsonNull)

    @PublishedApi
    internal fun toJsonArray(): JsonArray = JsonArray(elements)
}

object JsonElementFactory {
    operator fun String?.invoke() = JsonPrimitive(this)

    operator fun Number?.invoke() = JsonPrimitive(this)

    operator fun Boolean?.invoke() = JsonPrimitive(this)

    operator fun JsonElement?.invoke() = this

    val Null = JsonNull
}

inline fun <T> Iterable<T>.mapToJsonArray(transform: JsonElementFactory.(T) -> JsonElement) =
    JsonArray(map { JsonElementFactory.transform(it) })

@JvmName("mapStringsToJsonArray")
fun Iterable<String>.mapToJsonArray() = JsonArray(map { JsonPrimitive(it) })

@JvmName("mapNumbersToJsonArray")
fun Iterable<Number>.mapToJsonArray() = JsonArray(map { JsonPrimitive(it) })

@JvmName("mapBooleansToJsonArray")
fun Iterable<Boolean>.mapToJsonArray() = JsonArray(map { JsonPrimitive(it) })

@JvmName("mapJsonElementsToJsonArray")
fun Iterable<JsonElement>.mapToJsonArray() = JsonArray(toList())

inline fun <T> Iterable<T>.mapToJsonArrayOfObjects(transform: Kson.(T) -> Unit) =
    JsonArray(map { kson { transform(it) } })

inline fun <V> Map<String, V>.mapToJsonObject(transform: JsonElementFactory.(V) -> JsonElement) =
    JsonObject(mapValues { (_, value) -> JsonElementFactory.transform(value) })

@JvmName("mapStringsToJsonObject")
fun Map<String, String>.mapToJsonObject() = JsonObject(mapValues { (_, value) -> JsonPrimitive(value) })

@JvmName("mapNumbersToJsonObject")
fun Map<String, Number>.mapToJsonObject() = JsonObject(mapValues { (_, value) -> JsonPrimitive(value) })

@JvmName("mapBooleansToJsonObject")
fun Map<String, Boolean>.mapToJsonObject() = JsonObject(mapValues { (_, value) -> JsonPrimitive(value) })

@JvmName("mapJsonElementsToJsonObject")
fun Map<String, JsonElement>.mapToJsonObject() = JsonObject(toMap())

@KsonDsl
inline fun kson(block: Kson.() -> Unit) = Kson().apply(block).toJsonObject()

@KsonDsl
inline fun ksonArray(block: KsonArray.() -> Unit) = KsonArray().apply(block).toJsonArray()
