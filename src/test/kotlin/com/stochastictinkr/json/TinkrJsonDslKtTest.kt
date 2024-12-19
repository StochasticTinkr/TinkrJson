package com.stochastictinkr.json

import org.junit.jupiter.api.Test
import kotlin.test.*

class TinkrJsonDslKtTest {
    @Test
    fun `jsonObject empty`() {
        val result = jsonObject { }
        assertEquals(JsonObject(), result)
    }

    @Test
    fun `jsonArray empty`() {
        val result = jsonArray { }
        assertEquals(JsonArray(), result)
    }

    @Test
    fun `jsonObject with values`() {
        val result = jsonObject {
            "key"("value")
            "number"(123)
            "array"[{
                add("element")
            }]
            "nested" {
                "key"("value")
            }
        }
        val expected = JsonObject()
        expected["key"] = JsonString("value")
        expected["number"] = JsonNumber(123)
        expected["array"] = JsonArray(JsonString("element"))
        expected["nested"] = jsonObject {
            "key"("value")
        }
        assertEquals(expected, result)
    }

    @Test
    fun `jsonArray with values`() {
        val result = jsonArray {
            add("value")
            add(123)
            add(true)
            add(null)
            add(234L)
            add(1.2e205)
            add(1.2f)
            addArray {
                add("element")
            }
            addObject {
                "key"("value")
            }
        }
        val expected = JsonArray(
            JsonString("value"),
            JsonNumber(123),
            JsonBoolean(true),
            JsonNull,
            JsonNumber(234L),
            JsonNumber(1.2e205),
            JsonNumber(1.2f),
            JsonArray(JsonString("element")),
            JsonObject("key" to JsonString("value"))
        )
        assertEquals(expected, result)
    }

    @Test
    fun `jsonArray set values`() {
        val result = jsonArray {
            repeat(9) {
                add(null)
            }
        }
        result[0] = "value"
        result[1] = 123
        result[2] = true
        result[3] = null
        result[4] = 234L
        result[5] = 1.2e205
        result[6] = 1.2f
        result[7] = jsonArray {
            add("element")
        }
        result[8] = jsonObject {
            "key"("value")
        }
        val expected = JsonArray(
            JsonString("value"),
            JsonNumber(123),
            JsonBoolean(true),
            JsonNull,
            JsonNumber(234L),
            JsonNumber(1.2e205),
            JsonNumber(1.2f),
            JsonArray(JsonString("element")),
            JsonObject("key" to JsonString("value"))
        )
        assertEquals(expected, result)
    }


    @Test
    fun `jsonRoot set string`() {
        val root = jsonRoot {
            set("value")
        }
        assertEquals(JsonString("value"), root.jsonElement)
    }

    @Test
    fun `jsonRoot set int`() {
        val root = jsonRoot {
            set(123)
        }
        assertEquals(JsonNumber(123), root.jsonElement)
    }

    @Test
    fun `jsonRoot set long`() {
        val root = jsonRoot {
            set(123L)
        }
        assertEquals(JsonNumber(123L), root.jsonElement)
    }

    @Test
    fun `jsonRoot set float`() {
        val root = jsonRoot {
            set(123.45f)
        }
        assertEquals(JsonNumber(123.45f), root.jsonElement)
    }

    @Test
    fun `jsonRoot set double`() {
        val root = jsonRoot {
            set(1.2e205)
        }
        assertEquals(JsonNumber(1.2e205), root.jsonElement)
    }

    @Test
    fun `jsonRoot set boolean`() {
        val root = jsonRoot {
            set(true)
        }
        assertEquals(JsonBoolean(true), root.jsonElement)
    }

    @Test
    fun `jsonRoot set null`() {
        val root = jsonRoot {
            setNull()
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable string to null`() {
        val root = jsonRoot {
            set(null as String?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable int to null`() {
        val root = jsonRoot {
            set(null as Int?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable long to null`() {
        val root = jsonRoot {
            set(null as Long?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable float to null`() {
        val root = jsonRoot {
            set(null as Float?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable double to null`() {
        val root = jsonRoot {
            set(null as Double?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable boolean to null`() {
        val root = jsonRoot {
            set(null as Boolean?)
        }
        assertEquals(JsonNull, root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable string`() {
        val root = jsonRoot {
            set("value2" as String?)
        }
        assertEquals(JsonString("value2"), root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable int`() {
        val root = jsonRoot {
            set(123 as Int?)
        }
        assertEquals(JsonNumber(123), root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable long`() {
        val root = jsonRoot {
            set(123L as Long?)
        }
        assertEquals(JsonNumber(123L), root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable float`() {
        val root = jsonRoot {
            set(123.45f as Float?)
        }
        assertEquals(JsonNumber(123.45f), root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable double`() {
        val root = jsonRoot {
            set(1.2e205 as Double?)
        }
        assertEquals(JsonNumber(1.2e205), root.jsonElement)
    }

    @Test
    fun `jsonRoot set nullable boolean`() {
        val root = jsonRoot {
            set(true as Boolean?)
        }
        assertEquals(JsonBoolean(true), root.jsonElement)
    }

    @Test
    fun `jsonRoot makeObject`() {
        val root = jsonRoot {
            makeObject {
                "key"("value")
                "number"(42)
                "bool"(true)
            }
        }
        assertEquals(
            jsonObject {
                "key"("value")
                "number"(42)
                "bool"(true)
            },
            root.jsonElement
        )
    }

    @Test
    fun `jsonRoot makeArray`() {
        val root = jsonRoot {
            makeArray {
                add("value")
                add(123)
                add(jsonArray {
                    add("element")
                })
            }
        }
        assertEquals(
            jsonArray {
                add("value")
                add(123)
                add(jsonArray {
                    add("element")
                })

            },
            root.jsonElement
        )
    }
}