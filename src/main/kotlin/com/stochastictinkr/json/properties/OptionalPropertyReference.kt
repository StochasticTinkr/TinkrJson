package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*

class OptionalPropertyReference<D : OptionalDescriptor<*, *>>(
    private val jsonObject: JsonObject,
    private val key: String,
    val descriptor: D,
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
}

fun <K : Any, V : K?, D : OptionalDescriptor<K, V>> OptionalPropertyReference<D>.set(value: V?) {
    element = value?.let(descriptor.toKotlinType.converter.reverse::transform) ?: JsonNull
}

@JvmName("getOptionalPropertyOrNull")
fun <K : Any, D : OptionalDescriptor<K, *>> OptionalPropertyReference<D>.getOrNull(): K? {
    return element?.unlessNull(descriptor::forward)
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
