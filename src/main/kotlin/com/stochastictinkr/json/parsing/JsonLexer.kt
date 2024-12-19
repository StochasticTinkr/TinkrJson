package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*

/**
 * A lexer for JSON input.
 *
 * @property input The input character sequence to be lexed.
 */
internal class JsonLexer(private val input: CharSequence) {
    var position: Int = 0
        private set
    var line: Int = 1
        private set
    var column: Int = 1
        private set

    /**
     * Peeks at the current character without advancing the position.
     *
     * @return The current character or null if at the end of input.
     */
    fun peek(): Char? = if (position < input.length) input[position] else null

    /**
     * Advances the position by one character, if possible.
     *
     * @return The current character or null if at the end of input.
     */
    private fun advanceOrNull(): Char? = peek()?.also {
        position++
        column++
    }

    /**
     * Advances the position by one character, or calls the provided function if at the end of input.
     *
     * @param onEof The function to call if at the end of input.
     * @return The current character.
     */
    private fun advance(onEof: () -> Nothing): Char = advanceOrNull() ?: onEof()

    /**
     * Skips over any whitespace characters.
     */
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

    /**
     * Matches the current character against the expected character.
     *
     * @param expected The character to match.
     * @return True if the current character matches the expected character, false otherwise.
     */
    fun match(expected: Char): Boolean {
        if (peek() == expected) {
            advanceOrNull()
            return true
        }
        return false
    }

    /**
     * Parses a JSON string value.
     *
     * @return The parsed string value.
     */
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

    /**
     * Parses a JSON escape sequence.
     *
     * @return The parsed escape character.
     */
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

    /**
     * Parses a Unicode escape sequence.
     *
     * @return The parsed Unicode character.
     */
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

    /**
     * Parses a JSON number.
     *
     * @return The parsed number as a JsonNumber.
     */
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

    /**
     * Parses a JSON literal.
     *
     * @param expected The expected literal string.
     * @param result The result to return if the literal matches.
     * @return The parsed JSON element.
     */
    private fun parseLiteral(expected: String, result: JsonLiteral): JsonLiteral {
        val currentColumn = column
        for (char in expected) {
            require(match(char)) { "Expected '$expected' at $line:$currentColumn" }
        }
        return result
    }

    /**
     * Takes a JSON literal from the input.
     *
     * @return The parsed JSON element.
     */
    fun takeLiteral() = when (peek()) {
        '"' -> JsonString(parseStringValue())
        '+', '-', in '0'..'9' -> parseNumber()
        't' -> parseLiteral("true", JsonBoolean(true))
        'f' -> parseLiteral("false", JsonBoolean(false))
        'n' -> parseLiteral("null", JsonNull)
        else -> unmatched("'\"', number, 'true', 'false', or 'null'")
    }

    /**
     * Throws an exception for unmatched input.
     *
     * @param expected The expected input description.
     * @throws IllegalArgumentException Always thrown with a message indicating the unexpected input.
     */
    fun unmatched(expected: String): Nothing {
        val current = peek()
        val message = if (current == null) {
            "End of input"
        } else {
            "$current"
        }
        throw IllegalArgumentException("Unexpected $message. Expected $expected at $line:$column")
    }

    /**
     * Expects a specific JSON element.
     *
     * @param expected The expected element.
     * @return The parsed element.
     */
    fun <E> expect(expected: Expectation<E>): E = expected.run { this@JsonLexer() }
}

/**
 * An interface representing an expectation that can be invoked on a JsonLexer.
 *
 * @param E The type of the result produced by the expectation.
 */
internal interface Expectation<E> {
    /**
     * Invokes the expectation on the given JsonLexer.
     *
     * @receiver The JsonLexer on which the expectation is invoked.
     * @return The result of the expectation, if successful.
     * @throws IllegalArgumentException If the expectation is not met.
     */
    operator fun JsonLexer.invoke(): E
}

/**
 * Represents the one of the following tokens:
 * - An object start token `{`
 * - An array start token `[`
 * - A literal token
 */
internal sealed interface ElementStart : ElementStartOrArrayEnd {

    /**
     * An expectation that the next token is an element start.
     */
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

/**
 * Represents the one of the following tokens:
 * - A key in an object `"key":`
 * - The end of an object `}`
 */
internal sealed interface KeyOrObjectEnd {

    /**
     * An expectation that the next token is a key or the end of an object.
     */
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

/**
 * Represents the one of the following tokens:
 * - A comma `,`
 * - The end of an object `}`
 */
internal sealed interface CommaOrObjectEnd {

    /**
     * An expectation that the next token is a comma or the end of an object.
     */
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

/**
 * Represents the one of the following tokens:
 * - The end of an array `]`
 * - An element start token
 */
internal sealed interface ElementStartOrArrayEnd {

    /**
     * An expectation that the next token is an element start or the end of an array.
     */
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

/**
 * Represents the one of the following tokens:
 * - A comma `,`
 * - The end of an array `]`
 */
internal sealed interface CommaOrArrayEnd {

    /**
     * An expectation that the next token is a comma or the end of an array.
     */
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

/**
 * Represents a key in a JSON object. `"key":`
 */
internal data class Key(val key: String) : KeyOrObjectEnd {

    /**
     * Expects a string literal, followed by a colon, returning the Key token.
     */
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

/**
 * Represents the start of a JSON object `{`.
 */
internal data object ObjectStart : ElementStart, ElementStartOrArrayEnd

/**
 * Represents the end of a JSON object `}`.
 */
internal data object ObjectEnd : KeyOrObjectEnd, CommaOrObjectEnd

/**
 * Represents the start of a JSON array `[`.
 */
internal data object ArrayStart : ElementStart, ElementStartOrArrayEnd

/**
 * Represents the end of a JSON array `]`.
 */
internal data object ArrayEnd : CommaOrArrayEnd, ElementStartOrArrayEnd

/**
 * Represents a comma `,`.
 */
internal data object Comma : CommaOrObjectEnd, CommaOrArrayEnd

/**
 * Represents a JSON literal.
 *
 * @property literal The literal value.
 */
internal data class Literal(val literal: JsonLiteral) : ElementStart, ElementStartOrArrayEnd

/**
 * This is an expectation that there is no more non-whitespace input.
 */
internal data object EndOfInput : Expectation<Unit> {
    override fun JsonLexer.invoke() {
        skipWhitespace()
        check(peek() == null) { "Expected end of input at $line:$column" }
    }
}