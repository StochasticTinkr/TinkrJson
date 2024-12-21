import com.stochastictinkr.json.*

sealed interface JsonSchema {
    val metadata: SchemaMetadata
    val type: JsonSchemaType

    fun toJsonElement(): JsonElement
}

data class SchemaMetadata(
    val title: String? = null,
    val description: String? = null,
    val default: JsonElement? = null,
    val examples: JsonArray? = null
)

enum class JsonSchemaType {
    OBJECT, ARRAY, STRING, NUMBER, INTEGER, BOOLEAN, NULL
}

class ObjectSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    val properties: Map<String, JsonSchema> = emptyMap(),
    val required: List<String> = emptyList(),
    val additionalProperties: Boolean? = null,
    val patternProperties: Map<String, JsonSchema> = emptyMap(),
    val propertyNames: JsonSchema? = null,
    val dependencies: Map<String, Any> = emptyMap(),
    val minProperties: Int? = null,
    val maxProperties: Int? = null
) : JsonSchema {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT

    override fun toJsonElement(): JsonElement = jsonObject {
        "type"("object")
        "properties" {
            properties.forEach { (key, schema) ->
                key(schema.toJsonElement())
            }
        }
        "required"[{ addAll(required) }]
        additionalProperties?.let { "additionalProperties"(it) }
        minProperties?.let { "minProperties"(it) }
        maxProperties?.let { "maxProperties"(it) }
    }
}

class ArraySchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    val items: JsonSchema? = null,
    val additionalItems: JsonSchema? = null,
    val minItems: Int = 0,
    val maxItems: Int? = null,
    val uniqueItems: Boolean = false
) : JsonSchema {
    override val type: JsonSchemaType = JsonSchemaType.ARRAY

    override fun toJsonElement(): JsonElement = jsonObject {
        "type"("array")
        items?.let { "items"(it.toJsonElement()) }
        additionalItems?.let { "additionalItems"(it.toJsonElement()) }
        "minItems"(minItems)
        maxItems?.let { "maxItems"(it) }
        "uniqueItems"(uniqueItems)
    }
}

class PrimitiveSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    override val type: JsonSchemaType,
    val multipleOf: Number? = null,
    val minimum: Number? = null,
    val maximum: Number? = null,
    val exclusiveMinimum: Boolean = false,
    val exclusiveMaximum: Boolean = false,
    val minLength: Int = 0,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val format: String? = null
) : JsonSchema {
    override fun toJsonElement(): JsonElement = jsonObject {
        "type"(type.name.lowercase())
        multipleOf?.let { "multipleOf"(it) }
        minimum?.let { "minimum"(it) }
        maximum?.let { "maximum"(it) }
        if (exclusiveMinimum) "exclusiveMinimum"(true)
        if (exclusiveMaximum) "exclusiveMaximum"(true)
        "minLength"(minLength)
        maxLength?.let { "maxLength"(it) }
        pattern?.let { "pattern"(it) }
        format?.let { "format"(it) }
    }
}

class ReferenceSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    val reference: String
) : JsonSchema {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT

    override fun toJsonElement(): JsonElement = jsonObject {
        "\$ref"(reference)
    }
}

class CompositeSchema(
    override val metadata: SchemaMetadata = SchemaMetadata(),
    val allOf: List<JsonSchema> = emptyList(),
    val anyOf: List<JsonSchema> = emptyList(),
    val oneOf: List<JsonSchema> = emptyList(),
    val not: JsonSchema? = null
) : JsonSchema {
    override val type: JsonSchemaType = JsonSchemaType.OBJECT

    private fun JsonObject.setListProperty(name: String, list: List<JsonSchema>) {
        if (list.isNotEmpty()) {
            set(name, list.mapTo(JsonArray()) { it.toJsonElement() })
        }
    }
    override fun toJsonElement(): JsonElement = jsonObject {
        setListProperty("allOf", allOf)
        setListProperty("anyOf", anyOf)
        setListProperty("oneOf", oneOf)
        not?.let { "not"(it.toJsonElement()) }
    }
}
