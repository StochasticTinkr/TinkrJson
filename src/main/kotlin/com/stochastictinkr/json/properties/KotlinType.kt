package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

sealed class KotlinType<K>(
    val requiredNonNull: RequiredConverter<K>,
    val optionalUnsetNull: UnsetNullOptionalConverter<K>,
    val requiredStoreNull: RequiredNullableConverter<K>,
    val optionalStoreNull: StoresNullOptionalConverter<K>,
) {
    constructor(
        forward: (JsonElement) -> K,
        forwardUnlessNull: (JsonElement) -> K?,
        reverse: (K) -> JsonElement,
        forwardExisting: (JsonElement?) -> K? = { it?.let(forward) },
        forwardExistingOrNull: (JsonElement?) -> K? = { it?.let(forwardUnlessNull) },
        unsetNull: (K?) -> JsonElement? = { it?.let(reverse) },
        storeNull: (K?) -> JsonElement = { it?.let(reverse) ?: JsonNull },
    ) : this(
        RequiredConverter(forward, reverse),
        UnsetNullOptionalConverter(forwardExisting, unsetNull),
        RequiredNullableConverter(forward, storeNull),
        StoresNullOptionalConverter(forwardExistingOrNull, storeNull),
    )

    data object BooleanType : KotlinType<Boolean>(
        forward = JsonElement::boolean,
        forwardUnlessNull = JsonElement::booleanOrNull,
        reverse = Boolean::toJsonBoolean,
    )

    data object IntType : KotlinType<Int>(
        forward = JsonElement::int,
        forwardUnlessNull = JsonElement::intUnlessNull,
        reverse = Int::toJsonNumber,
    )

    data object LongType : KotlinType<Long>(
        forward = JsonElement::long,
        forwardUnlessNull = JsonElement::longUnlessNull,
        reverse = Long::toJsonNumber,
    )

    data object FloatType : KotlinType<Float>(
        forward = JsonElement::float,
        forwardUnlessNull = JsonElement::floatUnlessNull,
        reverse = Float::toJsonNumber,
    )

    data object DoubleType : KotlinType<Double>(
        forward = JsonElement::double,
        forwardUnlessNull = JsonElement::doubleUnlessNull,
        reverse = Double::toJsonNumber,
    )

    data object StringType : KotlinType<String>(
        forward = JsonElement::string,
        forwardUnlessNull = JsonElement::stringOrNull,
        reverse = String::toJsonString,
    )

    data object JsonObjectType : KotlinType<JsonObject>(
        forward = { it.jsonObject },
        forwardUnlessNull = { it.jsonObjectUnlessNull },
        reverse = { it },
    ) {
        fun <W : Any> wrapped(converter: Converter<JsonObject, W>) =
            WrappedType(this, converter)

        operator fun <W : Any> invoke(converter: Converter<JsonObject, W>, key: String) =
            RequiredProperty(key, wrapped(converter))

        operator fun <W : JsonObjectWrapper> invoke(wrap: (JsonObject) -> W, key: String) =
            RequiredProperty(
                key, wrapped(
                    Converter(
                        forward = wrap,
                        reverse = { it.jsonObject }
                    )
                ))
    }

    data object JsonArrayType : KotlinType<JsonArray>(
        forward = { it.jsonArray },
        forwardUnlessNull = { it.jsonArrayUnlessNull },
        reverse = { it },
    ) {
        fun <W : Any> wrapped(converter: Converter<JsonArray, W>) =
            WrappedType(this, converter)

        operator fun <W : Any> invoke(converter: Converter<JsonArray, W>, key: String) =
            RequiredProperty(key, wrapped(converter))

        operator fun <W : JsonObjectWrapper> invoke(wrap: (JsonObject) -> W, key: String) =
            items(
                Converter(
                    forward = { wrap(it.jsonObject) },
                    reverse = { it.jsonObject }
                ), key
            )

        operator fun <W : Any> invoke(type: KotlinType<W>, key: String) =
            items(type.requiredNonNull, key)

        fun <W : Any> items(converter: Converter<JsonElement, W>, key: String) =
            RequiredProperty(key, wrappedItems(converter))

        fun <W> wrappedItems(converter: Converter<JsonElement, W>) =
            wrapped(
                Converter(
                    forward = { JsonArrayWrapper(converter, it) },
                    reverse = { it.jsonArray }
                ))
    }

    data object AnyType : KotlinType<JsonElement>(
        forward = { it },
        forwardUnlessNull = { it },
        reverse = { it },
    )

    data class WrappedType<K : Any, W : Any>(
        val wrapped: KotlinType<K>,
        val converter: Converter<K, W>,
        val nullableConverter: Converter<K?, W?> = Converter(
            { it?.let(converter::forward) },
            { it?.let(converter::reverse) }),
    ) : KotlinType<W>(
        requiredNonNull = wrapped.requiredNonNull then converter,
        optionalUnsetNull = wrapped.optionalUnsetNull then nullableConverter,
        requiredStoreNull = wrapped.requiredStoreNull then nullableConverter,
        optionalStoreNull = wrapped.optionalStoreNull then nullableConverter,
    )
}

