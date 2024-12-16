package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*
import org.intellij.lang.annotations.*

fun parseJson(
    @Language("JSON")
    input: CharSequence,
): JsonElement {
    return parseJson(JsonLexer(input))
}

private fun parseJson(input: JsonLexer): JsonElement {
    var state: State = InitialState
    do {
        state = state(input)
    } while (state !is FinalState)

    return state(input).element
}

private sealed interface State {
    operator fun invoke(input: JsonLexer): State
}

private data object InitialState : State {
    override fun invoke(input: JsonLexer) = elementState(input.expect(ElementStart)) {
        FinalState(it)
    }
}

private fun elementState(
    elementStart: ElementStart,
    onComplete: (JsonElement) -> State,
): State = when (elementStart) {
    is ObjectStart -> StartObjectState(onComplete)
    is ArrayStart -> StartArrayState(onComplete)
    is Literal -> onComplete(elementStart.literal)
}

private data class StartArrayState(val onComplete: (JsonArray) -> State) : State {
    override fun invoke(input: JsonLexer) = when (val result = input.expect(ElementStartOrArrayEnd)) {
        is ElementStart -> elementState(result) {
            val array = JsonArray()
            array.add(it)
            ArrayState(array, onComplete)
        }

        is ArrayEnd -> onComplete(JsonArray())
    }
}

private data class ArrayState(
    val jsonArray: JsonArray,
    val onComplete: (JsonArray) -> State,
) : State {
    override fun invoke(input: JsonLexer) =
        when (val elementStart = input.expect(CommaOrArrayEnd)) {
            is Comma -> elementState(input.expect(ElementStart)) {
                jsonArray.add(it)
                this
            }

            is ArrayEnd -> onComplete(jsonArray)
        }
}

private data class StartObjectState(val onComplete: (JsonObject) -> State) : State {
    override fun invoke(input: JsonLexer) = when (val result = input.expect(KeyOrObjectEnd)) {
        is Key -> KeyState(result.key, JsonObject(), onComplete)
        is ObjectEnd -> onComplete(JsonObject())
    }
}

private data class KeyState(
    val key: String,
    val jsonObject: JsonObject,
    val onComplete: (JsonObject) -> State,
) : State {
    override fun invoke(input: JsonLexer) =
        elementState(input.expect(ElementStart)) {
            jsonObject[key] = it
            when (input.expect(CommaOrObjectEnd)) {
                is Comma -> KeyState(input.expect(Key).key, jsonObject, onComplete)
                is ObjectEnd -> onComplete(jsonObject)
            }
        }
}

private data class FinalState(val element: JsonElement) : State {
    override fun invoke(input: JsonLexer): FinalState {
        input.expect(EndOfInput)
        return this
    }
}

