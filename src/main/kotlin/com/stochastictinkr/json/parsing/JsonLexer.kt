package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*

internal class JsonLexer(private val input: CharSequence) {
    var position: Int = 0
        private set
    var line: Int = 1
        private set
    var column: Int = 1
        private set

    fun peek(): Char? = if (position < input.length) input[position] else null

    private fun advanceOrNull(): Char? = peek()?.also {
        position++
        column++
    }

    private fun advance(onEof: () -> Nothing): Char = advanceOrNull() ?: onEof()

    fun skipWhitespace() {
        while (true) {
            val ch = peek()
            when {
                ch == '\n' -> {
                    line++
                    column = 1
                    position++
                }

                ch?.isWhitespace() == true -> {
                    advanceOrNull()
                }

                else -> return
            }

        }
    }

    fun match(expected: Char): Boolean {
        if (peek() == expected) {
            advanceOrNull()
            return true
        }
        return false
    }

    fun parseStringValue(): String {
        val builder = StringBuilder()
        if (!match('"')) unmatched("'\"'")
        while (true) {
            when (val ch = advance { unmatched("string content") }) {
                '"' -> return builder.toString()
                '\\' -> builder.append(parseEscapeSequence())
                in '\u0000'..'\u001F' -> unmatched("valid string character")
                else -> builder.append(ch)
            }
        }
    }

    private fun parseEscapeSequence(): Char {
        return when (val ch = advance { unmatched("string content") }) {
            '"' -> '"'
            '\\' -> '\\'
            '/' -> '/'
            'b' -> '\b'
            'f' -> '\u000C'
            'n' -> '\n'
            'r' -> '\r'
            't' -> '\t'
            'u' -> parseUnicodeEscape()
            else -> unmatched("valid escape sequence")
        }
    }

    private fun parseUnicodeEscape(): Char {
        var value = 0
        repeat(4) {
            val digit = advance { unmatched("hex digit") }
            value = value * 16 + when (digit) {
                in '0'..'9' -> digit - '0'
                in 'a'..'f' -> digit - 'a' + 10
                in 'A'..'F' -> digit - 'A' + 10
                else -> unmatched("hex digit")
            }
        }
        return value.toChar()
    }

    private fun parseNumber(): JsonNumber {
        val start = position
        val startColumn = column
        match('-')
        when (peek()) {
            '0' -> position++
            in '1'..'9' -> while (peek()?.isDigit() == true) position++
            else -> unmatched("digit")
        }
        if (match('.')) {
            if (peek()?.isDigit() != true) {
                unmatched("digit")
            }
            while (peek()?.isDigit() == true) position++
        }
        if (match('e') || match('E')) {
            match('+') || match('-')
            if (peek()?.isDigit() != true) {
                unmatched("digit")
            }
            while (peek()?.isDigit() == true) position++
        }
        val numberString = input.substring(start, position)
        return numberString.toIntOrNull()?.toJsonNumber()
            ?: numberString.toLongOrNull()?.toJsonNumber()
            ?: numberString.toFloatOrNull()?.toJsonNumber()
            ?: numberString.toDoubleOrNull()?.toJsonNumber()
            ?: throw IllegalArgumentException("Invalid number at $line:$startColumn")
    }

    private fun parseLiteral(expected: String, result: JsonElement): JsonElement {
        val currentColumn = column
        for (char in expected) {
            require(match(char)) { "Expected '$expected' at $line:$currentColumn" }
        }
        return result
    }

    fun takeLiteral() = when (val ch = peek()) {
        '"' -> JsonString(parseStringValue())
        '+', '-', in '0'..'9' -> parseNumber()
        't' -> parseLiteral("true", JsonBoolean(true))
        'f' -> parseLiteral("false", JsonBoolean(false))
        'n' -> parseLiteral("null", JsonNull)
        else -> unmatched("'\"', number, 'true', 'false', or 'null'")
    }

    fun unmatched(expected: String): Nothing {
        val current = peek()
        val message = if (current == null) {
            "End of input"
        } else {
            "$current"
        }
        throw IllegalArgumentException("Unexpected $message. Expected $expected at $line:$column")
    }

    fun <E> expect(expected: Expectation<E>): E = expected.run { this@JsonLexer() }
}


internal interface Expectation<E> {
    operator fun JsonLexer.invoke(): E
}

internal sealed interface ElementStart : ElementStartOrArrayEnd {
    companion object : Expectation<ElementStart> {
        override fun JsonLexer.invoke(): ElementStart {
            skipWhitespace()
            return when {
                match('[') -> ArrayStart
                match('{') -> ObjectStart
                else -> Literal(takeLiteral())
            }
        }
    }
}

internal sealed interface KeyOrObjectEnd {
    companion object : Expectation<KeyOrObjectEnd> {
        override fun JsonLexer.invoke(): KeyOrObjectEnd {
            skipWhitespace()
            return when {
                match('}') -> ObjectEnd
                else -> {
                    val key = Key(parseStringValue())
                    skipWhitespace()
                    check(match(':')) { "Expected ':' at $line:$column" }
                    key
                }
            }
        }
    }
}

internal sealed interface CommaOrObjectEnd {
    companion object : Expectation<CommaOrObjectEnd> {
        override fun JsonLexer.invoke(): CommaOrObjectEnd {
            skipWhitespace()
            return when {
                match(',') -> Comma
                match('}') -> ObjectEnd
                else -> unmatched("',' or '}'")
            }
        }
    }
}

internal sealed interface ElementStartOrArrayEnd {
    companion object : Expectation<ElementStartOrArrayEnd> {
        override fun JsonLexer.invoke(): ElementStartOrArrayEnd {
            skipWhitespace()
            return when {
                match(']') -> ArrayEnd
                else -> expect(ElementStart)
            }
        }
    }
}

internal sealed interface CommaOrArrayEnd {
    companion object : Expectation<CommaOrArrayEnd> {
        override fun JsonLexer.invoke(): CommaOrArrayEnd {
            skipWhitespace()
            return when {
                match(',') -> Comma
                match(']') -> ArrayEnd
                else -> unmatched("',' or ']'")
            }
        }
    }
}

internal data class Key(val key: String) : KeyOrObjectEnd {
    companion object : Expectation<Key> {
        override fun JsonLexer.invoke(): Key {
            skipWhitespace()
            val key = Key(parseStringValue())
            skipWhitespace()
            check(match(':')) { "Expected ':' at $line:$column" }
            return key
        }
    }
}

internal data object ObjectStart : ElementStart, ElementStartOrArrayEnd {}
internal data object ObjectEnd : KeyOrObjectEnd, CommaOrObjectEnd
internal data object ArrayStart : ElementStart, ElementStartOrArrayEnd
internal data object ArrayEnd : CommaOrArrayEnd, ElementStartOrArrayEnd

internal data object Comma : CommaOrObjectEnd, CommaOrArrayEnd
internal data class Literal(val literal: JsonElement) : ElementStart, ElementStartOrArrayEnd

internal data object EndOfInput : Expectation<Unit> {
    override fun JsonLexer.invoke() {
        skipWhitespace()
        check(peek() == null) { "Expected end of input at $line:$column" }
    }
}