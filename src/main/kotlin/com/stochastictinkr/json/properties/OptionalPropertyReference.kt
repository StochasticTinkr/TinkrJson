package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

class OptionalPropertyReference<D : OptionalDescriptor<*, *>>(
    private val jsonObject: JsonObject,
    private val key: String,
    val descriptor: D,
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

fun <K : Any, V : K?, D : OptionalDescriptor<K, V>> OptionalPropertyReference<D>.set(value: V?) {
    element = value?.let(descriptor.toKotlinType.converter.reverse::transform) ?: JsonNull
}

@JvmName("getOptionalPropertyOrNull")
fun <K : Any, D : OptionalDescriptor<K, *>> OptionalPropertyReference<D>.getOrNull(): K? {
    return element?.unlessNull(descriptor::forward)
}

@JvmName("createOrGetNullableOptionalProperty")
fun <K : Any, D : OptionalDescriptor<K, K?>> OptionalPropertyReference<D>.createOrGet(): K? {
    return createOrGetElement().unlessNull(descriptor::forward)
}

@JvmName("createOrGetNonNullOptionalProperty")
fun <K : Any, D : OptionalDescriptor<K, K>> OptionalPropertyReference<D>.createOrGet(): K {
    return descriptor.forward(createOrGetElement())
}

@JvmName("foldNullableOptionalProperty")
inline fun <K : Any, D : OptionalDescriptor<K, K?>, R> OptionalPropertyReference<D>.fold(
    ifPresent: (K?) -> R,
    ifAbsent: () -> R,
): R = element?.let { ifPresent(it.unlessNull(descriptor::forward)) } ?: ifAbsent()

@JvmName("foldNonNullOptionalProperty")
inline fun <K : Any, D : OptionalDescriptor<K, K>, R> OptionalPropertyReference<D>.fold(
    ifPresent: (K) -> R,
    ifAbsent: () -> R,
): R = element?.let { ifPresent(descriptor.forward(it)) } ?: ifAbsent()
