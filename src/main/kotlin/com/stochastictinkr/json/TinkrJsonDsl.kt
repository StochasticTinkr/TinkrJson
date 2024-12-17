package com.stochastictinkr.json

@DslMarker
annotation class TinkrJsonDsl

@TinkrJsonDsl
inline fun jsonObject(block: JsonObject.() -> Unit): JsonObject {
    return JsonObject().apply(block)
}

@TinkrJsonDsl
inline fun jsonArray(block: JsonArray.() -> Unit): JsonArray {
    return JsonArray().apply(block)
}

@TinkrJsonDsl
inline fun jsonRoot(block: JsonRoot.() -> Unit): JsonRoot {
    return JsonRoot().apply(block)
}