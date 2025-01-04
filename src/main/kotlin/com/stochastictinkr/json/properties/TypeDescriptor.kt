package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*
import com.stochastictinkr.json.schema.*

/**
 * A descriptor for a type that can be converted to and from JSON.
 *
 * @param Value The Kotlin type of the property.
 * @param SchemaConfig The type of the schema configuration.
 * @property converter The bidirectional converter for the property.
 * @property createDefault A function to create a default value for the property.
 * @property configureSchema A function to configure the schema for the property.
 */
data class TypeDescriptor<Value, SchemaConfig>(
    val converter: Converter<JsonElement, Value>,
    val createDefault: () -> Value,
    val configureSchema: JsonSchema.(SchemaConfig.() -> Unit) -> Unit = {},
) {
    /**
     * Create a new descriptor with the given forward and reverse transformers.
     */
    constructor(
        forward: Transformer<JsonElement, Value>,
        reverse: Transformer<Value, JsonElement>,
        createDefault: () -> Value,
        configureSchema: JsonSchema.(SchemaConfig.() -> Unit) -> Unit,
    ) : this(Converter(forward, reverse), createDefault, configureSchema)

    /**
     * Create an equivalent descriptor with a different default value.
     */
    infix fun withDefault(createDefault: () -> Value) = TypeDescriptor(converter, createDefault, configureSchema)

    /**
     * Create an equivalent descriptor with a different schema configuration.
     */
    infix fun <NewSchemaConfig> withSchema(configure: JsonSchema.(NewSchemaConfig.() -> Unit) -> Unit) =
        TypeDescriptor(converter, createDefault, configure)

    /**
     * Compose this descriptor with another converter to create a new descriptor.
     */
    fun <NewValue> then(
        next: Converter<Value, NewValue>,
        newCreateDefault: () -> NewValue = createDefault.let { { next.forward(it()) } },
    ) = TypeDescriptor(converter then next, newCreateDefault, configureSchema)

    /**
     * Compose this descriptor with another converter to create a new descriptor.
     */
    infix fun <K2> then(next: Converter<Value, K2>) = then(next, createDefault.let { { next.forward(it()) } })
}


/**
 * Create a new descriptor that converts to a JsonObjectWrapper instance.
 */
infix fun <Wrapped : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonObject, SchemaConfig>.wrapped(wrap: (JsonObject) -> Wrapped) =
    then(wrapper(wrap))

/**
 * Create a new descriptor that converts to a JsonArrayWrapper instance, where the items are each wrapped in a JsonObjectWrapper instance.
 */
infix fun <Wrapped : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.wrappedItems(wrap: (JsonObject) -> Wrapped) =
    wrappedItems(jsonObject.converter then wrapper(wrap))

/**
 * Create a new descriptor that converts to a JsonArrayWrapper instance, where the items are each converted by the given converter.
 */
infix fun <Value, SchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.wrappedItems(
    converter: Converter<JsonElement, Value>,
) = then(Converter({ JsonArrayWrapper(converter, it) }, { it.jsonArray }))

/**
 * Create a new descriptor that converts to a JsonArrayWrapper instance, where the items are each converted by the given converter.
 */
infix fun <Value, SchemaConfig, ItemSchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.wrappedItems(
    itemType: TypeDescriptor<Value, ItemSchemaConfig>,
) = wrappedItems(itemType.converter)

private fun <Wrapped : JsonObjectWrapper> wrapper(wrap: (JsonObject) -> Wrapped): Converter<JsonObject, Wrapped> =
    Converter({ wrap(it.jsonObject) }, { it.jsonObject })


// Numeric type descriptors
typealias NumericType<N> = TypeDescriptor<N, JsonSchema.TypedNumericProperties<N>>

/**
 * A type descriptor for a `number` value represented as an [Int]. The default value is `0`.
 *
 * This differs from [integer] in that it uses the `number` type in JSON Schema.
 */
val int: NumericType<Int> = NumericType({ it.int }, { it.toJsonNumber() }, { 0 }, { number(int, it) })

/**
 * A type descriptor for a `number` value represented as a [Long]. The default value is `0L`.
 */
val long: NumericType<Long> = NumericType({ it.long }, { it.toJsonNumber() }, { 0L }, { number(long, it) })

