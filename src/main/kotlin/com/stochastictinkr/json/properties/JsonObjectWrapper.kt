package com.stochastictinkr.json.properties

import com.stochastictinkr.json.*
import kotlin.reflect.*

open class JsonObjectWrapper(val jsonObject: JsonObject = JsonObject()) {
    protected operator fun <D : Descriptor<*, *, *>> D.invoke(name: String) = PropertyDescriptor(name, this)

    @JvmName("createRequiredNonNullProperty")
    protected operator fun <W : JsonObjectWrapper, D : RequiredNonNullDescriptor<JsonObject>> D.invoke(
        wrap: (JsonObject) -> W,
        name: String,
    ) = this(name).modify { wrapped(wrap) }

    @JvmName("createRequiredNullableProperty")
    protected operator fun <W : JsonObjectWrapper, D : NullableDescriptor<Required, JsonObject>> D.invoke(
        wrap: (JsonObject) -> W,
        name: String,
    ) = this(name).modify { wrapped(wrap) }

    @JvmName("createRequiredNonNullWrappedArrayProperty")
    protected operator fun <I : JsonObjectWrapper, D : RequiredNonNullDescriptor<JsonArray>> D.invoke(
        wrapItems: (JsonObject) -> I,
        name: String,
    ) = PropertyDescriptor(
        name, wrapped(
            Converter(
                {
                    JsonArrayWrapper(
                        ToKotlinType.JsonObjectType.converter then Converter(wrapItems),
                        it.jsonArray
                    )
                },
                { it.jsonArray })
        )
    )

    @JvmName("createOptionalNonNullProperty")
    protected operator fun <I : Any, E : RequiredDescriptor<I, I>, D : RequiredNonNullDescriptor<JsonArray>> D.invoke(
        items: E,
        name: String,
    ) = PropertyDescriptor(
        name, wrapped(
            Converter(
                { JsonArrayWrapper(items.toKotlinType.converter, it.jsonArray) },
                { it.jsonArray })
        )
    )

    // Required properties
    @Suppress("unused")
    @JvmName("getRequiredNonNullProperty")
    protected operator fun <K : Any, D : RequiredDescriptor<K, K>> PropertyDescriptor<D>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): K = descriptor.forward(jsonObject.getValue(name))

    @Suppress("unused")
    @JvmName("setRequiredNullableProperty")
    protected operator fun <K : Any, D : RequiredDescriptor<K, K?>> PropertyDescriptor<D>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): K? = jsonObject.getValue(name).unlessNull(descriptor::forward)

    @Suppress("unused")
    @JvmName("setRequiredNonNullProperty")
    protected operator fun <K : Any, D : RequiredDescriptor<K, K>> PropertyDescriptor<D>.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: K,
    ) {
        jsonObject[name] = descriptor.reverse(value)
    }

    @Suppress("unused")
    @JvmName("setRequiredNullableProperty")
    protected operator fun <K : Any, D : RequiredDescriptor<K, K?>> PropertyDescriptor<D>.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: K?,
    ) {
        jsonObject[name] = value?.let(descriptor::reverse) ?: JsonNull
    }

    // Optional properties
    @Suppress("unused")
    @JvmName("getOptionalPropertyOrNull")
    protected operator fun <K : Any, D : OptionalDescriptor<K, *>> PropertyDescriptor<D>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): K? = jsonObject[name]?.let(descriptor::forward)

    @Suppress("unused")
    @JvmName("setOptionalNonNullPropertyOrRemoveKey")
    protected operator fun <K : Any, D : OptionalDescriptor<K, K>> PropertyDescriptor<D>.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: K?,
    ) {
        jsonObject.setNonNull(name, value?.let(descriptor::reverse))
    }

    @Suppress("unused")
    @JvmName("setOptionalNullableProperty")
    protected operator fun <K : Any, D : OptionalDescriptor<K, K?>> PropertyDescriptor<D>.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: K?,
    ) {
        jsonObject[name] = value?.let(descriptor::reverse) ?: JsonNull
    }

    @Suppress("unused")
    @JvmName("getOptionalPropertyReference")
    protected operator fun <K : Any, V : K?, D : OptionalDescriptor<K, V>> ByReference<PropertyDescriptor<D>>.getValue(
        thisRef: Any?,
        property: Any?,
    ) = OptionalPropertyReference<D>(jsonObject, value.name, value.descriptor)
}

fun <K : Any, V : K?, D : OptionalDescriptor<K, V>> PropertyDescriptor<D>.byRef() = ByReference(this)
