package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.parsing.*
import com.stochastictinkr.json.properties.*
import org.intellij.lang.annotations.*
import kotlin.test.*

class JsonSchemaTests {

    @Test
    fun `test string schema json`() {
        AssertThat(JsonSchema().apply {
            type = "string"
        }) buildsTo """ { "type": "string" } """
    }

    @Test
    fun `test metadata json`() {
        AssertThat(JsonSchema().apply {
            type = "string"
            title = "String Schema"
            description = "A schema for strings"
            default = JsonString("default")
            examples = jsonArray {
                add("example1")
                add("example2")
            }
        }
        ) buildsTo """
            {
                "type": "string",
                "title": "String Schema",
                "description": "A schema for strings",
                "default": "default",
                "examples": ["example1", "example2"]
            }
            """
    }

    @Test
    fun `test integer schema json`() {
        AssertThat(JsonSchema().apply {
            type = "integer"
            intProperties.set(
                minimum = 0,
                maximum = 100
            )
        }
        ) buildsTo """
            {
                "type": "integer",
                "minimum": 0,
                "maximum": 100
            }
            """
    }

    @Test
    fun `test array schema json`() {
        AssertThat(JsonSchema().apply {
            type = "array"
            items.set(
                JsonSchema().apply {
                    type = "string"
                }
            )
            minItems = 1
            uniqueItems = true
        }
        ) buildsTo """
            {
                "type": "array",
                "items": {
                    "type": "string"
                },
                "minItems": 1,
                "uniqueItems": true
            }
            """
    }

    @Test
    fun `test object schema json`() {
        AssertThat(
            JsonSchema().apply {
                type = "object"
                properties.set(JsonSchema.ObjectProperties().also {
                    it["name"] = JsonSchema().apply {
                        type = "string"
                    }
                    it["age"] = JsonSchema().apply {
                        type = "integer"
                        intProperties.set(minimum = 0)
                    }
                })
                required.element = jsonArray { add("name") }
            }
        ) buildsTo """
            {
                "type": "object",
                "properties": {
                    "name": { "type": "string" },
                    "age": { "type": "integer", "minimum": 0 }
                },
                "required": ["name"]
            }
            """
    }

    private infix fun AssertThat<out JsonSchema>.buildsTo(
        @Language("JSON")
        expected: String,
    ) = assertEquals(
        expected = JsonParser.jsonElement(expected),
        actual = value.jsonObject,
    )

    private data class AssertThat<T>(val value: T)
}
