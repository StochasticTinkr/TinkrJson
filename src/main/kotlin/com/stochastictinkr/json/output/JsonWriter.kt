package com.stochastictinkr.json.output

import com.stochastictinkr.json.*
import com.stochastictinkr.json.walker.*

/**
 * Writes a [JsonRoot] or [JsonElement] to an [Appendable] or as a [String].
 */
sealed class JsonWriter(private val pretty: Boolean) {
    /**
     * A [JsonWriter] that writes JSON in a compact format, with no extra whitespace.
     */
    companion object Compact : JsonWriter(false) {
        /**
         * Returns a [JsonWriter] that writes JSON in either a compact or pretty format.
         * @param pretty `true` to write pretty JSON, `false` to write compact JSON.
         */
        operator fun invoke(pretty: Boolean = false) = if (pretty) Pretty else Compact
    }

    /**
     * A [JsonWriter] that writes JSON in a pretty format, with extra whitespace for readability.
     */
    data object Pretty : JsonWriter(true)

    /**
     * Writes the element from the [JsonRoot] to the [output].
     */
    fun write(json: JsonRoot, output: Appendable) = write(json.jsonElement, output)

    /**
     * Writes the [json] element to the [output].
     */
    fun write(json: JsonElement, output: Appendable) {
        writerFor(if (pretty) PrettyJson(output, 0) else CompactJson(output))(ElementStack(json))
    }

    /**
     * Returns the element from the [JsonRoot] as a JSON string.
     */
    fun writeToString(json: JsonRoot) = writeToString(json.jsonElement)

    /**
     * Returns the [json] element as a JSON string.
     */
    fun writeToString(json: JsonElement) = buildString { write(json, this) }

}


private sealed class JsonStyler(val output: Appendable) {
    abstract fun nextIndent(): JsonStyler
    abstract fun keySeparator()
    abstract fun elementSeparator()
    abstract fun nextLineWithIndent(relative: Int = 0)
    fun append(value: Char) = output.append(value)
    fun append(value: String) = output.append(value)

    fun writeString(string: String) {
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

    fun writeLiteral(literal: JsonLiteral) {
        when (literal) {
            is JsonNull -> output.append("null")
            is JsonBoolean -> output.append(literal.boolean.toString())
            is JsonNumber -> output.append(literal.number.toString())
            is JsonString -> writeString(literal.string)
        }
    }
}

private class CompactJson(output: Appendable) : JsonStyler(output) {
    override fun nextIndent() = this
    override fun keySeparator() {
        append(":")
    }

    override fun elementSeparator() {
        append(",")
    }

    override fun nextLineWithIndent(relative: Int) {}
}

private class PrettyJson(output: Appendable, private val level: Int) : JsonStyler(output) {
    override fun nextIndent() = PrettyJson(output, level + 1)

    override fun keySeparator() {
        append(": ")
    }

    override fun elementSeparator() {
        append(",\n")
        append("")
        append("    ".repeat(level))
    }

    override fun nextLineWithIndent(relative: Int) {
        append("\n")
        append("    ".repeat(level + relative))
    }
}

private fun writerFor(style: JsonStyler) =
    ElementVisitor { head ->
        when (val element = head.element) {
            is JsonLiteral -> style.writeLiteral(element)
            is JsonObject -> writeObject(style, element, head)
            is JsonArray -> writeArray(style, element, head)
        }
    }

private suspend fun ElementVisitorScope<*>.writeArray(
    style: JsonStyler,
    element: JsonArray,
    head: ElementStack,
) {
    style.append('[')
    if (element.isEmpty()) {
        style.append(']')
        return
    }
    val indented = style.nextIndent()
    val childWriter = writerFor(indented)
    element.forEachIndexed { index, value ->
        if (index == 0) {
            indented.nextLineWithIndent()
        } else {
            indented.elementSeparator()
        }
        childWriter.callRecursive(head.push(value))
    }
    style.nextLineWithIndent()
    style.append(']')
}

private suspend fun ElementVisitorScope<*>.writeObject(
    style: JsonStyler,
    element: JsonObject,
    head: ElementStack,
) {
    style.append('{')
    if (element.isEmpty()) {
        style.append('}')
        return
    }
    val indented = style.nextIndent()
    val childWriter = writerFor(indented)
    element.entries.forEachIndexed { index, (key, value) ->
        if (index == 0) {
            indented.nextLineWithIndent()
        } else {
            indented.elementSeparator()
        }
        indented.writeString(key)
        indented.keySeparator()
        childWriter.callRecursive(head.push(value))
    }
    style.nextLineWithIndent()
    style.append('}')
}