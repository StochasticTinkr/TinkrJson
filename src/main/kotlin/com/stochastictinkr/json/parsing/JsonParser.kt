package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*
import org.intellij.lang.annotations.Language

data object JsonParser {
    /**
     * Parses a JSON input and returns a JsonElement.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonElement
     * @throws IllegalArgumentException if the input is not valid JSON
     */
    fun jsonElement(
        @Language("JSON") input: CharSequence,
    ): JsonElement = parseElement(JsonLexer(input))

    /**
     * Parses a JSON input and returns a JsonObject.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonObject
     * @throws IllegalArgumentException if the input is not a valid JSON object
     */
    fun jsonObject(
        @Language("JSON") input: CharSequence,
    ): JsonObject = parseObject(JsonLexer(input))

    /**
     * Parses a JSON input and returns a JsonArray.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonArray
     * @throws IllegalArgumentException if the input is not a valid JSON array
     */
    fun jsonArray(
        @Language("JSON") input: CharSequence,
    ): JsonArray = parseArray(JsonLexer(input))

    /**
     * Parses a JSON input and returns a JsonString.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonString
     * @throws IllegalArgumentException if the input is not a valid JSON string
     */
    fun jsonString(
        @Language("JSON") input: CharSequence,
    ): JsonString = JsonLexer(input).run {
        val token = takeToken()
        if (token.type !is StringLiteral) {
            token.expectedInstead("string literal")
        }
        JsonString(token.type.value)
    }

    /**
     * Parses a JSON input and returns a JsonNumber.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonNumber
     * @throws IllegalArgumentException if the input is not a valid JSON number
     */
    fun jsonNumber(
        @Language("JSON") input: CharSequence,
    ): JsonNumber = JsonLexer(input).run {
        val token = takeToken()
        (token.type as? JsonLiteral)?.value?.jsonNumberOrNull ?: token.expectedInstead("number")
    }

    /**
     * Parses a JSON input and returns a JsonBoolean.
     *
     * @param input the JSON input as a CharSequence
     * @return the parsed JsonBoolean
     * @throws IllegalArgumentException if the input is not a valid JSON boolean
     */
    fun jsonBoolean(
        @Language("JSON") input: CharSequence,
    ): JsonBoolean = when (input) {
        "true" -> JsonBoolean(true)
        "false" -> JsonBoolean(false)
        else -> throw IllegalArgumentException("Expected 'true' or 'false' at 1:1")
    }

    private val parseElement: DeepRecursiveFunction<JsonLexer, JsonElement> = DeepRecursiveFunction { lexer ->
        val token = lexer.peekToken()
        when (val type = token.type) {
            is StringLiteral -> JsonString(type.value).also { lexer.takeToken() }
            is JsonLiteral -> type.value.also { lexer.takeToken() }
            ObjectStart -> parseObject.callRecursive(lexer)
            ArrayStart -> parseArray.callRecursive(lexer)
            else -> lexer.unmatched("JSON element")
        }
    }

    private val parseObject: DeepRecursiveFunction<JsonLexer, JsonObject> = DeepRecursiveFunction { lexer ->
        lexer.takeToken().expected(ObjectStart)
        val map = mutableMapOf<String, JsonElement>()
        var first = true
        do {
            val keyToken = lexer.takeToken()
            if (keyToken.type !is StringLiteral) {
                if (first && keyToken.type == ObjectEnd) {
                    break
                }
                first = false
                keyToken.expectedInstead("object key starting with '\"'")
            }
            lexer.takeToken().expected(Colon)
            map[keyToken.type.value] = parseElement.callRecursive(lexer)
            val next = lexer.takeToken()
            when (next.type) {
                Comma -> continue
                ObjectEnd -> break
                else -> next.expectedInstead("',' or '}'")
            }
        } while (true)
        JsonObject(map)
    }

    private val parseArray: DeepRecursiveFunction<JsonLexer, JsonArray> = DeepRecursiveFunction { lexer ->
        lexer.takeToken().expected(ArrayStart)
        val list = mutableListOf<JsonElement>()
        var first = true
        do {
            if (first && lexer.peekToken().type == ArrayEnd) {
                lexer.takeToken()
                break
            }
            list.add(parseElement.callRecursive(lexer))
            val next = lexer.takeToken()
            when (next.type) {
                Comma -> continue
                ArrayEnd -> break
                else -> next.expectedInstead("',' or ']'")
            }
        } while (true)
        JsonArray(list)
    }
}
