package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class ArrayJsonType<T>(
    val outer: JsonType<JsonArray>,
    val items: JsonType<T>,
) : JsonType<List<T>> {
    override fun JsonObject.toSchema() {
        "type"("array")
        "items" { items.run { toSchema() } }
    }

    override fun isValid(jsonElement: JsonElement): Boolean =
        jsonElement.jsonArrayOrNull?.all { items.isValid(it) } ?: false

    override fun convert(jsonElement: JsonElement): List<T> = outer.convert(jsonElement).map { items.convert(it) }
}

fun <T> JsonType<JsonArray>.withItems(items: JsonType<T>) = ArrayJsonType(this, items)

fun <T> list(items: JsonType<T>) = jsonArray.withItems(items)
