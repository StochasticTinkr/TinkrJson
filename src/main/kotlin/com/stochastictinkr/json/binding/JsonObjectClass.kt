package com.stochastictinkr.json.binding

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*
import com.stochastictinkr.json.properties.JsonObjectWrapper.*
import com.stochastictinkr.json.schema.*

/**
 * A class describes a JsonObjectWrapper, and provides a schema for the object.
 * Common usage is to have your companion object extend this class to define the Json properties of the object,
 * and then have the class extend JsonObjectWrapper, and use property delegates to access the properties of the JsonObject
 * in a type-safe manner.
 *
 * @param W The type of the wrapped object. It is recommended to extend JsonObjectWrapper, but not required.
 * @param wrap A function that wraps a JsonObject in the wrapped object.
 */
open class JsonObjectClass<W>(private val wrap: (JsonObject) -> W) : (JsonObject) -> W {

    /**
     * Create a new instance of the wrapped object with the given JsonObject as the backing object.
     */
    override fun invoke(jsonObject: JsonObject): W = wrap(jsonObject)

    /**
     * Create a new instance of the wrapped object with a new empty JsonObject as the backing object.
     */
    operator fun invoke(): W = wrap(JsonObject())

    /**
     * The schema for the object.
     */
    val schema = jsonSchema {
        type = "object"
    }

    private val properties = schema.objectProperties.properties.createOrGet()
    private val required = schema.objectProperties.required.createOrGet()

    fun <V> addProperty(descriptor: PropertyDescriptor<V>): PropertyDescriptor<V> {
        require(descriptor.name !in properties) { "Property ${descriptor.name} already exists." }
        properties[descriptor.name] = jsonSchema {
            descriptor.type.run { configureSchema {} }
        }
        required += descriptor.name
        return descriptor
    }

    protected fun <SchemaConfig, Value> addProperty(
        name: String,
        type: TypeDescriptor<Value, SchemaConfig>,
        configure: SchemaConfig.() -> Unit = {},
    ): PropertyDescriptor<Value> = type.run {
        require(name !in properties) { "Property $name already exists." }
        properties[name] = jsonSchema { configureSchema(configure) }
        required += name
        PropertyDescriptor(name, this)
    }

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * By default, the property is required, but you can make it optional by calling [PropertyDescriptor.optional].
     * You can also make the property nullable by calling [PropertyDescriptor.nullable].
     *
     * ### Example usage:
     * ```
     * val name = string("name") {
     *    minLength = 1
     *    title = "Name"
     *    description = "The name of the person."
     * }
     * ```
     */
    protected operator fun <Value, SchemaConfig> TypeDescriptor<Value, SchemaConfig>.invoke(
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ): PropertyDescriptor<Value> = addProperty(name, this, configure)

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * The value of the property is the wrapped JsonObject.
     */
    @JvmName("wrappedJsonObject")
    protected operator fun <Value : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonObject, SchemaConfig>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ) = addProperty(name, this wrapped wrap, configure)

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * The value of the property is the wrapped JsonObject. The schema for the property is the schema from the given [JsonObjectClass].
     */
    @JvmName("wrappedJsonObjectClass")
    protected operator fun <Value : JsonObjectWrapper> TypeDescriptor<JsonObject, JsonSchema.ObjectProperties>.invoke(
        wrap: JsonObjectClass<Value>,
        name: String,
        configure: JsonSchema.ObjectProperties.() -> Unit = {},
    ) = addProperty(name, this wrapped wrap) {
        configure()
    }

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * The value of the property is a JsonArrayWrapper of the wrapped JsonObject.
     */
    @JvmName("wrappedJsonArrayOfJsonObjectWrapper")
    protected operator fun <Value : JsonObjectWrapper, SchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
        configure: SchemaConfig.() -> Unit = {},
    ) = (this wrappedItems wrap)(name, configure)

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * The value of the property is a JsonArrayWrapper of the wrapped JsonObject. The schema for the items
     * is the schema from the given [JsonObjectClass].
     */
    @JvmName("wrappedJsonArrayOfJsonObjectClassInstances")
    protected operator fun <Value : JsonObjectWrapper> TypeDescriptor<JsonArray, JsonSchema.ArrayProperties>.invoke(
        wrap: JsonObjectClass<Value>,
        name: String,
        configure: JsonSchema.ArrayProperties.() -> Unit = {},
    ) = (this wrappedItems wrap)(name) {
        items.set(wrap.schema)
        configure()
    }

    /**
     * Define a property on the object with the given name, and configure the schema for the property.
     * The value of the property is a JsonArrayWrapper of the given type.
     */
    protected operator fun <Value, SchemaConfig, ValueSchemaConfig> TypeDescriptor<JsonArray, SchemaConfig>.invoke(
        itemType: TypeDescriptor<Value, ValueSchemaConfig>,
        name: String,
        configure: ValueSchemaConfig.() -> Unit = {},
    ) = PropertyDescriptor(name, this wrappedItems itemType).also {
        properties[name] = jsonSchema {
            array {
                items.set(jsonSchema {
                    itemType.run { configureSchema(configure) }
                })
            }
        }
        required += name
    }

    /**
     * Allow the property value to be null. The schema for the property will be updated to allow null values,
     * and null values will be set as [JsonNull].
     */
    protected fun <Value : Any> PropertyDescriptor<Value>.nullable(defaultNull: Boolean = false) =
        PropertyDescriptor(name, type.nullable(defaultNull)).also {
            val existing = properties[name]
                ?: error("Property $name does not exist. Make sure this property was created with this instance.")
            properties[name] = jsonSchema {
                compose {
                    anyOf {
                        add(existing)
                        add {
                            type = "null"
                        }
                    }
                }
            }
        }

    /**
     * Make the property optional. The schema for the property will be updated to allow null values,
     */
    protected fun <Value> PropertyDescriptor<Value>.optional() = OptionalPropertyDescriptor(this).also {
        require(name in required) { "Property $name is not required. Make sure this property was created with this instance." }
        required -= name
    }

    protected infix fun <Value> PropertyDescriptor<Value>.schema(configure: JsonSchema.() -> Unit) = apply {
        properties.update(name, configure)
    }

    protected infix fun <Value> OptionalPropertyDescriptor<Value>.schema(configure: JsonSchema.() -> Unit) = apply {
        properties.update(property.name, configure)
    }
}


