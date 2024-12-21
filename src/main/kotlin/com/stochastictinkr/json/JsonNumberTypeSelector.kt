package com.stochastictinkr.json

sealed interface JsonNumberTypeSelector<N : Number> {
    fun select(value: JsonElement): N
}

data object AsInt : JsonNumberTypeSelector<Int> {
    override fun select(value: JsonElement): Int = value.int
}

data object AsLong : JsonNumberTypeSelector<Long> {
    override fun select(value: JsonElement): Long = value.long
}

data object AsFloat : JsonNumberTypeSelector<Float> {
    override fun select(value: JsonElement): Float = value.float
}

data object AsDouble : JsonNumberTypeSelector<Double> {
    override fun select(value: JsonElement): Double = value.double
}

data object AsNumber : JsonNumberTypeSelector<Number> {
    override fun select(value: JsonElement): Number = value.number
}

