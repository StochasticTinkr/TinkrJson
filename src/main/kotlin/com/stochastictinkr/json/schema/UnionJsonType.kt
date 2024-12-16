package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class UnionJsonType(
    private val types: MutableList<Entry> = mutableListOf(),
) : JsonType<JsonType<*>?> {
    data class Entry(
        var type: JsonType<*>,
    )

    override fun JsonArray.asUnionMember() {
        types.forEach { (type) -> with(type) { asUnionMember() } }
    }

    override fun JsonObject.toSchema() {
        "type"[{ asUnionMember() }]
    }

    override fun isValid(jsonElement: JsonElement): Boolean = types.any { (type) -> type.isValid(jsonElement) }

    override fun convert(jsonElement: JsonElement) = types.firstOrNull { (type) -> type.isValid(jsonElement) }?.type

    fun <T, J : JsonType<T>> add(type: J) = Entry(type).also { types.add(it) }
}
