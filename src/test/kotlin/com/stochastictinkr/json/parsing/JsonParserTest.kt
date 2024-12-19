package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*
import org.intellij.lang.annotations.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class JsonParserTests {

    @Test
    fun testParseString() {
        val result = JsonParser.jsonElement(
            """
              "hello"
            """
        )
        assertEquals(JsonString("hello"), result)
    }

    @Test
    fun testParseNumber() {
        val result = JsonParser.jsonElement("123")
        assertEquals(JsonNumber.Companion(123), result)
    }

    @Test
    fun testParseFloat() {
        val result = JsonParser.jsonElement("123.45")
        assertEquals(JsonNumber.Companion(123.45f), result)
    }

    @Test
    fun testParseBooleanTrue() {
        val result = JsonParser.jsonElement("true")
        assertEquals(JsonBoolean.Companion(true), result)
    }

    @Test
    fun testParseBooleanFalse() {
        val result = JsonParser.jsonElement("false")
        assertEquals(JsonBoolean.Companion(false), result)
    }

    @Test
    fun testParseNull() {
        val result = JsonParser.jsonElement("null")
        assertEquals(JsonNull, result)
    }

    @Test
    fun testParseEmptyObject() {
        val result = JsonParser.jsonElement("{}")
        assertEquals(JsonObject(), result)
    }

    @Test
    fun testParseObjectWithValues() {
        val result = JsonParser.jsonElement(
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
        val result = JsonParser.jsonElement(input)
        assertEquals(JsonArray(), result)
    }

    @Test
    fun testParseArrayWithValues() {
        val result = JsonParser.jsonElement("""["value", 42, true, null]""")
        assertEquals(
            JsonArray(
                listOf(
                    JsonString("value"),
                    JsonNumber.Companion(42),
                    JsonBoolean.Companion(true),
                    JsonNull
                )
            ), result
        )
    }

    @Test
    fun testParseNestedObject() {
        val result = JsonParser.jsonElement("""{"nested": {"key": "value"}}""")
        assertEquals(jsonObject { "nested" { "key"("value") } }, result)
    }

    @Test
    fun testParseNestedArray() {
        val result = JsonParser.jsonElement("[[1, 2], [3, 4]]")
        val expected = jsonArray {
            addArray {
                add(1)
                add(2)
            }
            addArray {
                add(3)
                add(4)
            }
        }
        assertEquals(
            expected, result
        )
    }

    @Test
    fun testSyntaxErrorInvalidJson() {
        assertThrows<IllegalArgumentException> {
            @Language("text")
            val input = """{"key": "value""""
            JsonParser.jsonElement(input)
        }
    }

    @Test
    fun testSyntaxErrorUnexpectedCharacter() {
        assertThrows<IllegalArgumentException> {
            @Language("text")
            val input = "@"
            JsonParser.jsonElement(input)
        }
    }

    @Test
    fun testParseComplexJson() {
        val input = """
                |{
                |  "array": [
                |    1,
                |    {
                |      "key": "value"
                |    },
                |    true
                |  ],
                |  "object": {
                |    "nested": [
                |      null,
                |      42
                |    ]
                |  }
                |}
                |
            """.trimMargin()

        val expected = jsonObject {
            "array"[{
                add(1)
                addObject {
                    "key"("value")
                }
                add(true)
            }]
            "object" {
                "nested"[{
                    add(null)
                    add(42)
                }]
            }
        }

        assertEquals(expected, JsonParser.jsonElement(input))
    }
}