package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

class OptionalPropertyReference<Value>(
    private val jsonObject: JsonObject,
    private val key: String,
    val descriptor: TypeDescriptor<Value, *>,
    private val createDefault: (() -> JsonElement)? = null,
) {
    val isPresent: Boolean
        get() = jsonObject.containsKey(key)

    fun remove() {
        jsonObject.remove(key)
    }

    var element: JsonElement?
        get() = jsonObject[key]
        set(value) {
            jsonObject.setNonNull(key, value)
        }

    fun createOrGetElement(): JsonElement {
        requireNotNull(createDefault) { "Cannot set an optional property without a default value" }
        return jsonObject.getOrPut(key, createDefault)
    }
}

@JvmName("setNonNullOptionalProperty")
fun <K : Any> OptionalPropertyReference<K>.set(value: K?) {
    element = value?.let(descriptor.converter::reverse)
}

@JvmName("setNullableOptionalProperty")
fun <K : Any> OptionalPropertyReference<K?>.set(value: K?) {
    element = descriptor.converter.reverse(value)
}

@JvmName("getOptionalPropertyOrNull")
fun <K> OptionalPropertyReference<K>.getOrNull(): K? {
    return element?.unlessNull(descriptor.converter::forward)
}

@JvmName("createOrGetNullableOptionalProperty")
fun <K : Any> OptionalPropertyReference<K?>.createOrGet(): K? {
    return createOrGetElement().unlessNull(descriptor.converter::forward)
}

@JvmName("createOrGetNonNullOptionalProperty")
fun <K : Any> OptionalPropertyReference<K>.createOrGet(): K {
    return descriptor.converter.forward(createOrGetElement())
}

@JvmName("foldNullableOptionalProperty")
inline fun <K : Any, R> OptionalPropertyReference<K?>.fold(
    ifPresent: (K?) -> R,
    ifAbsent: () -> R,
): R = element?.let { ifPresent(it.unlessNull(descriptor.converter::forward)) } ?: ifAbsent()

@JvmName("foldNonNullOptionalProperty")
inline fun <K : Any, R> OptionalPropertyReference<K>.fold(
    ifPresent: (K) -> R,
    ifAbsent: () -> R,
): R = element?.let { ifPresent(descriptor.converter.forward(it)) } ?: ifAbsent()
