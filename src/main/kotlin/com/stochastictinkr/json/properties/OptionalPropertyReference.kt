package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

class OptionalPropertyReference<V>(
    private val jsonObject: JsonObject,
    private val key: String,
    private val converter: Converter<JsonElement, V>,
) {
    val isPresent: Boolean
        get() = jsonObject.containsKey(key)

    fun set(value: V) {
        jsonObject[key] = converter.reverse(value)
    }

    fun get(): V = converter.forward(jsonObject.getValue(key))

    fun remove() {
        jsonObject.remove(key)
    }

    fun getOrSet(defaultValue: () -> V): V {
        jsonObject[key]?.let { return converter.forward(it) }
        return defaultValue().also { set(it) }
    }
}