val boolean = KotlinType.BooleanType
val int = KotlinType.IntType
val long = KotlinType.LongType
val float = KotlinType.FloatType
val double = KotlinType.DoubleType
val string = KotlinType.StringType
val jsonObject = KotlinType.JsonObjectType
val jsonArray = KotlinType.JsonArrayType
val jsonElement = KotlinType.AnyType

operator fun <K : Any> KotlinType<K>.invoke(key: String) =
    RequiredProperty(key, this)

class PropertyDescriptor<JE : JsonElement?, K : Any, V : K?>(
    val name: String,
    val kotlinType: KotlinType<K>,
    val converter: Converter<JE, V>,
) {
    companion object {
        operator fun <K : Any> invoke(
            name: String,
            kotlinType: KotlinType<K>,
        ) = PropertyDescriptor(name, kotlinType, kotlinType.requiredNonNull)
    }
}

typealias RequiredProperty<K, V> = PropertyDescriptor<JsonElement, K, V>
typealias OptionalProperty<K, V> = PropertyDescriptor<JsonElement?, K, V>

fun <K : Any> RequiredProperty<K, K>.nullable(): RequiredProperty<K, K?> =
    RequiredProperty(name, kotlinType, kotlinType.requiredStoreNull)

fun <K : Any> RequiredProperty<K, K?>.nonNull(): RequiredProperty<K, K> =
    RequiredProperty(name, kotlinType, kotlinType.requiredNonNull)

fun <K : Any> RequiredProperty<K, K>.optional(): OptionalProperty<K, K?> =
    OptionalProperty(name, kotlinType, kotlinType.optionalUnsetNull)

fun <K : Any> RequiredProperty<K, K>.optionalRef(): AsReference<PropertyDescriptor<*, K, K>> = AsReference(this)

fun <K : Any> RequiredProperty<K, K?>.optional(): OptionalProperty<K, K?> =
    OptionalProperty(name, kotlinType, kotlinType.optionalStoreNull)

fun <K : Any> OptionalProperty<K, *>.storeJsonNull(): OptionalProperty<K, K?> =
    OptionalProperty(name, kotlinType, kotlinType.optionalStoreNull)

fun <K : Any> OptionalProperty<K, *>.unsetOnNull(): OptionalProperty<K, K?> =
    OptionalProperty(name, kotlinType, kotlinType.optionalUnsetNull)

fun <K : Any> OptionalProperty<K, K?>.ref() = AsReference(this)

@JvmInline
value class AsReference<T>(val value: T)

/**
 * A converter that converts a [JsonElement] to a [T] and vice versa. Does not allow JsonNull values, and requires
 * the [JsonElement] to be present.
 */
typealias RequiredConverter<T> = Converter<JsonElement, T>

/**
 * Converts a [JsonElement] to a [T] and vice versa. Does not store JsonNull values, and does not require the
 * [JsonElement] to be present.
 */
typealias UnsetNullOptionalConverter<T> = Converter<JsonElement?, T?>

/**
 * Converts a [JsonElement] to a [T] and vice versa. Allows JsonNull values, and does require the [JsonElement] to be
 * present.
 */
typealias RequiredNullableConverter<T> = Converter<JsonElement, T?>

/**
 * Converts a [JsonElement] to a [T] and vice versa. Allows JsonNull values, and does not require the [JsonElement] to be
 * present.
 */
typealias StoresNullOptionalConverter<T> = Converter<JsonElement?, T?>


