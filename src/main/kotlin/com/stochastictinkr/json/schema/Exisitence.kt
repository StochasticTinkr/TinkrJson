package com.stochastictinkr.json.schema

sealed interface Existence

data object Required : Existence

data object Optional : Existence
