package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

private class SimpleJsonType<T>(
    val name: String,
    val converter: JsonElement.() -> T,
    val validator: JsonElement.() -> Boolean = { true },
) : JsonType<T> {
    override fun JsonArray.asUnionMember() {
        add(name)
    }

    override fun JsonObject.toSchema() {
        "type"(name)
    }

    override fun convert(jsonElement: JsonElement): T = jsonElement.converter()

    override fun isValid(jsonElement: JsonElement): Boolean = jsonElement.validator()
}

private inline fun <T> primitiveType(
    name: String,
    crossinline get: JsonElement.() -> T,
    crossinline getOrNull: JsonElement.() -> T?,
): JsonType<T> =
    SimpleJsonType(
        name = name,
        converter = { get() },
        validator = { getOrNull() != null }
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

val int = primitiveType("integer", { jsonNumber.toInt() }, { jsonNumberOrNull?.toIntOrNull() })
val string = primitiveType("string", { jsonString.value }, { jsonStringOrNull?.value })
val boolean = primitiveType("boolean", { jsonBoolean.value }, { jsonBooleanOrNull?.value })
val float = primitiveType("number", { jsonNumber.toFloat() }, { jsonNumberOrNull?.toFloatOrNull() })
val double = primitiveType("number", { jsonNumber.toDouble() }, { jsonNumberOrNull?.toDoubleOrNull() })
val long = primitiveType("number", { jsonNumber.toLong() }, { jsonNumberOrNull?.toLongOrNull() })
val jsonArray = elementType("array") { jsonArray }
val jsonObject = elementType("object") { jsonObject }
