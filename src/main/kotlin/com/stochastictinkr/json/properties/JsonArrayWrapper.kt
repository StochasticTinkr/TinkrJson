package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

class JsonArrayWrapper<V>(
    private val converter: Converter<JsonElement, V>,
    val jsonArray: JsonArray = JsonArray()
): AbstractMutableList<V>() {
    override fun add(index: Int, element: V) {
        jsonArray.add(index, converter.reverse(element))
    }

    override fun removeAt(index: Int): V {
        return converter.forward(jsonArray.removeAt(index))
    }

    override fun set(index: Int, element: V): V {
        return converter.forward(jsonArray.set(index, converter.reverse(element)))
    }

    override val size: Int get() = jsonArray.size

    override fun isEmpty(): Boolean {
        return jsonArray.isEmpty()
    }

    override fun get(index: Int): V {
        return converter.forward(jsonArray[index])
    }
}