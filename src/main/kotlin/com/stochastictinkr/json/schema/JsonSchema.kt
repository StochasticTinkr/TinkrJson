@file:Suppress("ConstPropertyName")

package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*

@DslMarker
annotation class JsonSchemaDsl

@JsonSchemaDsl
class JsonSchema(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
    val commonProperties = CommonProperties()
    val objectProperties = ObjectProperties()
    val arrayProperties = ArrayProperties()
    val stringProperties = StringProperties()
    val numericProperties = NumericProperties()
    val compositionProperties = CompositionProperties()

    @JsonSchemaDsl
    fun string(block: StringProperties.() -> Unit = {}) {
        commonProperties.type = "string"
        stringProperties.block()
    }

    @JsonSchemaDsl
    fun <N : Number> number(type: RequiredNonNullDescriptor<N>, block: TypedNumericProperties<N>.() -> Unit = {}) {
        commonProperties.type = "number"
        numericProperties[type].block()
    }

    @JsonSchemaDsl
    fun integer(block: TypedNumericProperties<Int>.() -> Unit = {}) {
        commonProperties.type = "integer"
        numericProperties[int].block()
    }

    @JsonSchemaDsl
    fun boolean(block: CommonProperties.() -> Unit = {}) {
        commonProperties.type = "boolean"
        commonProperties.block()
    }

    @JsonSchemaDsl
    fun obj(block: ObjectProperties.() -> Unit = {}) {
        commonProperties.type = "object"
        objectProperties.block()
    }

    @JsonSchemaDsl
    fun array(block: ArrayProperties.() -> Unit = {}) {
        commonProperties.type = "array"
        arrayProperties.block()
    }

    @JsonSchemaDsl
    fun compose(block: CompositionProperties.() -> Unit) {
        compositionProperties.block()
    }

    @JsonSchemaDsl
    fun common(block: CommonProperties.() -> Unit) {
        commonProperties.block()
    }

    inner class NumericProperties : CommonProperties() {
        operator fun <N : Number> get(type: RequiredNonNullDescriptor<N>) = TypedNumericProperties(type)

        fun set(
            multipleOf: Int? = null,
            minimum: Int? = null,
            maximum: Int? = null,
            exclusiveMinimum: Int? = null,
            exclusiveMaximum: Int? = null,
        ) = this[int].set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

        fun set(
            multipleOf: Long? = null,
            minimum: Long? = null,
            maximum: Long? = null,
            exclusiveMinimum: Long? = null,
            exclusiveMaximum: Long? = null,
        ) = this[long].set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

        fun set(
            multipleOf: Float? = null,
            minimum: Float? = null,
            maximum: Float? = null,
            exclusiveMinimum: Float? = null,
            exclusiveMaximum: Float? = null,
        ) = this[float].set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

        fun set(
            multipleOf: Double? = null,
            minimum: Double? = null,
            maximum: Double? = null,
            exclusiveMinimum: Double? = null,
            exclusiveMaximum: Double? = null,
        ) = this[double].set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

    }

    inner class TypedNumericProperties<N : Number>(number: RequiredNonNullDescriptor<N>) : CommonProperties() {
        var multipleOf by number("multipleOf").optional()
        var minimum by number("minimum").optional()
        var maximum by number("maximum").optional()
        var exclusiveMinimum by number("exclusiveMinimum").optional()
        var exclusiveMaximum by number("exclusiveMaximum").optional()

        fun set(
            multipleOf: N? = null,
            minimum: N? = null,
            maximum: N? = null,
            exclusiveMinimum: N? = null,
            exclusiveMaximum: N? = null,
        ) {
            this.multipleOf = multipleOf
            this.minimum = minimum
            this.maximum = maximum
            this.exclusiveMinimum = exclusiveMinimum
            this.exclusiveMaximum = exclusiveMaximum
        }

        fun clear() {
            multipleOf = null
            minimum = null
            maximum = null
            exclusiveMinimum = null
            exclusiveMaximum = null
        }

        operator fun invoke(block: TypedNumericProperties<N>.() -> Unit) = block()
    }

    companion object : (JsonObject) -> JsonSchema {
        override fun invoke(jsonObject: JsonObject) = JsonSchema(jsonObject)
    }

    open inner class CommonProperties internal constructor() {
        var type by string("type").optional()
        var title by string("title").optional()
        var description by string("description").optional()
        var default by jsonElement("default").optional()
        var examples by jsonArray("examples").optional()

        fun set(
            type: String? = null,
            title: String? = null,
            description: String? = null,
            default: JsonElement? = null,
            examples: JsonArray? = null,
        ) {
            this.type = type
            this.title = title
            this.description = description
            this.default = default
            this.examples = examples
        }
    }

    inner class ObjectProperties : CommonProperties() {
        val properties by jsonObject(SchemaMap, "properties").optional().byRef()
        val required by jsonArray(string, "required").optional().byRef()
        var additionalProperties by jsonElement("additionalProperties").optional()
        var minProperties by int("minProperties").optional()
        var maxProperties by int("maxProperties").optional()

        fun set(
            schemaMap: SchemaMap? = null,
            required: JsonArrayWrapper<String>? = null,
            additionalProperties: JsonElement? = null,
            minProperties: Int? = null,
            maxProperties: Int? = null,
        ) {
            this.properties.set(schemaMap)
            this.required.set(required)
            this.additionalProperties = additionalProperties
            this.minProperties = minProperties
            this.maxProperties = maxProperties
        }

        fun property(name: String, block: JsonSchema.() -> Unit) =
            properties.createOrGet()(name, block)

        fun required(vararg names: String) {
            required.createOrGet().addAll(names)
        }
    }

    class SchemaMap(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
        operator fun get(name: String) = jsonObject[name]?.jsonObject?.let { JsonSchema(it) }
        operator fun set(name: String, value: JsonSchema?) {
            jsonObject.setNonNull(name, value?.jsonObject)
        }

        operator fun invoke(block: SchemaMap.() -> Unit) = block()

        operator fun invoke(name: String, block: JsonSchema.() -> Unit) {
            jsonObject.getOrPut(name) { JsonObject() }
                .jsonObject
                .let { JsonSchema(it) }
                .block()
        }

        companion object : (JsonObject) -> SchemaMap {
            override fun invoke(jsonObject: JsonObject) = SchemaMap(jsonObject)
        }
    }

    inner class ArrayProperties : CommonProperties() {
        val items by jsonObject(JsonSchema, "items").optional().byRef()
        var minItems by int("minItems").optional()
        var maxItems by int("maxItems").optional()
        var uniqueItems by boolean("uniqueItems").optional()

        fun set(
            items: JsonSchema? = null,
            minItems: Int? = null,
            maxItems: Int? = null,
            uniqueItems: Boolean? = null,
        ) {
            this.items.set(items)
            this.minItems = minItems
            this.maxItems = maxItems
            this.uniqueItems = uniqueItems
        }

        fun items(block: JsonSchema.() -> Unit) {
            items.createOrGet().block()
        }
    }

    inner class StringProperties : CommonProperties() {
        var minLength by int("minLength").optional()
        var maxLength by int("maxLength").optional()
        var pattern by string("pattern").optional()

        fun set(
            minLength: Int? = null,
            maxLength: Int? = null,
            pattern: String? = null,
        ) {
            this.minLength = minLength
            this.maxLength = maxLength
            this.pattern = pattern
        }
    }

    inner class CompositionProperties : CommonProperties() {
        val allOf by jsonArray(JsonSchema, "allOf").optional().byRef()
        val anyOf by jsonArray(JsonSchema, "anyOf").optional().byRef()
        val oneOf by jsonArray(JsonSchema, "oneOf").optional().byRef()
        val not by jsonObject(JsonSchema, "not").optional().byRef()

        fun set(
            allOf: JsonArrayWrapper<JsonSchema>? = null,
            anyOf: JsonArrayWrapper<JsonSchema>? = null,
            oneOf: JsonArrayWrapper<JsonSchema>? = null,
            not: JsonSchema? = null,
        ) {
            this.allOf.set(allOf)
            this.anyOf.set(anyOf)
            this.oneOf.set(oneOf)
            this.not.set(not)
        }

        fun allOf(block: ArrayOfJsonSchema.() -> Unit) {
            ArrayOfJsonSchema(allOf.createOrGet()).block()
        }

        fun anyOf(block: ArrayOfJsonSchema.() -> Unit) {
            ArrayOfJsonSchema(anyOf.createOrGet()).block()
        }

        fun oneOf(block: ArrayOfJsonSchema.() -> Unit) {
            ArrayOfJsonSchema(oneOf.createOrGet()).block()
        }

        fun not(block: JsonSchema.() -> Unit) {
            not.createOrGet().block()
        }
    }

    class ArrayOfJsonSchema(val contents: JsonArrayWrapper<JsonSchema>) {
        fun add(block: JsonSchema.() -> Unit) {
            contents.jsonArray.addObject {
                JsonSchema(this).block()
            }
        }
    }
}

@JsonSchemaDsl
fun jsonSchema(block: JsonSchema.() -> Unit) = JsonSchema().apply(block)

