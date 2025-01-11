@file:Suppress("ConstPropertyName")

package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*

@DslMarker
annotation class JsonSchemaDsl

interface CommonProperties {
    var type: String?
    var title: String?
    var description: String?
    var default: JsonElement?
    var examples: JsonArray?

    fun set(
        type: String? = null,
        title: String? = null,
        description: String? = null,
        default: JsonElement? = null,
        examples: JsonArray? = null,
    )
}

@JsonSchemaDsl
class JsonSchema(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject),
    CommonProperties by CommonPropertiesImpl(jsonObject) {

    val objectProperties = ObjectProperties()
    val arrayProperties = ArrayProperties()
    val stringProperties = StringProperties()
    val numericProperties = NumericProperties()
    val compositionProperties = CompositionProperties()

    @JsonSchemaDsl
    fun string(block: StringProperties.() -> Unit = {}) {
        type = "string"
        stringProperties.block()
    }

    @JsonSchemaDsl
    fun <N : Number> number(typeDescriptor: TypeDescriptor<N, *>, block: TypedNumericProperties<N>.() -> Unit = {}) {
        type = "number"
        numericProperties[typeDescriptor].block()
    }

    @JsonSchemaDsl
    fun integer(block: TypedNumericProperties<Int>.() -> Unit = {}) {
        type = "integer"
        numericProperties[int].block()
    }

    @JsonSchemaDsl
    fun boolean(block: CommonProperties.() -> Unit = {}) {
        type = "boolean"
        block()
    }

    @JsonSchemaDsl
    fun obj(block: ObjectProperties.() -> Unit = {}) {
        type = "object"
        objectProperties.block()
    }

    @JsonSchemaDsl
    fun array(block: ArrayProperties.() -> Unit = {}) {
        type = "array"
        arrayProperties.block()
    }

    @JsonSchemaDsl
    fun compose(block: CompositionProperties.() -> Unit) {
        compositionProperties.block()
    }

    @JsonSchemaDsl
    fun common(block: CommonProperties.() -> Unit) {
        block()
    }

    inner class NumericProperties : CommonProperties by this@JsonSchema {
        operator fun <N : Number> get(type: TypeDescriptor<N, *>) = TypedNumericProperties(type)

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

    inner class TypedNumericProperties<N : Number>(number: TypeDescriptor<N, *>) : CommonProperties by this@JsonSchema {
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


    inner class ObjectProperties : CommonProperties by this@JsonSchema {
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

        fun property(name: String, block: JsonSchema.() -> Unit): JsonSchema =
            properties.createOrGet().update(name, block)

        fun required(vararg names: String) {
            required.createOrGet().addAll(names)
        }

        fun removeRequired(name: String) {
            required.getOrNull()?.remove(name)
        }
    }

    class SchemaMap(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
        operator fun get(name: String) = jsonObject[name]?.jsonObject?.let { JsonSchema(it) }
        operator fun set(name: String, value: JsonSchema?) {
            jsonObject.setNonNull(name, value?.jsonObject)
        }

        operator fun invoke(block: SchemaMap.() -> Unit) = block()

        fun update(name: String, block: JsonSchema.() -> Unit): JsonSchema =
            jsonObject
                .getOrPut(name) { JsonObject() }
                .jsonObject
                .let { JsonSchema(it) }
                .apply(block)

        operator fun contains(name: String) = name in jsonObject

        companion object : (JsonObject) -> SchemaMap {
            override fun invoke(jsonObject: JsonObject) = SchemaMap(jsonObject)
        }
    }

    inner class ArrayProperties : CommonProperties by this@JsonSchema {
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

    inner class StringProperties : CommonProperties by this@JsonSchema {
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

    inner class CompositionProperties : CommonProperties by this@JsonSchema {
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
        fun add(schema: JsonSchema) {
            contents.jsonArray.add(schema.jsonObject)
        }

        fun add(block: JsonSchema.() -> Unit) {
            contents.jsonArray.addObject {
                JsonSchema(this).block()
            }
        }
    }

    private class CommonPropertiesImpl(jsonObject: JsonObject) : JsonObjectWrapper(jsonObject), CommonProperties {
        override var type by string("type").optional()
        override var title by string("title").optional()
        override var description by string("description").optional()
        override var default by jsonElement("default").optional()
        override var examples by jsonArray("examples").optional()

        override fun set(
            type: String?,
            title: String?,
            description: String?,
            default: JsonElement?,
            examples: JsonArray?,
        ) {
            this.type = type
            this.title = title
            this.description = description
            this.default = default
            this.examples = examples
        }
    }
}


@JsonSchemaDsl
fun jsonSchema(block: JsonSchema.() -> Unit = {}) = JsonSchema().apply(block)

