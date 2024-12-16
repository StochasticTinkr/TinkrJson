package com.stochastictinkr.json.schema

import com.stochastictinkr.json.JsonObject
import com.stochastictinkr.json.wrapper.*

class Example(wrapped: JsonObject) : Compound(wrapped) {
    val foo by Schema.foo
    val bar by Schema.bar
    val baz by Schema.baz
    val qux by Schema.qux
    val quux by Schema.quux
    val quuxString by Schema.quuxString
    val quuxInt by Schema.quuxInt
    val quuxObj by Schema.quuxObj


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
        val quuz by Schema.quuz

        companion object Schema : CompoundClass<ExampleSubObject>({ ExampleSubObject(it) }) {
            val quuz = string("quuz")
        }
    }
}

