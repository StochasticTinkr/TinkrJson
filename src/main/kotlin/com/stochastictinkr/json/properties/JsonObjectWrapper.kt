package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*
import kotlin.reflect.*

open class JsonObjectWrapper(val jsonObject: JsonObject = JsonObject()) {
    operator fun <K : Any, V : K?> RequiredProperty<K, V>.getValue(thisRef: Any?, property: KProperty<*>): V {
        return converter.forward(jsonObject.getValue(name))
    }

    operator fun <K : Any, V : K?> RequiredProperty<K, V>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        jsonObject[name] = converter.reverse(value)
    }

    operator fun <K : Any, V : K?> OptionalProperty<K, V>.getValue(thisRef: Any?, property: KProperty<*>): V? {
        return jsonObject[name]?.let { converter.forward(it) }
    }

    operator fun <K : Any, V : K?> OptionalProperty<K, V>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        jsonObject.setNonNull(name, converter.reverse(value))
    }

    operator fun <K : Any> AsReference<PropertyDescriptor<*, K, K>>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): OptionalPropertyReference<K> {
        return OptionalPropertyReference(jsonObject, value.name, value.kotlinType.requiredNonNull)
    }

    operator fun <K : Any> AsReference<PropertyDescriptor<*, K, K?>>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): OptionalPropertyReference<K?> {
        return OptionalPropertyReference(jsonObject, value.name, value.kotlinType.requiredStoreNull)
    }
}
