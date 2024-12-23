@file:Suppress("ConstPropertyName")

package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

sealed class JsonSchema<S : JsonSchema<S>> {
    abstract val metadata: SchemaMetadata
    abstract fun withMetadata(metadata: SchemaMetadata): S

    inline fun mapMetadata(transform: SchemaMetadata.() -> SchemaMetadata): S = withMetadata(metadata.transform())
    protected abstract fun JsonObject.build()
    internal abstract val type: JsonSchemaType
    fun toJson(): JsonObject = jsonObject {
        setSchemaType(type)
        metadata.run { build() }
        build()
    }
}

data class SchemaMetadata(
    val title: String? = null,
    val description: String? = null,
    val default: JsonElement? = null,
    val examples: JsonArray? = null,
) {
    internal fun JsonObject.build() {
        "title".nonNull(title)
        "description".nonNull(description)
        "default".nonNull(default)
        "examples".nonNull(examples)
    }
}

data class NumericProperties<N : Number>(
    val multipleOf: N? = null,
    val minimum: N? = null,
    val maximum: N? = null,
    val exclusiveMinimum: Boolean = false,
    val exclusiveMaximum: Boolean = false,
) {
    private companion object {
        const val multipleOfProperty = "multipleOf"
        const val minimumProperty = "minimum"
        const val maximumProperty = "maximum"
        const val exclusiveMinimumProperty = "exclusiveMinimum"
        const val exclusiveMaximumProperty = "exclusiveMaximum"
    }

    internal fun JsonObject.build() {
        multipleOf?.let { multipleOfProperty(it) }
        minimum?.let { minimumProperty(it) }
        maximum?.let { maximumProperty(it) }
        if (exclusiveMinimum) exclusiveMinimumProperty(true)
        if (exclusiveMaximum) exclusiveMaximumProperty(true)
    }
}

typealias FloatProperties = NumericProperties<Float>
typealias DoubleProperties = NumericProperties<Double>
typealias LongProperties = NumericProperties<Long>
typealias IntProperties = NumericProperties<Int>

internal enum class JsonSchemaType(val jsonType: String) {
    OBJECT("object"),
    ARRAY("array"),
    STRING("string"),
    NUMBER("number"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    NULL("null")
}

private fun JsonObject.setSchemaType(type: JsonSchemaType) {
    "type"(type.jsonType)
}

class IntegerSchema(
    metadata: SchemaMetadata = SchemaMetadata(),
    properties: NumericProperties<Int> = NumericProperties(),
) : NumericSchema<Int, IntegerSchema>(metadata, properties, JsonSchemaType.INTEGER) {
    override fun copy(metadata: SchemaMetadata, properties: NumericProperties<Int>) =
        IntegerSchema(metadata, properties)
}

data class ObjectSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    private val properties: Map<String, JsonSchema<*>> = emptyMap(),
    private val required: List<String> = emptyList(),
    private val additionalProperties: Boolean? = null,
    private val patternProperties: Map<String, JsonSchema<*>> = emptyMap(),
    private val propertyNames: JsonSchema<*>? = null,
    private val dependencies: Map<String, Dependency> = emptyMap(),
    private val minProperties: Int? = null,
    private val maxProperties: Int? = null,
) : JsonSchema<ObjectSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT

    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    override fun JsonObject.build() {
        "properties".nonNull(propertiesObject(properties))
        "required".nonNull(required.toJsonArray())
        "additionalProperties".nonNull(additionalProperties)
        "patternProperties".nonNull(propertiesObject(patternProperties))
        "propertyNames".nonNull(propertyNames?.toJson())
        "dependencies".nonNull(dependenciesObject(dependencies))
    }

    private fun propertiesObject(properties: Map<String, JsonSchema<*>>): JsonObject? = properties
        .takeUnless { it.isEmpty() }
        ?.mapValuesTo(JsonObject()) { (_, schema) -> schema.toJson() }

    private fun dependenciesObject(dependencies: Map<String, Dependency>): JsonObject? = dependencies
        .takeUnless { it.isEmpty() }
        ?.mapValuesTo(JsonObject()) { (_, value) -> value.toJson() }
}

sealed class Dependency {
    data class Property(val requiredProperties: List<String>) : Dependency() {
        override fun toJson(): JsonElement = requiredProperties.toJsonArray()
    }

    data class Schema(val schema: ObjectSchema) : Dependency() {
        override fun toJson(): JsonElement = schema.toJson()
    }

    internal abstract fun toJson(): JsonElement
}


