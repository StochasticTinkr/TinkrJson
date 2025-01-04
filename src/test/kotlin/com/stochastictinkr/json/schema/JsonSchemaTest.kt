package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import com.stochastictinkr.json.output.*
import com.stochastictinkr.json.parsing.*
import com.stochastictinkr.json.properties.JsonObjectWrapper
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

}

@JvmName("jsonObjectWrapperBuildsTo")
infix fun AssertThat<out JsonObjectWrapper>.buildsTo(
    @Language("JSON")
    expected: String,
) = AssertThat(value.jsonObject).buildsTo(expected)

@JvmName("jsonElementBuildsTo")
infix fun AssertThat<out JsonElement>.buildsTo(
    @Language("JSON")
    expected: String,
) {
    val expectedJson = JsonParser.jsonElement(expected)
    if (expectedJson != value) {
        val actualString = JsonWriter.Pretty.writeToString(value)
        val expectedString = JsonWriter.Pretty.writeToString(expectedJson)
        assertEquals(expectedString, actualString)
    }
}

data class AssertThat<T>(val value: T)
