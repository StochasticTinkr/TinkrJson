package com.stochastictinkr.json.binding

import com.stochastictinkr.json.*
import com.stochastictinkr.json.properties.*
import com.stochastictinkr.json.schema.AssertThat
import com.stochastictinkr.json.schema.buildsTo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class JsonObjectClassTest {

    @Test
    fun `test schema`() {
        AssertThat(Person.schema) buildsTo """{
            "type": "object",
            "properties": {
              "name": {
                "type": "string",
                "minLength": 1,
                "title": "Name",
                "description": "The name of the person."
              },
              "age": { "type": "integer" },
              "favoriteColor": {
                "oneOf": [
                  { "type": "string" },
                  { "type": "null" }
                ],
                "title": "Favorite Color",
                "description": "The person's favorite color."
              }
            },
            "required": [ "name", "favoriteColor" ]
          }"""
    }

    @Test
    fun `test json object created`() {
        val person = Person()
        person.name = "Alice"
        person.age.set(null)
        person.favoriteColor = null

        AssertThat(person.jsonObject) buildsTo """{
            "name": "Alice",
            "favoriteColor": null
          }"""
    }

    @Test
    fun `test wrapped json object`() {
        val person = Person(jsonObject {
            "name"("Bob")
            "favoriteColor"("blue")
        })

        assertEquals("Bob", person.name)
        assertFalse { person.age.isPresent }
        assertEquals("blue", person.favoriteColor)
    }

}

private class Person(jsonObject: JsonObject) : JsonObjectWrapper(jsonObject) {
    var name by Schema.name
    val age by Schema.age.byRef()
    var favoriteColor by Schema.favoriteColor

    companion object Schema : JsonObjectClass<Person>(::Person) {
        val name = string("name") {
            minLength = 1
            title = "Name"
            description = "The name of the person."
        }
        val age = integer("age").optional()
        val favoriteColor = string("favoriteColor").nullable() schema {
            title = "Favorite Color"
            description = "The person's favorite color."
        }
    }
}