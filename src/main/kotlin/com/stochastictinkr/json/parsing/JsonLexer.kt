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

    private var nextToken: Token? = null

    /**
     * Peeks at the current character without advancing the position.
     *
     * @return The current character or null if at the end of input.
     */
    private fun peek(): Char? = if (position < input.length) input[position] else null

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
    private fun skipWhitespace() {
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
    private fun match(expected: Char): Boolean {
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
    private fun takeStringValue(): String {
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
    private fun takeNumber(): JsonNumber {
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
    private fun takeLiteral(expected: String, result: JsonLiteral): JsonLiteral {
        val currentColumn = column
        for (char in expected) {
            require(match(char)) { "Expected '$expected' at $line:$currentColumn" }
        }
        return result
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
     * Peeks at the next token in the input.
     *
     * @return The next token.
     */
    fun peekToken(): Token = nextToken ?: takeToken().also { nextToken = it }

    /**
     * Takes the next token from the input.
     *
     * @return The next token.
     */
    fun takeToken(): Token {
        nextToken?.let {
            nextToken = null
            return it
        }
        skipWhitespace()
        val type = when (peek()) {
            null -> EndOfInput
            '{' -> ObjectStart.also { position++ }
            '}' -> ObjectEnd.also { position++ }
            '[' -> ArrayStart.also { position++ }
            ']' -> ArrayEnd.also { position++ }
            ',' -> Comma.also { position++ }
            ':' -> Colon.also { position++ }
            '"' -> StringLiteral(takeStringValue())
            '+', '-', in '0'..'9' -> JsonLiteral("number", takeNumber())
            't' -> takeLiteral("true", JsonLiteral("true", JsonBoolean(true)))
            'f' -> takeLiteral("false", JsonLiteral("false", JsonBoolean(false)))
            'n' -> takeLiteral("null", JsonLiteral("null", JsonNull))
            else -> unmatched("'\"', number, 'true', 'false', 'null', '{', '}', '[', ']', or ','")
        }
        return Token(type, line, column)
    }
}

internal data class Token(val type: TokenType, val line: Int, val column: Int) {
    fun expected(vararg tokenType: TokenType): TokenType {
        if (type !in tokenType) {
            val expectString = buildString {
                if (tokenType.size > 1) {
                    append("one of ")
                    append(tokenType.dropLast(1).joinToString(", ") { "'${it}'" })
                    append(", or '${tokenType.last()}'")
                } else {
                    append("'${tokenType.first()}'")
                }
            }
            expectedInstead(expectString)
        }
        return type
    }

    fun expectedInstead(expected: String): Nothing =
        throw IllegalArgumentException("Unexpected '$type'. Expected $expected at $line:$column")
}

internal sealed interface TokenType

internal data object ArrayStart : TokenType {
    override fun toString() = "["
}

internal data object ArrayEnd : TokenType {
    override fun toString() = "]"
}

internal data object ObjectStart : TokenType {
    override fun toString() = "{"
}

internal data object ObjectEnd : TokenType {
    override fun toString() = "}"
}

internal data object Comma : TokenType {
    override fun toString() = ","
}

internal data object Colon : TokenType {
    override fun toString() = ":"
}

internal data class StringLiteral(val value: String) : TokenType {
    override fun toString() = "\""
}

internal data class JsonLiteral(val stringValue: String, val value: JsonElement) : TokenType {
    override fun toString() = stringValue
}

internal data object EndOfInput : TokenType
