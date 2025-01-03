package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*
import kotlin.reflect.*

open class JsonObjectWrapper(val jsonObject: JsonObject = JsonObject()) {
    operator fun <Value> TypeDescriptor<Value, *>.invoke(name: String) =
        PropertyDescriptor(name, this)

    @JvmName("wrappedJsonObject")
    operator fun <Value : JsonObjectWrapper> TypeDescriptor<JsonObject, *>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
    ) =
        PropertyDescriptor(name, this wrapped wrap)

    @JvmName("wrappedJsonArrayOfJsonObjectWrapper")
    operator fun <Value : JsonObjectWrapper> TypeDescriptor<JsonArray, *>.invoke(
        wrap: (JsonObject) -> Value,
        name: String,
    ) = PropertyDescriptor(name, this wrappedItems wrap)

    operator fun <Value> TypeDescriptor<JsonArray, *>.invoke(
        itemType: TypeDescriptor<Value, *>,
        name: String,
    ) = PropertyDescriptor(name, this wrappedItems itemType)

    operator fun <Value> RequiredPropertyDescriptor<Value>.getValue(
        thisRef: Any?,
        unused: KProperty<*>,
    ): Value {
        val element =
            jsonObject[property.name] ?: throw NoSuchElementException("Property ${property.name} is required.")
        return property.type.converter.forward(element)
    }

    operator fun <Value> RequiredPropertyDescriptor<Value>.setValue(
        thisRef: Any?,
        unused: KProperty<*>,
        value: Value,
    ) {
        jsonObject[property.name] = property.type.converter.reverse(value)
    }

    operator fun <Value> OptionalPropertyDescriptor<Value>.getValue(
        thisRef: Any?,
        unused: KProperty<*>,
    ): Value? {
        val element = jsonObject[property.name]
        return element?.let(property.type.converter::forward)
    }

    operator fun <Value> OptionalPropertyDescriptor<Value>.setValue(
        thisRef: Any?,
        unused: KProperty<*>,
        value: Value?,
    ) {
        jsonObject.setNonNull(property.name, value?.let(property.type.converter::reverse))
    }

    operator fun <Value> ByReference<Value>.getValue(
        thisRef: Any?,
        unused: KProperty<*>,
    ) = run {
        val property = optionalProperty.property
        val type = property.type
        OptionalPropertyReference(jsonObject, property.name, type) { type.converter.reverse(type.createDefault()) }
    }
}

data class PropertyDescriptor<K>(
    val name: String,
    val type: TypeDescriptor<K, *>,
) {
    fun optional() = OptionalPropertyDescriptor(this)
    fun required() = RequiredPropertyDescriptor(this)
}

data class RequiredPropertyDescriptor<K>(val property: PropertyDescriptor<K>)
data class OptionalPropertyDescriptor<K>(val property: PropertyDescriptor<K>) {
    fun byRef() = ByReference(this)
}

data class ByReference<K>(val optionalProperty: OptionalPropertyDescriptor<K>)

fun <K : Any> PropertyDescriptor<K>.nullable() = PropertyDescriptor(name, type.nullable())
