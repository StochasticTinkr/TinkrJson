package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*

interface JsonType<T> {
    fun convert(jsonElement: JsonElement): T

    fun isValid(jsonElement: JsonElement): Boolean

    fun JsonArray.asUnionMember() {
        addObject { toSchema() }
    }

    fun JsonObject.toSchema()
}

fun <T, R> JsonType<T>.map(transform: (T) -> R): JsonType<R> {
    val outer = this
    return object : JsonType<R> {
        override fun JsonObject.toSchema() = with(outer) { toSchema() }

        override fun JsonArray.asUnionMember() = with(outer) { asUnionMember() }

        override fun isValid(jsonElement: JsonElement): Boolean = outer.isValid(jsonElement)

        override fun convert(jsonElement: JsonElement): R = transform(outer.convert(jsonElement))
    }
}
