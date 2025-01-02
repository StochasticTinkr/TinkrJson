package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

sealed class ToKotlinType<K>(
    val converter: Converter<JsonElement, K>,
    val createDefault: (() -> K)? = null,
) {
    constructor(
        forward: Transformer<JsonElement, K>,
        reverse: Transformer<K, JsonElement>,
        createDefault: (() -> K)? = null,
    ) : this(Converter(forward, reverse), createDefault)

    data object BooleanType : ToKotlinType<Boolean>(JsonElement::boolean, Boolean::toJsonBoolean, { false })
    data object IntType : ToKotlinType<Int>(JsonElement::int, Int::toJsonNumber, { 0 })
    data object LongType : ToKotlinType<Long>(JsonElement::long, Long::toJsonNumber, { 0L })
    data object FloatType : ToKotlinType<Float>(JsonElement::float, Float::toJsonNumber, { 0.0f })
    data object DoubleType : ToKotlinType<Double>(JsonElement::double, Double::toJsonNumber, { 0.0 })
    data object StringType : ToKotlinType<String>(JsonElement::string, String::toJsonString, { "" })
    data object JsonObjectType : ToKotlinType<JsonObject>(JsonElement::jsonObject, { it }, ::JsonObject)
    data object JsonArrayType : ToKotlinType<JsonArray>(JsonElement::jsonArray, { it }, ::JsonArray)
    data object AnyType : ToKotlinType<JsonElement>({ it }, { it }, { JsonNull })

    class WrappedType<K : Any, W : Any>(
        wrapped: ToKotlinType<K>,
        converter: Converter<K, W>,
    ) : ToKotlinType<W>(
        converter = wrapped.converter then converter,
        createDefault = wrapped.createDefault?.let { { converter.forward(it()) } }
    )
}

val boolean = RequiredNonNullDescriptor(ToKotlinType.BooleanType)
val int = RequiredNonNullDescriptor(ToKotlinType.IntType)
val long = RequiredNonNullDescriptor(ToKotlinType.LongType)
val float = RequiredNonNullDescriptor(ToKotlinType.FloatType)
val double = RequiredNonNullDescriptor(ToKotlinType.DoubleType)
val string = RequiredNonNullDescriptor(ToKotlinType.StringType)
val jsonObject = RequiredNonNullDescriptor(ToKotlinType.JsonObjectType)
val jsonArray = RequiredNonNullDescriptor(ToKotlinType.JsonArrayType)
val jsonElement = RequiredNonNullDescriptor(ToKotlinType.AnyType)

@JvmName("wrapNullable")
fun <P : Presence, K : Any, W : Any> NullableDescriptor<P, K>.wrapped(converter: Converter<K, W>) =
    NullableDescriptor<P, W>(ToKotlinType.WrappedType(toKotlinType, converter))

@JvmName("wrapNonNull")
fun <P : Presence, K : Any, W : Any> NonNullDescriptor<P, K>.wrapped(converter: Converter<K, W>) =
    NonNullDescriptor<P, W>(ToKotlinType.WrappedType(toKotlinType, converter))

@JvmName("wrapNullable")
fun <P : Presence, W : JsonObjectWrapper> NullableDescriptor<P, JsonObject>.wrapped(wrap: (JsonObject) -> W) =
    NullableDescriptor<P, W>(ToKotlinType.WrappedType(toKotlinType, Converter(wrap)))

@JvmName("wrapNonNull")
fun <P : Presence, W : JsonObjectWrapper> NonNullDescriptor<P, JsonObject>.wrapped(wrap: (JsonObject) -> W) =
    NonNullDescriptor<P, W>(ToKotlinType.WrappedType(toKotlinType, Converter(wrap)))

fun <W : JsonObjectWrapper> Converter(wrap: (JsonObject) -> W) = Converter<JsonObject, W>(
    { wrap(it.jsonObject) },
    { it.jsonObject }
)

fun <V> Converter<JsonObject, V>.fromElement() = ToKotlinType.JsonObjectType.converter then this

sealed interface Presence
data object Required : Presence
data object Optional : Presence

data class Descriptor<P : Presence, K : Any, V : K?>(val toKotlinType: ToKotlinType<K>) {
    val forward get() = toKotlinType.converter.forward
    val reverse get() = toKotlinType.converter.reverse
    fun forward(jsonElement: JsonElement): K = forward.transform(jsonElement)
    fun reverse(value: K): JsonElement = reverse.transform(value)
}

typealias RequiredDescriptor<K, V> = Descriptor<Required, K, V>
typealias OptionalDescriptor<K, V> = Descriptor<Optional, K, V>
typealias NullableDescriptor<P, K> = Descriptor<P, K, K?>
typealias NonNullDescriptor<P, K> = Descriptor<P, K, K>
typealias RequiredNonNullDescriptor<K> = Descriptor<Required, K, K>

fun <P : Presence, K : Any> Descriptor<P, K, K>.nullable(): Descriptor<P, K, K?> = Descriptor(toKotlinType)
fun <K : Any, V : K?> Descriptor<Required, K, V>.optional(): Descriptor<Optional, K, V> = Descriptor(toKotlinType)

data class PropertyDescriptor<D : Descriptor<*, *, *>>(val name: String, val descriptor: D) {
    inline fun <D2 : Descriptor<*, *, *>> modify(block: D.() -> D2) = PropertyDescriptor(name, descriptor.block())
}

typealias RequiredProperty<K, V> = PropertyDescriptor<RequiredDescriptor<K, V>>
typealias OptionalProperty<K, V> = PropertyDescriptor<OptionalDescriptor<K, V>>

fun <K : Any, V : K?> RequiredProperty<K, V>.optional() = modify { optional() }
fun <P : Presence, K : Any, D : Descriptor<P, K, K>> PropertyDescriptor<D>.nullable() = modify { nullable() }

fun <O : OptionalProperty<*, *>> O.byRef() = ByReference(this)

data class ByReference<T>(val value: T)
