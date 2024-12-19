package com.stochastictinkr.json

import kotlin.test.*

class JsonElementTest {
    @Test
    fun `jsonObjectOrNull on JsonObject`() {
        val jsonObject = JsonObject()
        val result = jsonObject.jsonObjectOrNull
        assertSame(jsonObject, result)
    }

    @Test
    fun `jsonObjectOrNull not on JsonObject`() {
        val jsonElement = JsonNull
        val result = jsonElement.jsonObjectOrNull
        assertNull(result)
    }

    @Test
    fun `jsonArrayOrNull on JsonArray`() {
        val jsonArray = JsonArray()
        val result = jsonArray.jsonArrayOrNull
        assertSame(jsonArray, result)
    }

    @Test

    fun `jsonArrayOrNull not on JsonArray`() {
        val jsonElement = JsonNull
        val result = jsonElement.jsonArrayOrNull
        assertNull(result)
    }

    @Test
    fun `stringOrNull on JsonString`() {
        val jsonString = JsonString("hello")
        val result = jsonString.stringOrNull
        assertEquals("hello", result)
    }

    @Test
    fun `stringOrNull not on JsonString`() {
        val jsonElement = JsonNull
        val result = jsonElement.stringOrNull
        assertNull(result)
    }

    @Test
    fun `numberOrNull on JsonNumber short`() {
        val jsonNumber = JsonNumber(123.toShort())
        val result = jsonNumber.numberOrNull
        assertEquals(123, result)
    }

    @Test
    fun `numberOrNull on JsonNumber int`() {
        val jsonNumber = JsonNumber(123)
        val result = jsonNumber.numberOrNull
        assertEquals(123, result)
    }

    @Test
    fun `numberOrNull on JsonNumber long`() {
        val jsonNumber = JsonNumber(123L)
        val result = jsonNumber.numberOrNull
        assertEquals(123L, result)
    }

    @Test
    fun `numberOrNull on JsonNumber float`() {
        val jsonNumber = JsonNumber(123.45f)
        val result = jsonNumber.numberOrNull
        assertEquals(123.45f, result)
    }

    @Test
    fun `numberOrNull on JsonNumber double`() {
        val jsonNumber = JsonNumber(1.2e205)
        val result = jsonNumber.numberOrNull
        assertEquals(1.2e205, result)
    }

    @Test
    fun `numberOrNull not on JsonNumber`() {
        val jsonElement = JsonNull
        val result = jsonElement.numberOrNull
        assertNull(result)
    }

    @Test
    fun `booleanOrNull on JsonBoolean true`() {
        val jsonBoolean = JsonBoolean(true)
        val result = jsonBoolean.booleanOrNull
        assertEquals(true, result)
    }

    @Test
    fun `booleanOrNull on JsonBoolean false`() {
        val jsonBoolean = JsonBoolean(false)
        val result = jsonBoolean.booleanOrNull
        assertEquals(false, result)
    }

    @Test
    fun `booleanOrNull not on JsonBoolean`() {
        val jsonElement = JsonNull
        val result = jsonElement.booleanOrNull
        assertNull(result)
    }

    @Test
    fun `jsonObject on JsonObject`() {
        val jsonObject = JsonObject()
        val result = jsonObject.jsonObject
        assertSame(jsonObject, result)
    }

    @Test
    fun `jsonObject not on JsonObject`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.jsonObject
        }
    }

    @Test
    fun `jsonArray on JsonArray`() {
        val jsonArray = JsonArray()
        val result = jsonArray.jsonArray
        assertSame(jsonArray, result)
    }

    @Test
    fun `jsonArray not on JsonArray`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.jsonArray
        }
    }

    @Test
    fun `string on JsonString`() {
        val jsonString = JsonString("hello")
        val result = jsonString.string
        assertEquals("hello", result)
    }

    @Test
    fun `string not on JsonString`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.string
        }
    }

    @Test
    fun `number on JsonNumber`() {
        val jsonNumber = JsonNumber(123)
        val result = jsonNumber.number
        assertEquals(123, result)
    }

    @Test
    fun `int on JsonNumber`() {
        val jsonNumber = JsonNumber(123)
        val result = jsonNumber.int
        assertEquals(123, result)
    }

    @Test
    fun `long on JsonNumber`() {
        val jsonNumber = JsonNumber(123L)
        val result = jsonNumber.long
        assertEquals(123L, result)
    }

    @Test
    fun `float on JsonNumber`() {
        val jsonNumber = JsonNumber(123.45f)
        val result = jsonNumber.float
        assertEquals(123.45f, result)
    }

    @Test
    fun `double on JsonNumber`() {
        val jsonNumber = JsonNumber(1.2e205)
        val result = jsonNumber.double
        assertEquals(1.2e205, result)
    }

    @Test
    fun `number not on JsonNumber`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.number
        }
    }

    @Test
    fun `int not on JsonNumber`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.int
        }
    }

    @Test
    fun `long not on JsonNumber`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.long
        }
    }

    @Test
    fun `float not on JsonNumber`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.float
        }
    }

    @Test
    fun `double not on JsonNumber`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.double
        }
    }

    @Test
    fun `boolean on JsonBoolean`() {
        val jsonBoolean = JsonBoolean(true)
        val result = jsonBoolean.boolean
        assertEquals(true, result)
    }

    @Test
    fun `boolean not on JsonBoolean`() {
        val jsonElement = JsonNull
        assertFailsWith<IllegalStateException> {
            jsonElement.boolean
        }
    }

    @Test
    fun isNull() {
        assertTrue(JsonNull.isNull)
        assertFalse(JsonBoolean(true).isNull)
        assertFalse(JsonNumber(123).isNull)
        assertFalse(JsonString("hello").isNull)
        assertFalse(JsonArray().isNull)
        assertFalse(JsonObject().isNull)
    }

    @Test
    fun `deepCopy happy path`() {
        val original = JsonObject(
            mapOf(
                "nested" to JsonObject(mapOf("key" to JsonString("value"))),
                "array" to JsonArray(listOf(JsonNumber(1), JsonNumber(2)))
            )
        )
        val copy = original.deepCopy()
        assertNotSame(original, copy)
        assertEquals(original, copy)
    }

    @Test
    fun `deepCopy self reference`() {
        val array = JsonArray()
        array.add(array)
        assertFailsWith<IllegalArgumentException> {
            array.deepCopy()
        }
    }

    @Test
    fun `deepCopy nested self reference`() {
        val array = JsonArray()
        val nested = JsonObject()
        array.add(nested)
        nested["array"] = array
        assertFailsWith<IllegalArgumentException> {
            array.deepCopy()
        }
    }

    @Test
    fun `deepCopy duplicate descendants become separate instances`() {
        val array = jsonArray {
            add(1)
            add(2)
        }
        val input = jsonObject {
            "key1"(array)
            "key2"(array)
        }

        // Validate precondition
        assertSame(input["key1"], input["key2"])

        val copy = input.deepCopy()

        // Validate postcondition
        assertEquals(input, copy)
        assertNotSame(copy["key1"], copy["key2"])
    }
}