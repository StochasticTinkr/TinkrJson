package com.stochastictinkr.json.wrapper

import com.stochastictinkr.json.*
import com.stochastictinkr.json.schema.*
import kotlin.jvm.*

open class Compound(
    private val json: JsonObject,
) {
    @JvmName("getRequired")
    protected operator fun <T, J : JsonType<T>> PropertyDescriptor<T, J, Required>.getValue(
        thisRef: Any?,
        property: Any?,
    ) = type.convert(json.getValue(name))

    @JvmName("getOptional")
    protected operator fun <T, J : JsonType<T>> PropertyDescriptor<T, J, Optional>.getValue(
        thisRef: Any?,
        property: Any?,
    ) = json[this.name]?.let(type::convert)

}

open class CompoundClass<T>(
    private val converter: (JsonObject) -> T,
    val schema: JsonObjectSchema = JsonObjectSchema(),
) : JsonObjectSchemaBuilder by schema,
    JsonType<T> by CompoundJsonType(schema).map(converter)
