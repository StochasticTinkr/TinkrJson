package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

interface JsonConverter<V> {
    fun fromElement(element: JsonElement): V
    fun fromValue(value: V): JsonElement
}

data object IntConverter : JsonConverter<Int> {
    override fun fromElement(element: JsonElement): Int {
        return element.number.toInt()
    }

    override fun fromValue(value: Int): JsonElement {
        return JsonNumber(value)
    }
}

data object StringConverter : JsonConverter<String> {
    override fun fromElement(element: JsonElement): String {
        return element.string
    }

    override fun fromValue(value: String): JsonElement {
        return JsonString(value)
    }
}

