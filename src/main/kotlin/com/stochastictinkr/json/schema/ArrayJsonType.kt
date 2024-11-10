package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class ArrayJsonType<T>(
    val outer: JsonType<JsonArray>,
    val items: JsonType<T>,
) : JsonType<List<T>> {
    override fun Kson.toSchema() {
        "type" /= "array"
        obj("items") { with(items) { toSchema() } }
    }

    override fun isValid(jsonElement: JsonElement): Boolean =
        jsonElement is JsonArray &&
            jsonElement.all {
                this.items.isValid(
                    it
                )
            }

    override fun convert(jsonElement: JsonElement): List<T> = outer.convert(jsonElement).map { items.convert(it) }
}

fun <T> JsonType<JsonArray>.withItems(items: JsonType<T>) = ArrayJsonType(this, items)

fun <T> list(items: JsonType<T>) = jsonArray.withItems(items)
