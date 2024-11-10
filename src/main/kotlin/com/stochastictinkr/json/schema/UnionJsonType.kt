package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

class UnionJsonType(
    private val types: MutableList<Entry> = mutableListOf(),
) : JsonType<JsonType<*>?> {
    data class Entry(
        var type: JsonType<*>,
    )

    override fun KsonArray.asUnionMember() {
        types.forEach { (type) -> with(type) { asUnionMember() } }
    }

    override fun Kson.toSchema() {
        "type" /= ksonArray { asUnionMember() }
    }

    override fun isValid(jsonElement: JsonElement): Boolean = types.any { (type) -> type.isValid(jsonElement) }

    override fun convert(jsonElement: JsonElement) = types.firstOrNull { (type) -> type.isValid(jsonElement) }?.type

    fun <T, J : JsonType<T>> add(type: J) = Entry(type).also { types.add(it) }
}
