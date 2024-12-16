package com.stochastictinkr.json.output

import com.stochastictinkr.json.*

fun writeJson(json: JsonRoot, output: Appendable) {
    writeJson(json.jsonElement, output)
}

fun jsonToString(json: JsonRoot) = jsonToString(json.jsonElement)

fun jsonToString(json: JsonElement) = buildString { writeJson(json, this) }

fun writeJson(json: JsonElement, output: Appendable) {
    val stack = ArrayDeque<ParentProgress>()
    stack.add(RootProgress(json))
    while (stack.isNotEmpty()) {
        val next = stack.last().advance(output)
        when (val current = next.jsonElement) {
            is JsonObject -> {
                checkCycle(stack, current)
                output.append('{')
                stack.add(ObjectProgress(current))
            }

            is JsonArray -> {
                checkCycle(stack, current)
                output.append('[')
                stack.add(ArrayProgress(current))
            }

            is JsonLiteral -> {
                writeLiteral(current, output)
            }

            null -> stack.removeLast()
        }
    }
}

private fun checkCycle(
    stack: ArrayDeque<ParentProgress>,
    current: JsonElement?,
) {
    require(stack.none { it.json === current }) { "Circular reference detected" }
}

private fun writeString(string: String, output: Appendable) {
    output.append('"')
    for (char in string) {
        when (char) {
            '\\' -> output.append("\\\\")
            '"' -> output.append("\\\"")
            '\b' -> output.append("\\b")
            '\u000C' -> output.append("\\f")
            '\n' -> output.append("\\n")
            '\r' -> output.append("\\r")
            '\t' -> output.append("\\t")
            in '\u0000'..'\u001F' -> output.append("\\u").append(char.code.toString(16).padStart(4, '0'))
            else -> output.append(char)
        }
    }
    output.append('"')
}

private fun writeLiteral(literal: JsonLiteral, output: Appendable) {
    when (literal) {
        is JsonNull -> output.append("null")
        is JsonBoolean -> output.append(literal.value.toString())
        is JsonNumber -> output.append(literal.toNumber().toString())
        is JsonString -> writeString(literal.value, output)
    }
}

private class NextProgress(
    val jsonElement: JsonElement?,
)

private sealed interface ParentProgress {
    val json: JsonElement?
    fun advance(output: Appendable): NextProgress
}

private data class RootProgress(
    val rootElement: JsonElement,
) : ParentProgress {
    override val json = null
    var first = true
    override fun advance(output: Appendable) =
        if (first) {
            first = false
            NextProgress(rootElement)
        } else {
            NextProgress(null)
        }
}

private data class ObjectProgress(
    override val json: JsonObject,
) : ParentProgress {
    val keys: Iterator<Map.Entry<String, JsonElement>> = json.iterator()
    var first = true

    override fun advance(output: Appendable): NextProgress {
        if (keys.hasNext()) {
            if (first) {
                first = false
            } else {
                output.append(',')
            }
            val (key, value) = keys.next()
            writeString(key, output)
            output.append(':')
            return NextProgress(value)
        }
        output.append('}')
        return NextProgress(null)
    }
}

private data class ArrayProgress(
    override val json: JsonArray,
) : ParentProgress {
    val elements: Iterator<JsonElement> = json.iterator()
    var first = true

    override fun advance(output: Appendable): NextProgress {
        if (elements.hasNext()) {
            if (first) {
                first = false
            } else {
                output.append(',')
            }
            return NextProgress(elements.next())
        }
        output.append(']')
        return NextProgress(null)
    }
}

