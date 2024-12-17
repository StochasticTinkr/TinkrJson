package com.stochastictinkr.json.output

import com.stochastictinkr.json.*

sealed class JsonWriter(private val pretty: Boolean) {
    companion object Compact : JsonWriter(false) {
        operator fun invoke(pretty: Boolean) = if (pretty) Pretty else Compact
    }

    fun write(json: JsonRoot, output: Appendable) = write(json.jsonElement, output)
    fun write(json: JsonElement, output: Appendable) {
        val style = if (pretty) PrettyJson(output, 0) else CompactJson(output)
        var head: ParentProgress<*>? = RootProgress(json, style)
        while (head != null) {
            when (val element = head.advance()) {
                is JsonObject -> {
                    head.checkCycle(element)
                    head = ObjectProgress(element, head)
                    head.writeStart()
                }

                is JsonArray -> {
                    head.checkCycle(element)
                    head = ArrayProgress(element, head)
                    head.writeStart()
                }

                is JsonLiteral -> style.writeLiteral(element)
                null -> head = head.next
            }
        }
    }

    fun writeToString(json: JsonRoot) = writeToString(json.jsonElement)
    fun writeToString(json: JsonElement) = buildString { write(json, this) }

    data object Pretty : JsonWriter(true)

    private fun ParentProgress<*>.checkCycle(current: JsonElement?) {
        require(generateSequence(this) { it.next }.none { it.json === current }) { "Circular reference detected" }
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
                is JsonBoolean -> output.append(literal.value.toString())
                is JsonNumber -> output.append(literal.toNumber().toString())
                is JsonString -> writeString(literal.value)
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

    private sealed class ParentProgress<J : JsonElement>(
        val json: J?,
        val style: JsonStyler,
        val next: ParentProgress<*>?,
    ) {
        constructor(json: J, next: ParentProgress<*>) : this(json, next.style.nextIndent(), next)

        abstract fun writeStart()
        abstract fun advance(): JsonElement?
    }

    private class RootProgress(
        val rootElement: JsonElement,
        style: JsonStyler,
    ) : ParentProgress<JsonElement>(null, style, null) {
        var first = true
        override fun writeStart() {
            error("RootProgress should not write start")
        }

        override fun advance() =
            if (first) {
                first = false
                rootElement
            } else {
                null
            }
    }

    private class ObjectProgress(
        json: JsonObject,
        next: ParentProgress<*>,
    ) : ParentProgress<JsonObject>(json, next) {
        val keys: Iterator<Map.Entry<String, JsonElement>> = json.iterator()
        var first = true

        override fun writeStart() {
            style.append('{')
        }

        override fun advance() = if (keys.hasNext()) {
            if (first) {
                first = false
                style.nextLineWithIndent()
            } else {
                style.elementSeparator()
            }
            val (key, value) = keys.next()
            style.writeString(key)
            style.keySeparator()
            value
        } else {
            if (!first) {
                style.nextLineWithIndent(-1)
            }
            style.append('}')
            null
        }
    }

    private class ArrayProgress(
        json: JsonArray,
        next: ParentProgress<*>,
    ) : ParentProgress<JsonArray>(json, next) {
        val elements: Iterator<JsonElement> = json.iterator()
        var first = true

        override fun writeStart() {
            style.append('[')
        }

        override fun advance() =
            if (elements.hasNext()) {
                if (first) {
                    first = false
                    style.nextLineWithIndent()
                } else {
                    style.elementSeparator()
                }
                elements.next()
            } else {

                if (!first) {
                    style.nextLineWithIndent(-1)
                }
                style.append(']')
                null
            }
    }
}
