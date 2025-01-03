package com.stochastictinkr.json.binding

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*
import com.stochastictinkr.json.schema.*

open class JsonObjectWrapperBuilder<W>(private val wrap: (JsonObject) -> W) {
    val schema = jsonSchema { obj() }
    val properties = schema.objectProperties.properties.createOrGet()
    val requiredProperties = schema.objectProperties.required.createOrGet()

}

