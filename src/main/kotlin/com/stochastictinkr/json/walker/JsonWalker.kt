package com.stochastictinkr.json.walker

import com.stochastictinkr.json.*

class ElementStack(
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

typealias ElementVisitor<R> = DeepRecursiveFunction<ElementStack, R>

typealias ElementVisitorScope<R> = DeepRecursiveScope<ElementStack, R>

operator fun <R> ElementVisitor<R>.invoke(element: JsonElement): R = this(ElementStack(element))