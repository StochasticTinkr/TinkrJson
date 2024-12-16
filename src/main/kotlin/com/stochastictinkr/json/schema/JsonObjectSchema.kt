package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

interface JsonObjectSchemaBuilder {
    fun <J : JsonType<T>, T, E : Existence> property(
        name: String,
        type: J,
        description: String?,
        existence: E,
    ): PropertyDescriptor<T, J, E>

    operator fun <T, J : JsonType<T>> PropertyDescriptor<JsonType<*>?, UnionJsonType, *>.get(
        memberType: J,
    ): PropertyDescriptor<T, J, Optional>

    operator fun <J : JsonType<T>, T> JsonType<T>.invoke(
        name: String,
        description: String? = null,
    ) = property(name, this, description, Required)
}

open class JsonObjectSchema : JsonObjectSchemaBuilder {
    private val properties = mutableMapOf<String, PropertyDescriptor<*, *, *>>()

    fun toSchema(jsonObject: JsonObject) {
        with(jsonObject) {
            set("type", "object")
            properties()
        }
    }

    fun toSchema() = JsonObject().also { toSchema(it) }

    private fun JsonObject.properties() {
        "properties" {
            properties.forEach { (name, slot) ->
                name {
                    with(slot.type) { toSchema() }
                    slot.description?.let { "description"(it) }
                }
            }
        }

        "required"(
            properties.values
                .filter { it.isRequired }
                .map { it.name }
                .toJsonArray()
        )

        "additionalProperties"(false)
    }

    internal fun matches(jsonObject: JsonObject): Boolean =
        properties.values.all { property ->
            jsonObject[property.name]
                ?.let { value -> property.type.isValid(value) }
                ?: property.isOptional
        }

    override fun <J : JsonType<T>, T, E : Existence> property(
        name: String,
        type: J,
        description: String?,
        existence: E,
    ) = PropertyDescriptor(name, type, description, existence) { properties[name] = it }

    override fun <T, J : JsonType<T>> PropertyDescriptor<JsonType<*>?, UnionJsonType, *>.get(memberType: J) =
        type.add(memberType).let { entry ->
            PropertyDescriptor(name, memberType, description, Optional) { entry.type = it.type }
        }
}

@JvmName("getRequired")
operator fun <T, J : JsonType<T>> JsonObject.get(property: PropertyDescriptor<T, J, Required>): T =
    property.type.convert(getValue(property.name))


@JvmName("getOptional")
operator fun <T, J : JsonType<T>> JsonObject.get(property: PropertyDescriptor<T, J, Optional>): T? =
    this[property.name]?.let { property.type.convert(it) }

class PropertyDescriptor<T, J : JsonType<T>, E : Existence>(
    val name: String,
    val type: J,
    val description: String?,
    val existence: E,
    val onReplace: (PropertyDescriptor<*, *, *>) -> Unit,
) {
    val isRequired = existence is Required
    val isOptional = !isRequired

    fun <NT, NJ : JsonType<NT>, NE : Existence, PD : PropertyDescriptor<NT, NJ, NE>> replaceWith(newDescriptor: PD): PD {
        onReplace(newDescriptor)
        return newDescriptor
    }

    init {
        onReplace(this)
    }
}

fun JsonObjectSchemaBuilder.int(
    name: String,
    description: String? = null,
) = property(name, int, description, Required)

fun JsonObjectSchemaBuilder.union(
    name: String,
    description: String? = null,
) = property(name, UnionJsonType(), description, Required)

fun JsonObjectSchemaBuilder.string(
    name: String,
    description: String? = null,
) = property(name, string, description, Required)

fun JsonObjectSchemaBuilder.boolean(
    name: String,
    description: String? = null,
) = property(name, boolean, description, Required)

fun JsonObjectSchemaBuilder.float(
    name: String,
    description: String? = null,
) = property(name, float, description, Required)

fun JsonObjectSchemaBuilder.double(
    name: String,
    description: String? = null,
) = property(name, double, description, Required)

fun JsonObjectSchemaBuilder.long(
    name: String,
    description: String? = null,
) = property(name, long, description, Required)

fun JsonObjectSchemaBuilder.jsonArray(
    name: String,
    description: String? = null,
) = property(name, jsonArray, description, Required)

fun JsonObjectSchemaBuilder.jsonObject(
    name: String,
    description: String? = null,
) = property(name, jsonObject, description, Required)

fun <E, J : JsonType<E>> JsonObjectSchemaBuilder.list(
    name: String,
    description: String? = null,
    type: J,
) = jsonArray(name, description).withItems(type)

fun <S : JsonObjectSchema> JsonObjectSchemaBuilder.compound(
    name: String,
    schema: S,
) = property(name, compound(schema), null, Required)

fun <S : JsonObjectSchema> JsonObjectSchemaBuilder.compound(
    name: String,
    description: String?,
    schema: S,
) = property(name, compound(schema), description, Required)

fun <T, J : JsonType<T>> PropertyDescriptor<T, J, Required>.optional() =
    replaceWith(PropertyDescriptor(name, type, description, Optional, onReplace))

fun <T, J : JsonType<T>, NT, NJ : JsonType<NT>, E : Existence> PropertyDescriptor<T, J, E>.updateType(transform: (J) -> NJ) =
    replaceWith(PropertyDescriptor(name, transform(type), description, existence, onReplace))

fun <T : Any, J : JsonType<T>, E : Existence> PropertyDescriptor<T, J, E>.nullable() = updateType { it.nullable() }

fun <J : JsonType<JsonObject>, S : JsonObjectSchema, E : Existence> PropertyDescriptor<JsonObject, J, E>.withSchema(
    schema: S,
) =
    updateType { it.withSchema(schema) }

fun <J : JsonType<JsonArray>, NT, NJ : JsonType<NT>, E : Existence> PropertyDescriptor<JsonArray, J, E>.withItems(items: NJ) =
    updateType { it.withItems(items) }