data class ArraySchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    private val items: JsonSchema<*>? = null,
    private val additionalItems: JsonSchema<*>? = null,
    private val minItems: Int? = null,
    private val maxItems: Int? = null,
    private val uniqueItems: Boolean = false,
) : JsonSchema<ArraySchema>() {
    override val type: JsonSchemaType = JsonSchemaType.ARRAY

    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    override fun JsonObject.build() {
        "items".nonNull(items?.toJson())
        "additionalItems".nonNull(additionalItems?.toJson())
        "minItems".nonNull(minItems)
        "maxItems".nonNull(maxItems)
        "uniqueItems".nonNull(uniqueItems)
    }
}

data class StringSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    private val minLength: Int? = null,
    private val maxLength: Int? = null,
    private val pattern: String? = null,
    private val format: String? = null,
) : JsonSchema<StringSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.STRING

    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    override fun JsonObject.build() {
        "minLength".nonNull(minLength)
        "maxLength".nonNull(maxLength)
        "pattern".nonNull(pattern)
        "format".nonNull(format)
    }
}

sealed class NumericSchema<N : Number, S : NumericSchema<N, S>>(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    val properties: NumericProperties<N>,
    override val type: JsonSchemaType = JsonSchemaType.NUMBER,
) : JsonSchema<S>() {
    override fun withMetadata(metadata: SchemaMetadata): S = copy(metadata = metadata)
    fun withProperties(properties: NumericProperties<N>): S = copy(properties = properties)
    abstract fun copy(
        metadata: SchemaMetadata = this.metadata,
        properties: NumericProperties<N> = this.properties,
    ): S
    inline fun mapProperties(transform: NumericProperties<N>.() -> NumericProperties<N>): S = withProperties(properties.transform())

    override fun JsonObject.build() {
        properties.run { build() }
    }
}

class NumberSchema<N : Number> private constructor(
    metadata: SchemaMetadata = SchemaMetadata(),
    properties: NumericProperties<N>,
) : NumericSchema<N, NumberSchema<N>>(metadata, properties) {

    override fun copy(metadata: SchemaMetadata, properties: NumericProperties<N>) =
        NumberSchema(metadata, properties)

    companion object {
        @JvmName("intNumberSchema")
        operator fun invoke(
            metadata: SchemaMetadata = SchemaMetadata(),
            properties: IntProperties = IntProperties(),
        ) = NumberSchema(metadata, properties)

        @JvmName("longNumberSchema")
        operator fun invoke(
            metadata: SchemaMetadata = SchemaMetadata(),
            properties: LongProperties = LongProperties(),
        ) = NumberSchema(metadata, properties)

        @JvmName("floatNumberSchema")
        operator fun invoke(
            metadata: SchemaMetadata = SchemaMetadata(),
            properties: FloatProperties = FloatProperties(),
        ) = NumberSchema(metadata, properties)

        @JvmName("doubleNumberSchema")
        operator fun invoke(
            metadata: SchemaMetadata = SchemaMetadata(),
            properties: DoubleProperties = DoubleProperties(),
        ) = NumberSchema(metadata, properties)

        val int = this(properties = IntProperties())
        val long = this(properties = LongProperties())
        val float = this(properties = FloatProperties())
        val double = this(properties = DoubleProperties())
    }
}

data class BooleanSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
) : JsonSchema<BooleanSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.BOOLEAN
    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    override fun JsonObject.build() {}
}

data class NullSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
) : JsonSchema<NullSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.NULL
    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    override fun JsonObject.build() {}
}

data class ReferenceSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    private val reference: String,
) : JsonSchema<ReferenceSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT
    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    internal companion object {
        const val refProperty = "\$ref"
    }

    override fun JsonObject.build() {
        refProperty(reference)
    }
}

data class CompositeSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    private val allOf: List<JsonSchema<*>> = emptyList(),
    private val anyOf: List<JsonSchema<*>> = emptyList(),
    private val oneOf: List<JsonSchema<*>> = emptyList(),
    private val not: JsonSchema<*>? = null,
) : JsonSchema<CompositeSchema>() {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT
    override fun withMetadata(metadata: SchemaMetadata) = copy(metadata = metadata)

    internal companion object {
        const val allOfProperty = "allOf"
        const val anyOfProperty = "anyOf"
        const val oneOfProperty = "oneOf"
        const val notProperty = "not"
    }

    override fun JsonObject.build() {
        setCompositeType(allOf, allOfProperty)
        setCompositeType(anyOf, anyOfProperty)
        setCompositeType(oneOf, oneOfProperty)
        not?.let { notProperty(it.toJson()) }
    }

    private fun JsonObject.setCompositeType(list: List<JsonSchema<*>>, property: String) {
        if (list.isNotEmpty()) list.mapTo(property[{ }]) { it.toJson() }
    }
}
