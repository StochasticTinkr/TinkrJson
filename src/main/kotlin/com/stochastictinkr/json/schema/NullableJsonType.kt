package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class NullableJsonType<T : Any, J : JsonType<T>>(
    val innerType: J,
) : JsonType<T?> {
    override fun convert(jsonElement: JsonElement): T? =
        when (jsonElement) {
            is JsonNull -> null
            else -> innerType.convert(jsonElement)
        }

    override fun isValid(jsonElement: JsonElement) = jsonElement is JsonNull || innerType.isValid(jsonElement)

    override fun JsonArray.asUnionMember() {
        add("null")
        with(innerType) { asUnionMember() }
    }

    override fun JsonObject.toSchema() {
        "type"(JsonArray().apply { asUnionMember() })
    }
}

fun <T : Any, J : JsonType<T>> J.nullable() = NullableJsonType(this)
