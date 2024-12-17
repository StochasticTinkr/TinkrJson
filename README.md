# TinkrJson

**TinkrJson** is a lightweight and expressive Kotlin library for working with JSON. It provides a type-safe JSON object model, parsing and printing capabilities, JSON Schema support, and easy property access for JSON types.

---

## Features

- **JSON Object Model**: Represent and manipulate JSON data as an intuitive Kotlin object graph.
- **JSON Parsing**: Parse JSON strings into a type-safe object model.
- **JSON Printing**: Convert your JSON objects back to well-formatted JSON strings.
- **JSON Schema**: Define and validate JSON schemas with a type-safe API.
- **Typesafe Property Access**: Wrap JSON objects in Kotlin classes for easy and safe property access.

---

## Installation

We have not yet published TinkrJson to a repository. For now, you can clone the repository and build the library locally.

---

## Quick Start

### 1. Building JSON Objects with the DSL

#### Creating JSON Objects

Use the `jsonObject` DSL to create JSON objects:

```kotlin
import com.stochastictinkr.json.*

fun main() {
    val obj = jsonObject {
        "name"("John Doe")
        "age"(25)
        "address" {
            "number"(123)
            "street"("Main Street")
            "city"("Anytown")
            "state"("AS")
            "zip"(12345)
        }
        "phoneNumbers"[{
            add("555-1234")
            add("555-5678")
        }]
        "favoriteColor"(null)
        "averageScore"(85.5)
    }
    println(JsonWriter().writeToString(obj))
}
```

**Resulting JSON:**

```json
{
  "name": "John Doe",
  "age": 25,
  "address": {
    "number": 123,
    "street": "Main Street",
    "city": "Anytown",
    "state": "AS",
    "zip": 12345
  },
  "phoneNumbers": ["555-1234", "555-5678"],
  "favoriteColor": null,
  "averageScore": 85.5
}
```

#### Creating JSON Arrays

Use the `jsonArray` DSL to create JSON arrays:

```kotlin
import com.stochastictinkr.json.*

fun main() {
    val arr = jsonArray {
        add("Hello")
        add(12.34)
        add(null)
        addObject {
            "The answer"(42)
            "The question"("What is the meaning of life, the universe, and everything?")
        }
        addArray {
            add("This is an array")
            add("With two elements")
        }
    }
    println(JsonWriter.Pretty.writeToString(arr))
}
```

**Resulting JSON:**

```json
[
  "Hello",
  12.34,
  null,
  {
    "The answer": 42,
    "The question": "What is the meaning of life, the universe, and everything?"
  },
  [
    "This is an array",
    "With two elements"
  ]
]
```

#### Creating JSON Roots

Use the `jsonRoot` DSL for root JSON elements:

```kotlin
import com.stochastictinkr.json.*

fun main() {
    val root = jsonRoot {
        setObject {
            "name"("John Doe")
            "age"(25)
        }
    }
    println(JsonWriter.Compact.writeToString(root))
}
```

**Resulting JSON:**

```json
{"name":"John Doe","age":25}
```

---

### 2. Parsing JSON

Parse JSON strings into a `JsonElement`:

```kotlin
import com.stochastictinkr.json.*
import com.stochastictinkr.json.parsing.*

fun main() {
    val jsonString = """
        {
            "name": "TinkrJson",
            "version": 1.0
        }
    """
    val json = parseJson(jsonString)
    println(JsonWriter.Pretty.writeToString(json))
}
```

**Resulting JSON:**

```json
{
  "name": "TinkrJson",
  "version": 1.0
}
```

---

## API Overview

### Core DSL Entry Points

- **`jsonObject`**: Creates JSON objects with a DSL.
- **`jsonArray`**: Creates JSON arrays with a DSL.
- **`jsonRoot`**: Creates root JSON elements.

### JSON Writers
- **`JsonWriter`**: Compact and pretty JSON writer for `JsonElement`.
    - `JsonWriter.Compact`: Outputs compact JSON.
    - `JsonWriter.Pretty`: Outputs pretty-formatted JSON.

### Parsing JSON
- **`parseJson`**: Parses a JSON string into a `JsonElement`.

### Core Classes
- **JsonObject**: Represents a JSON object.
- **JsonArray**: Represents a JSON array.
- **JsonRoot**: Root JSON container.

---

## Why TinkrJson?

- **Kotlin-Friendly**: Idiomatic Kotlin API with features like delegates and DSLs.
- **Lightweight**: Focused on simplicity without sacrificing power.
- **Type-Safety**: Safely access and validate JSON properties.
- **Flexible**: Works well for both simple and complex JSON data.

---

## Roadmap

- TBD.

---

## License

TinkrJson is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Contributing

We welcome contributions! Feel free to open issues or submit PRs.

---

## Author

TinkrJson is developed and maintained by Daniel Pitts, aka StochasticTinkr
