package com.stochastictinkr.json.schema

import com.stochastictinkr.json.JsonObject
import com.stochastictinkr.json.wrapper.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*


class Example(wrapped: JsonObject) : Compound(wrapped) {
    companion object Schema : CompoundClass<Example>({ Example(it) }) {
        val foo = string("foo")
        val bar = int("bar")
        val baz = ExampleSubObject("baz")
        val qux = jsonArray("qux").withItems(string)
        val quux = union("quux")
        val quuxString = quux[string].nullable()
        val quuxInt = quux[int]
        val quuxObj = quux[jsonObject]
    }

    class ExampleSubObject(wrapped: JsonObject) : Compound(wrapped) {
        companion object Schema : CompoundClass<ExampleSubObject>({ ExampleSubObject(it) }) {
            val quuz = string("quuz")
        }
    }
}

private val prettyJson = Json { prettyPrint = true }

fun main() {
    val schemaObject = Example.schema.toSchema()
    prettyJson.encodeToString(serializer(), schemaObject).also(::println)
}