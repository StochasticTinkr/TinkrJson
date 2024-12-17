package com.stochastictinkr.json

@DslMarker
annotation class TinkrJsonDsl

/**
 * Creates a [JsonObject], optionally with a [block] to configure the object.
 *
 * Example usage:
 * ```kotlin
 * val obj = jsonObject {
 *    "name"("John Doe")
 *    "age"(25)
 *    "address" {
 *      "number"(123)
 *      "street"("Main Street")
 *      "city"("Anytown")
 *      "state"("AS")
 *      "zip"(12345)
 *    }
 *    "phoneNumbers"[{
 *      add("555-1234")
 *      add("555-5678")
 *    }]
 *    "favoriteColor"(null)
 *    "averageScore"(85.5)
 * }
 * ```
 *
 * Will result in the following JSON:
 * ```json
 * {
 *   "name": "John Doe",
 *   "age": 25,
 *   "address": {
 *     "number": 123,
 *     "street": "Main Street",
 *     "city": "Anytown",
 *     "state": "AS",
 *     "zip": 12345
 *   },
 *   "phoneNumbers": ["555-1234", "555-5678"]
 *   "favoriteColor": null
 *   "averageScore": 85.5
 * }
 * ```
 */
@TinkrJsonDsl
inline fun jsonObject(block: JsonObject.() -> Unit = {}): JsonObject {
    return JsonObject().apply(block)
}

/**
 * Creates a [JsonArray], optionally with a [block] to configure the array.
 *
 * Example usage:
 * ```kotlin
 * val arr = jsonArray {
 *   add("Hello")
 *   add(12.34)
 *   add(null)
 *   addObject {
 *      "The answer"(42)
 *      "The question"("What is the meaning of life, the universe, and everything?")
 *   }
 *   addArray {
 *      add("This is an array")
 *      add("With two elements")
 *   }
 * }
 * ```
 *
 * Will result in the following JSON:
 *
 * ```json
 * [
 *   "Hello",
 *   12.34,
 *   null,
 *   {
 *     "The answer": 42,
 *     "The question": "What is the meaning of life, the universe, and everything?"
 *   },
 *   [
 *     "This is an array",
 *     "With two elements"
 *   ]
 * ]
 * ```
 */
@TinkrJsonDsl
inline fun jsonArray(block: JsonArray.() -> Unit = {}): JsonArray {
    return JsonArray().apply(block)
}

/**
 * Creates a [JsonRoot], optionally with a [block] to configure the root.  The initial content of the root is JsonNull.
 *
 * ## Example usages:
 * ### Literal values:
 * ```kotlin
 * val root = jsonRoot {
 *    set("John Doe")
 * }
 * ```
 *
 * The value of `rool.jsonElement` will be a [JsonString] with the value "John Doe".
 * ### Object:
 * ```kotlin
 * val root = jsonRoot {
 *    setObject {
 *      "name"("John Doe")
 *      "age"(25)
 *    }
 * }
 * ```
 * The value of `root.jsonElement` will be a [JsonObject] with the following content:
 * ```json
 * {
 *    "name": "John Doe",
 *    "age": 25
 * }
 * ```
 */
@TinkrJsonDsl
inline fun jsonRoot(block: JsonRoot.() -> Unit = {}): JsonRoot {
    return JsonRoot().apply(block)
}