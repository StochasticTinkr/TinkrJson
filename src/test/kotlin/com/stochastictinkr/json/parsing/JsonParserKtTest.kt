package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*
import org.junit.jupiter.api.*
import kotlin.test.*
import kotlin.test.Test

class JsonParserTests {

    @Test
    fun testParseString() {
        val result = parseJson(
            """
              "hello"
            """
        )
        assertEquals(JsonString("hello"), result)
    }

    @Test
    fun testParseNumber() {
        val result = parseJson("123")
        assertEquals(JsonNumber(123), result)
    }

    @Test
    fun testParseFloat() {
        val result = parseJson("123.45")
        assertEquals(JsonNumber(123.45f), result)
    }

    @Test
    fun testParseBooleanTrue() {
        val result = parseJson("true")
        assertEquals(JsonBoolean(true), result)
    }

    @Test
    fun testParseBooleanFalse() {
        val result = parseJson("false")
        assertEquals(JsonBoolean(false), result)
    }

    @Test
    fun testParseNull() {
        val result = parseJson("null")
        assertEquals(JsonNull, result)
    }

    @Test
    fun testParseEmptyObject() {
        val result = parseJson("{}")
        assertEquals(JsonObject(), result)
    }

    @Test
    fun testParseObjectWithValues() {
        val result = parseJson(
            """{
                "key": "value",
                "number": 42,
                "bool": true
              }"""
        )

        assertEquals(
            jsonObject {
                "key"("value")
                "number"(42)
                "bool"(true)
            }, result
        )
    }

    @Test
    fun testParseEmptyArray() {
        val input = "[]"
        val result = parseJson(input)
        assertEquals(JsonArray(), result)
    }

    @Test
    fun testParseArrayWithValues() {
        val result = parseJson("""["value", 42, true, null]""")
        assertEquals(
            JsonArray(
                listOf(
                    JsonString("value"),
                    JsonNumber(42),
                    JsonBoolean(true),
                    JsonNull
                )
            ), result
        )
    }

    @Test
    fun testParseNestedObject() {
        val result = parseJson("""{"nested": {"key": "value"}}""")
        assertEquals(
            JsonObject(
                mapOf(
                    "nested" to JsonObject(
                        mapOf(
                            "key" to JsonString("value")
                        )
                    )
                )
            ), result
        )
    }

    @Test
    fun testParseNestedArray() {
        val result = parseJson("[[1, 2], [3, 4]]")
        assertEquals(
            JsonArray(
                listOf(
                    JsonArray(listOf(JsonNumber(1), JsonNumber(2))),
                    JsonArray(listOf(JsonNumber(3), JsonNumber(4)))
                )
            ), result
        )
    }

    @Test
    fun testSyntaxErrorInvalidJson() {
        assertThrows<IllegalArgumentException> {
            parseJson("""{"key": "value"""")
        }
    }

    @Test
    fun testSyntaxErrorUnexpectedCharacter() {
        assertThrows<IllegalArgumentException> {
            parseJson("@")
        }
    }

    @Test
    fun testParseComplexJson() {
        val result = parseJson("""{ "array": [ 1, { "key": "value" }, true ], "object": { "nested": [ null, 42 ] } }""")
        assertEquals(
            JsonObject(
                mapOf(
                    "array" to JsonArray(
                        listOf(
                            JsonNumber(1),
                            JsonObject(mapOf("key" to JsonString("value"))),
                            JsonBoolean(true)
                        )
                    ),
                    "object" to JsonObject(
                        mapOf(
                            "nested" to JsonArray(listOf(JsonNull, JsonNumber(42)))
                        )
                    )
                )
            ), result
        )
    }
}
