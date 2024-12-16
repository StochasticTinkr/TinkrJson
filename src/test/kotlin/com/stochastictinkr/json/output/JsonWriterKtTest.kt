package com.stochastictinkr.json.output

import com.stochastictinkr.json.*
import org.intellij.lang.annotations.*
import kotlin.test.*

class JsonWriterKtTest {
    @Test
    fun `all string escapes`() {
        val input = """
        |Quote: "
        |Backslash: \
        |Backspace: ${'\b'}
        |Form feed: ${'\u000C'}
        |Carriage return: CR
        |Tab: ${'\t'}
        """.trimMargin().replace("CR", "\r")

        @Language("JSON")
        val expected =
            """"Quote: \"\nBackslash: \\\nBackspace: \b\nForm feed: \f\nCarriage return: \r\nTab: \t""""
        val result = jsonToString(JsonString(input))
        assertEquals(expected, result)
    }

    @Test
    fun literals() {
        assertEquals("true", jsonToString(JsonBoolean(true)))
        assertEquals("false", jsonToString(JsonBoolean(false)))
        assertEquals("null", jsonToString(JsonNull))
        assertEquals("123", jsonToString(JsonNumber(123)))
        assertEquals("123.45", jsonToString(JsonNumber(123.45f)))
        assertEquals("1.2E205", jsonToString(JsonNumber(1.2e205)))
    }

    @Test
    fun `empty object`() {
        val input = JsonObject()
        val expected = "{}"
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `empty array`() {
        val input = JsonArray()
        val expected = "[]"
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `object with values`() {
        val input = JsonObject()
        input["key"] = JsonString("value")
        input["number"] = JsonNumber(123)
        input["array"] = JsonArray(JsonString("element"))
        val expected = """{"key":"value","number":123,"array":["element"]}"""
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `object with single value`() {
        val input = JsonObject()
        input["key"] = JsonString("value")
        val expected = """{"key":"value"}"""
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `array with values`() {
        val input = JsonArray()
        input.add(JsonString("value"))
        input.add(JsonNumber(123))
        input.add(JsonArray(JsonString("element")))
        val expected = """["value",123,["element"]]"""
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `array with single value`() {
        val input = JsonArray()
        input.add(JsonString("value"))
        val expected = """["value"]"""
        val result = jsonToString(input)
        assertEquals(expected, result)
    }

    @Test
    fun `cycles cause exception`() {
        val input = JsonObject()
        val array = JsonArray()
        input["array"] = array
        array.add(input)
        assertFailsWith<IllegalArgumentException> {
            jsonToString(input)
        }
    }
}