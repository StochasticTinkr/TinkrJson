package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.parsing.*
import org.intellij.lang.annotations.*
import kotlin.test.*

class JsonSchemaTests {

    @Test
    fun `string schema json`() {
        AssertThat(jsonSchema {
            string {
                description = "A string schema"
            }
        }) buildsTo """ { "type": "string", "description": "A string schema" } """
    }

    @Test
    fun `test common properties`() {
        AssertThat(jsonSchema {
            common {
                type = "string"
                title = "String Schema"
                description = "A schema for strings"
                default = JsonString("default")
                examples = jsonArray {
                    add("example1")
                    add("example2")
                }
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
        AssertThat(jsonSchema {
            integer {
                minimum = 0
                maximum = 100
            }
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
        AssertThat(jsonSchema {
            array {
                items {
                    string()
                }
                minItems = 1
                uniqueItems = true
            }
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
            jsonSchema {
                obj {
                    property("name") { string() }
                    property("age") {
                        integer {
                            minimum = 0
                        }
                    }
                    required("name")
                }
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
