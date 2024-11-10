package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

private class SimpleJsonType<T>(
    val name: String,
    val converter: JsonElement.() -> T,
    val validator: JsonElement.() -> Boolean = { true },
) : JsonType<T> {
    override fun KsonArray.asUnionMember() = value(name)

    override fun Kson.toSchema() {
        "type" /= name
    }

    override fun convert(jsonElement: JsonElement): T = jsonElement.converter()

    override fun isValid(jsonElement: JsonElement): Boolean = jsonElement.validator()
}

private inline fun <T> primitiveType(
    name: String,
    crossinline get: JsonPrimitive.() -> T,
    crossinline getOrNull: JsonPrimitive.() -> T?,
): JsonType<T> =
    SimpleJsonType(
        name = name,
        converter = { jsonPrimitive.get() },
        validator = { (this as? JsonPrimitive)?.getOrNull() != null }
    )

private inline fun <reified T : JsonElement> elementType(
    name: String,
    noinline get: JsonElement.() -> T,
): JsonType<T> =
    SimpleJsonType(
        name = name,
        converter = get,
        validator = { this is T }
    )

val int = primitiveType("integer", { int }, { intOrNull })
val string = primitiveType("string", { content }, { content.takeIf { isString } })
val boolean = primitiveType("boolean", { boolean }, { booleanOrNull })
val float = primitiveType("number", { float }, { floatOrNull })
val double = primitiveType("number", { double }, { doubleOrNull })
val long = primitiveType("number", { long }, { longOrNull })
val jsonArray = elementType("array") { jsonArray }
val jsonObject = elementType("object") { jsonObject }