/**
 * A type descriptor for a `number` value represented as a [Float]. The default value is `0.0f`.
 */
val float: NumericType<Float> = NumericType({ it.float }, { it.toJsonNumber() }, { 0.0f }, { number(float, it) })

/**
 * A type descriptor for a `number` value represented as a [Double]. The default value is `0.0`.
 */
val double: NumericType<Double> = NumericType({ it.double }, { it.toJsonNumber() }, { 0.0 }, { number(double, it) })

/**
 * A type descriptor for a `integer` value represented as a [Int]. The default value is `0`.
 *
 * This differs from [int] in that it uses the `integer` type in JSON Schema, which requires the value to be an integer.
 */
val integer: NumericType<Int> = int.withSchema { integer(it) }

// String type descriptor
typealias StringType = TypeDescriptor<String, JsonSchema.StringProperties>

/**
 * A type descriptor for a `string` value. The default value is an empty string.
 */
val string: StringType = StringType({ it.string }, { it.toJsonString() }, { "" }, { string(it) })

// Boolean type descriptor
typealias BooleanType = TypeDescriptor<Boolean, CommonProperties>

/**
 * A type descriptor for a `boolean` value. The default value is `false`.
 */
val boolean: BooleanType = BooleanType({ it.boolean }, { it.toJsonBoolean() }, { false }, { boolean(it) })

// Array and object type descriptors
typealias JsonArrayType = TypeDescriptor<JsonArray, JsonSchema.ArrayProperties>

/**
 * A type descriptor for a `array` value. The default value is an empty JsonArray.
 */
val jsonArray: JsonArrayType = JsonArrayType({ it.jsonArray }, { it }, ::JsonArray, { array(it) })

typealias JsonObjectType = TypeDescriptor<JsonObject, JsonSchema.ObjectProperties>

/**
 * A type descriptor for a `object` value. The default value is an empty JsonObject.
 */
val jsonObject: JsonObjectType = JsonObjectType({ it.jsonObject }, { it }, ::JsonObject, { obj(it) })

// Arbitrary JSON element type descriptor
typealias JsonElementType = TypeDescriptor<JsonElement, JsonSchema>

/**
 * A type descriptor for an arbitrary JSON element. The default value is [JsonNull].
 */
val jsonElement: JsonElementType = JsonElementType({ it }, { it }, { JsonNull }, { it() })

/**
 * Create a version of this descriptor that allows `null` values.  `null` values will be converted to [JsonNull], and
 * [JsonNull] will be converted to `null`.
 *
 * @param defaultNull When `true`, the default value for the property will be `null`, otherwise it will be the [TypeDescriptor.createDefault]
 *                    value from the original descriptor.
 */
fun <K : Any, SchemaConfig> TypeDescriptor<K, SchemaConfig>.nullable(defaultNull: Boolean = false): TypeDescriptor<K?, SchemaConfig> {
    return TypeDescriptor(
        converter = converter.nullable(),
        createDefault = if (defaultNull) ({ null }) else createDefault,
        configureSchema = {
            compose {
                oneOf {
                    add {
                        common {
                            type = "null"
                        }
                    }
                    add {
                        configureSchema(it)
                    }
                }
            }
        }
    )
}

/**
 * Creates a new [Converter] that converts a [JsonElement] to a nullable value of type [K].
 */
private fun <K : Any> Converter<JsonElement, K>.nullable() = let { (forward, reverse) ->
    Converter<JsonElement, _>(
        forward = forward.unlessNull(),
        reverse = reverse.orJsonNull(),
    )
}

/**
 * Create a transformer that converts a [JsonElement] to a value of type [K?]. If the [JsonElement] is [JsonNull], the
 * transformer will return `null`, otherwise it will return the result of the original transformer.
 */
private fun <K> Transformer<JsonElement, K>.unlessNull(): Transformer<JsonElement, K?> =
    Transformer<JsonElement, K?> { it.unlessNull { transform(it) } }

/**
 * Create a transformer that converts a value of type [K?] to a [JsonElement]. If the value is `null`, the transformer
 * will return [JsonNull], otherwise it will return the result of the original transformer.
 */
private fun <K> Transformer<K, JsonElement>.orJsonNull() =
    Transformer<K?, JsonElement> { if (it == null) JsonNull else transform(it) }
