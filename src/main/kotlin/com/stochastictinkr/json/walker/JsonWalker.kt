package com.stochastictinkr.json.walker

import com.stochastictinkr.json.*

/**
 * A helper data structure for depth-first traversal of a JSON structure, with cycle detection.
 */
internal class ElementStack(
    val element: JsonElement,
    val parent: ElementStack? = null,
) {
    fun push(element: JsonElement): ElementStack {
        if (element !is JsonLiteral) {
            require(ancestors.none { it === element }) { "Cycle detected in JSON structure" }
        }
        return ElementStack(element, this)
    }

    private val ancestors: Sequence<JsonElement> = generateSequence(this) { it.parent }.map { it.element }
}

/**
 * A [DeepRecursiveFunction] for depth-first traversal of a JSON structure.
 *
 * Example usage:
 * ```
 * val countNulls = ElementVisitor<Int> { stack ->
 *     when (val element = stack.element) {
 *       is JsonNull -> 1
 *       is JsonObject -> stack.element.values.sumBy { callRecursive(stack.push(it)) }
 *       is JsonArray -> stack.element.sumBy { callRecursive(stack.push(it)) }
 *       else -> 0
 *     }
 * }
 *
 * fun example(element: JsonElement): Int {
 *    println("There are ${countNulls(element)} nulls in the JSON structure.")
 * }
 * ```
 */
internal typealias ElementVisitor<R> = DeepRecursiveFunction<ElementStack, R>
/**
 * A [DeepRecursiveScope] for depth-first traversal of a JSON structure.
 */
internal typealias ElementVisitorScope<R> = DeepRecursiveScope<ElementStack, R>

/**
 * Convenience function for invoking an [ElementVisitor] on the root [JsonElement].
 */
internal operator fun <R> ElementVisitor<R>.invoke(element: JsonElement): R = this(ElementStack(element))