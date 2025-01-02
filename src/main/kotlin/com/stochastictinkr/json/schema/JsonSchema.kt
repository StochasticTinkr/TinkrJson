@file:Suppress("ConstPropertyName")

package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*

class JsonSchema(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
    // Common properties
    var type by string("type").optional()
    var title by string("title").optional()
    var description by string("description").optional()
    var default by jsonElement("default").optional()
    var examples by jsonArray("examples").optional()

    // Object properties
    val properties by jsonObject(::ObjectProperties, "properties").optional().byRef()
    val required by jsonArray(string, "required").optional().byRef()
    var additionalProperties by jsonElement("additionalProperties")
    var minProperties by int("minProperties").optional()
    var maxProperties by int("maxProperties").optional()

    // Array properties
    val items by jsonObject(::JsonSchema, "items").optional().byRef()
    var minItems by int("minItems").optional()
    var maxItems by int("maxItems").optional()
    var uniqueItems by boolean("uniqueItems").optional()

    // String properties
    var minLength by int("minLength").optional()
    var maxLength by int("maxLength").optional()
    var pattern by string("pattern").optional()

    // Number properties
    val intProperties get():NumberProperties<Int> = NumberProperties(jsonObject, int)
    val longProperties get():NumberProperties<Long> = NumberProperties(jsonObject, long)
    val floatProperties get():NumberProperties<Float> = NumberProperties(jsonObject, float)
    val doubleProperties get():NumberProperties<Double> = NumberProperties(jsonObject, double)

    fun numberProperties(
        multipleOf: Int? = null,
        minimum: Int? = null,
        maximum: Int? = null,
        exclusiveMinimum: Int? = null,
        exclusiveMaximum: Int? = null,
    ) = intProperties.set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

    fun numberProperties(
        multipleOf: Long? = null,
        minimum: Long? = null,
        maximum: Long? = null,
        exclusiveMinimum: Long? = null,
        exclusiveMaximum: Long? = null,
    ) = longProperties.set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

    fun numberProperties(
        multipleOf: Float? = null,
        minimum: Float? = null,
        maximum: Float? = null,
        exclusiveMinimum: Float? = null,
        exclusiveMaximum: Float? = null,
    ) = floatProperties.set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

    fun numberProperties(
        multipleOf: Double? = null,
        minimum: Double? = null,
        maximum: Double? = null,
        exclusiveMinimum: Double? = null,
        exclusiveMaximum: Double? = null,
    ) = doubleProperties.set(multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)

    // Composition properties
    val allOf by jsonArray(::JsonSchema, "allOf").optional().byRef()
    val anyOf by jsonArray(::JsonSchema, "anyOf").optional().byRef()
    val oneOf by jsonArray(::JsonSchema, "oneOf").optional().byRef()
    val not by jsonObject(::JsonSchema, "not").optional().byRef()

    class NumberProperties<N : Number>(
        jsonObject: JsonObject,
        number: RequiredNonNullDescriptor<N>,
    ) : JsonObjectWrapper(jsonObject) {
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

        operator fun invoke(block: NumberProperties<N>.() -> Unit) = block()
    }

    class ObjectProperties(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
        operator fun get(name: String) = jsonObject[name]?.jsonObject?.let { JsonSchema(it) }
        operator fun set(name: String, value: JsonSchema?) {
            jsonObject.setNonNull(name, value?.jsonObject)
        }

        operator fun invoke(block: ObjectProperties.() -> Unit) = block()

        operator fun invoke(name: String, block: JsonSchema.() -> Unit) {
            jsonObject.getOrPut(name) { JsonObject() }
                .jsonObject
                .let { JsonSchema(it) }
                .block()
        }
    }
}

