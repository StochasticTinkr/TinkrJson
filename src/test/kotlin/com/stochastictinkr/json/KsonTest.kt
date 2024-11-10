package com.stochastictinkr.json

import kotlinx.serialization.json.*

var spouse: String? = null
var details: JsonObject? = kson {
    "name" /= "Jane Doe"
    "age" /= 42
    "isMarried" /= true
    "address" {
        "street" /= "123 Main St"
        "city" /= "Springfield"
        "zip" /= "12345"
    }
    array("phoneNumbers") {
        obj {
            "type" /= "home"
            "number" /= "123-456-7890"
        }
        obj {
            "type" /= "work"
            "number" /= "123-456-7890"
        }
    }
}

private val prettyJson = Json { prettyPrint = true }

fun main() {

    val obj = kson {
        "name" /= "John Doe"
        "age" /= 42
        "isMarried" /= spouse != null
        spouse?.field("spouse")
        details?.field("spouseDetails")
        "address" {
            "street" /= "123 Main St"
            "city" /= "Springfield"
            "zip" /= "12345"
        }
        array("phoneNumbers") {
            obj {
                "type" /= "home"
                "number" /= "123-456-7890"
            }
            obj {
                "type" /= "work"
                "number" /= "123-456-7890"
            }
        }
    }

    prettyJson.encodeToString(JsonObject.serializer(), obj).also(::println)
}

