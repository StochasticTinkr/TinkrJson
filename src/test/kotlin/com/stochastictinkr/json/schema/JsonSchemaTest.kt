package com.stochastictinkr.json.schema

import com.stochastictinkr.json.*
import kotlin.test.*

class JsonSchemaTest {
    @Test
    fun `test string schema json`() {
        AssertThat(StringSchema()) buildsTo {
            "type"("string")
        }
    }

    @Test
    fun `test string schema with metadata json`() {
        AssertThat(StringSchema().withMetadata(SchemaMetadata(
            title = "String Schema",
            description = "A schema for strings",
            default = JsonString("default"),
            examples = jsonArray {
                add("example1")
                add("example2")
            }
        ))) buildsTo {
            "type"("string")
            "title"("String Schema")
            "description"("A schema for strings")
            "default"("default")
            "examples"[{
                add("example1")
                add("example2")
            }]
        }
    }

    private infix fun <T : JsonSchema> AssertThat<T>.buildsTo(
        expected: JsonObject.() -> Unit,
    ) = assertEquals(
        expected = jsonObject(expected),
        actual = value.toJson(),
    )


    private data class AssertThat<T>(val value: T)
}