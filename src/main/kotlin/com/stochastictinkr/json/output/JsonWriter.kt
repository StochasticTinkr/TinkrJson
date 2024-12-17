package com.stochastictinkr.json.output

import com.stochastictinkr.json.*

fun writeJson(json: JsonRoot, output: Appendable, pretty: Boolean = false) {
    writeJson(json.jsonElement, output, pretty)
}

fun jsonToString(json: JsonRoot, pretty: Boolean = false) = jsonToString(json.jsonElement, pretty)

fun jsonToString(json: JsonElement, pretty: Boolean = false) = buildString { writeJson(json, this, pretty) }

fun writeJson(json: JsonElement, output: Appendable, pretty: Boolean = false) {
    var head: ParentProgress<*>? = RootProgress(json, if (pretty) PrettyJson(0) else CompactJson)
    while (head != null) {
        val next = head.advance(output)
        val style = head.style
        when (val element = next.jsonElement) {
            is JsonObject -> {
                head.checkCycle(element)
                output.append('{')
                head = ObjectProgress(element, style.nextIndent(), head)
            }

            is JsonArray -> {
                head.checkCycle(element)
                output.append('[')
                head = ArrayProgress(element, style.nextIndent(), head)
            }

            is JsonLiteral -> writeLiteral(element, output)

            null -> head = head.next
        }
    }
}

private fun ParentProgress<*>.checkCycle(current: JsonElement?) {
    require(generateSequence(this) { it.next }.none { it.json === current }) { "Circular reference detected" }
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

private sealed class JsonStyle {
    abstract fun nextIndent(): JsonStyle
    abstract fun keySeparator(output: Appendable)
    abstract fun elementSeparator(output: Appendable)
    abstract fun indent(output: Appendable, relative: Int = 0)
}

private data object CompactJson : JsonStyle() {
    override fun nextIndent() = this
    override fun keySeparator(output: Appendable) {
        output.append(":")
    }

    override fun elementSeparator(output: Appendable) {
        output.append(",")
    }

    override fun indent(output: Appendable, relative: Int) {}
}

private data class PrettyJson(val level: Int) : JsonStyle() {
    override fun nextIndent() = PrettyJson(level + 1)
    override fun keySeparator(output: Appendable) {
        output.append(": ")
    }

    override fun elementSeparator(output: Appendable) {
        output.append(",\n")
        output.append("    ".repeat(level))
    }

    override fun indent(output: Appendable, relative: Int) {
        output.append("\n")
        output.append("    ".repeat(level + relative))
    }

}

private sealed class ParentProgress<J : JsonElement>(
    val json: J?,
    val style: JsonStyle,
    val next: ParentProgress<*>?,
) {
    abstract fun advance(output: Appendable): NextProgress
}

private class RootProgress(
    val rootElement: JsonElement,
    style: JsonStyle,
) : ParentProgress<JsonElement>(null, style, null) {
    var first = true
    override fun advance(output: Appendable) =
        if (first) {
            first = false
            NextProgress(rootElement)
        } else {
            NextProgress(null)
        }
}

private class ObjectProgress(
    json: JsonObject,
    style: JsonStyle,
    next: ParentProgress<*>,
) : ParentProgress<JsonObject>(json, style, next) {
    val keys: Iterator<Map.Entry<String, JsonElement>> = json.iterator()
    var first = true

    override fun advance(output: Appendable): NextProgress {
        if (keys.hasNext()) {
            if (first) {
                first = false
                style.indent(output)
            } else {
                style.elementSeparator(output)
            }
            val (key, value) = keys.next()
            writeString(key, output)
            style.keySeparator(output)
            return NextProgress(value)
        }
        if (!first) {
            style.indent(output, -1)
        }
        output.append('}')
        return NextProgress(null)
    }
}

private class ArrayProgress(
    json: JsonArray,
    style: JsonStyle,
    next: ParentProgress<*>,
) : ParentProgress<JsonArray>(json, style, next) {
    val elements: Iterator<JsonElement> = json.iterator()
    var first = true

    override fun advance(output: Appendable): NextProgress {
        if (elements.hasNext()) {
            if (first) {
                first = false
                style.indent(output)
            } else {
                style.elementSeparator(output)
            }
            return NextProgress(elements.next())
        }
        if (!first) {
            style.indent(output, -1)
        }
        output.append(']')
        return NextProgress(null)
    }
}
