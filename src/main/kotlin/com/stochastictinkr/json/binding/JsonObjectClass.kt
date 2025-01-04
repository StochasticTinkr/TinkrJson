package com.stochastictinkr.json.binding

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*
import com.stochastictinkr.json.properties.JsonObjectWrapper.*
import com.stochastictinkr.json.schema.*

open class JsonObjectClass<W>(private val wrap: (JsonObject) -> W) : (JsonObject) -> W {
    override fun invoke(jsonObject: JsonObject): W = wrap(jsonObject)
    operator fun invoke(): W = wrap(JsonObject())
    val schema = jsonSchema {
        type = "object"
    }

    private val properties = schema.objectProperties.properties.createOrGet()
    private val required = schema.objectProperties.required.createOrGet()

    protected operator fun <Value, SchemaConfig> TypeDescriptor<Value, SchemaConfig>.invoke(
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ): PropertyDescriptor<Value> {
        val propertyDescriptor = PropertyDescriptor(name, this)
        properties[propertyDescriptor.name] = jsonSchema {
            propertyDescriptor.type.run { configureSchema(configure) }
        }
        required += propertyDescriptor.name
        return propertyDescriptor
    }

    @JvmName("wrappedJsonObject")
    protected operator fun <Value : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonObject, SchemaConfig>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ) = (this wrapped wrap)(name, configure)

    @JvmName("wrappedJsonObjectClass")
    protected operator fun <Value : JsonObjectWrapper> TypeDescriptor<JsonObject, JsonSchema.ObjectProperties>.invoke(
        wrap: JsonObjectClass<Value>,
        name: String,
    ) = PropertyDescriptor(name, this wrapped wrap).also {
        properties[name] = wrap.schema
        required += name
    }

    @JvmName("wrappedJsonArrayOfJsonObjectWrapper")
    protected operator fun <Value : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ) = (this wrappedItems wrap)(name, configure)

    protected operator fun <Value, SchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.invoke(
        itemType: TypeDescriptor<Value, *>,
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ) = (this wrappedItems itemType)(name, configure)

    protected fun <Value : Any> PropertyDescriptor<Value>.nullable(defaultNull: Boolean = false) =
        PropertyDescriptor(name, type.nullable(defaultNull)).also {
            val existing = properties[name]
            properties[name] = jsonSchema {
                compose {
                    oneOf {
                        if (existing != null) add(existing)
                        add {
                            type = "null"
                        }
                    }
                }
            }
        }

    protected fun <Value> PropertyDescriptor<Value>.optional() = OptionalPropertyDescriptor(this).also {
        required -= name
    }

    protected infix fun <Value> PropertyDescriptor<Value>.schema(configure: JsonSchema.() -> Unit) = apply {
        properties.update(name, configure)
    }

    protected infix fun <Value> OptionalPropertyDescriptor<Value>.schema(configure: JsonSchema.() -> Unit) = apply {
        properties.update(property.name, configure)
    }
}


