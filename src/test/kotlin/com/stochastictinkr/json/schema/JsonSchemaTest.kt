package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.parsing.*
import org.intellij.lang.annotations.*
import kotlin.test.*

class JsonSchemaTests {

    @Test
    fun `test string schema json`() {
        AssertThat(StringSchema()) buildsTo """
            {
                "type": "string"
            }
            """
    }

    @Test
    fun `test metadata json`() {
        AssertThat(
            StringSchema().withMetadata(
                SchemaMetadata(
                    title = "String Schema",
                    description = "A schema for strings",
                    default = JsonString("default"),
                    examples = jsonArray {
                        add("example1")
                        add("example2")
                    }
                )
            )
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
        AssertThat(
            IntegerSchema(
                properties = IntProperties(minimum = 0, maximum = 100)
            )
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
        AssertThat(
            ArraySchema(
                items = StringSchema(),
                minItems = 1,
                uniqueItems = true
            )
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
            ObjectSchema(
                properties = mapOf(
                    "name" to StringSchema(),
                    "age" to IntegerSchema(properties = IntProperties(minimum = 0))
                ),
                required = listOf("name")
            )
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

    @Test
    fun `test boolean schema json`() {
        AssertThat(BooleanSchema()) buildsTo """
            {
                "type": "boolean"
            }
            """
    }

    @Test
    fun `test null schema json`() {
        AssertThat(NullSchema()) buildsTo """
            {
                "type": "null"
            }
            """
    }

    @Test
    fun `test composite schema json`() {
        AssertThat(
            CompositeSchema(
                allOf = listOf(StringSchema(), IntegerSchema(properties = IntProperties(minimum = 0))),
                anyOf = listOf(BooleanSchema(), NullSchema()),
                not = StringSchema()
            )
        ) buildsTo """
            {
                "type": "object",
                "allOf": [
                    { "type": "string" },
                    { "type": "integer", "minimum": 0 }
                ],
                "anyOf": [
                    { "type": "boolean" },
                    { "type": "null" }
                ],
                "not": { "type": "string" }
            }
            """
    }

    private infix fun AssertThat<out JsonSchema>.buildsTo(
        @Language("JSON")
        expected: String,
    ) = assertEquals(
        expected = JsonParser.jsonElement(expected),
        actual = value.toJson(),
    )

    private data class AssertThat<T>(val value: T)
}
