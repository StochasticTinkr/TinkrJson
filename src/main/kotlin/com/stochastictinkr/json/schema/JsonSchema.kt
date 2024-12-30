@file:Suppress("ConstPropertyName")

package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*
import kotlin.properties.*

class JsonSchema(jsonObject: JsonObject = JsonObject()) : JsonObjectWrapper(jsonObject) {
    // Common properties
    var type by string("type").optional()
    var title by string("title").optional()
    var description by string("description").optional()
    var default by jsonObject("default").optional()
    var examples by jsonArray("examples").optional()

    // Object properties
    val properties by jsonObject(::ObjectProperties, "properties").optionalRef()
    val required by jsonArray(string, "required").optionalRef()
    var additionalProperties by jsonElement("additionalProperties")
    var minProperties by int("minProperties").optional()
    var maxProperties by int("maxProperties").optional()

    // Array properties
    val items by jsonObject(::JsonSchema, "items").optionalRef()
    var minItems by int("minItems").optional()
    var maxItems by int("maxItems").optional()
    var uniqueItems by boolean("uniqueItems").optional()

    // String properties
    var minLength by int("minLength").optional()
    var maxLength by int("maxLength").optional()
    var pattern by string("pattern").optional()

    // Number properties
    val intProperties get():NumberProperties<Int> = NumberProperties.IntProperties(jsonObject)
    val longProperties get():NumberProperties<Long> = NumberProperties.LongProperties(jsonObject)
    val floatProperties get():NumberProperties<Float> = NumberProperties.FloatProperties(jsonObject)
    val doubleProperties get():NumberProperties<Double> = NumberProperties.DoubleProperties(jsonObject)

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
    val allOf by jsonArray(::JsonSchema, "allOf").optionalRef()
    val anyOf by jsonArray(::JsonSchema, "anyOf").optionalRef()
    val oneOf by jsonArray(::JsonSchema, "oneOf").optionalRef()
    val not by jsonObject(::JsonSchema, "not").optionalRef()

    sealed class NumberProperties<N : Number>(jsonObject: JsonObject) : JsonObjectWrapper(jsonObject) {
        var multipleOf by number("multipleOf")
        var minimum by number("minimum")
        var maximum by number("maximum")
        var exclusiveMinimum by number("exclusiveMinimum")
        var exclusiveMaximum by number("exclusiveMaximum")

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

        protected abstract fun number(name: String): OptionalProperty<N, N?>

        internal class IntProperties(jsonObject: JsonObject) : NumberProperties<Int>(jsonObject) {
            override fun number(name: String) = int(name).optional()
        }

        internal class LongProperties(jsonObject: JsonObject) : NumberProperties<Long>(jsonObject) {
            override fun number(name: String) = long(name).optional()
        }

        internal class FloatProperties(jsonObject: JsonObject) : NumberProperties<Float>(jsonObject) {
            override fun number(name: String) = float(name).optional()
        }

        internal class DoubleProperties(jsonObject: JsonObject) : NumberProperties<Double>(jsonObject) {
            override fun number(name: String) = double(name).optional()
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
