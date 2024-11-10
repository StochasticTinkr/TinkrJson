package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class CompoundJsonType<S : JsonObjectSchema>(
    val schema: S,
    private val objectType: JsonType<JsonObject> = jsonObject,
) : JsonType<JsonObject> {
    override fun Kson.toSchema() {
        with(schema) {
            toSchema(this@toSchema)
        }
    }

    override fun convert(jsonElement: JsonElement): JsonObject = objectType.convert(jsonElement)

    override fun isValid(jsonElement: JsonElement): Boolean =
        objectType.isValid(jsonElement) && schema.matches(objectType.convert(jsonElement))
}

fun <S : JsonObjectSchema> JsonType<JsonObject>.withSchema(schema: S) = CompoundJsonType(schema, this)

fun compound(schema: JsonObjectSchema) = jsonObject.withSchema(schema)
