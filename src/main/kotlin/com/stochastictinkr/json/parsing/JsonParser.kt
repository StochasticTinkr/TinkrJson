package com.stochastictinkr.json.parsing

import com.stochastictinkr.json.*
import org.intellij.lang.annotations.Language

/**
 * Parses a JSON input and returns a JsonElement.
 *
 * @param input the JSON input as a CharSequence
 * @return the parsed JsonElement
 * @throws IllegalArgumentException if the input is not valid JSON
 */
fun parseJson(
    @Language("JSON")
    input: CharSequence,
): JsonElement {
    return parseJson(JsonLexer(input))
}

/**
 * Parses a JSON input and returns a JsonElement.
 *
 * @param input the JSON input as a JsonLexer
 * @return the parsed JsonElement
 * @throws IllegalArgumentException if the input is not valid JSON
 */
private fun parseJson(input: JsonLexer): JsonElement {
    var state: State = InitialState
    do {
        state = state(input)
    } while (state !is FinalState)

    return state(input).element
}

/**
 * Represents the state of the JSON parser.  The parser is a state machine that reads tokens from
 * a JsonLexer and transitions between states based on the tokens it reads.
 *
 * When a state encounters a token it does not expect, it throws an exception.
 */
private sealed interface State {
    operator fun invoke(input: JsonLexer): State
}

/**
 * Transitions to the appropriate state based on the element start token.
 *
 * Transitions:
 * - [ObjectStart] -> [StartObjectState]`(onComplete)`
 * - [ArrayStart] -> [StartArrayState]`(onComplete)`
 * - [Literal] -> `onComplete(literalValue)`
 *
 * @param token the token
 * @param onComplete transition to the completion state when the element is complete
 */
private fun elementState(
    token: ElementStart,
    onComplete: (JsonElement) -> State,
): State = when (token) {
    is ObjectStart -> StartObjectState(onComplete)
    is ArrayStart -> StartArrayState(onComplete)
    is Literal -> onComplete(token.literal)
}

/**
 * The initial state of the JSON parser.
 *
 * Transitions:
 * - [ElementStart] -> `elementState(token)`
 */
private data object InitialState : State {
    override fun invoke(input: JsonLexer) =
        elementState(input.expect(ElementStart)) { FinalState(it) }
}

/**
 * The first state after starting an array.
 *
 * Transitions:
 * - [ElementStartOrArrayEnd] ->
 *     - [ElementStart] -> `elementState(token)` -> [ArrayState]
 *     - [ArrayEnd] -> `onComplete(emptyArray)`
 *
 * @param onComplete transition to the completion state when the array is complete
 */
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

/**
 * A state that adds additional elements to an array.
 *
 * Transitions:
 * - [CommaOrArrayEnd] ->
 *    - [ElementStart] -> `elementState(token)` { add element } -> [ArrayState]
 *    - [ArrayEnd] -> `onComplete(array)`
 *
 * @param jsonArray the array to add elements to
 * @param onComplete transition to the completion state when the array is complete
 */
private data class ArrayState(
    val jsonArray: JsonArray,
    val onComplete: (JsonArray) -> State,
) : State {
    override fun invoke(input: JsonLexer) =
        when (input.expect(CommaOrArrayEnd)) {
            is Comma -> elementState(input.expect(ElementStart)) {
                jsonArray.add(it)
                this
            }

            is ArrayEnd -> onComplete(jsonArray)
        }
}

/**
 * The first state after starting an object.
 *
 * Transitions:
 * - [KeyOrObjectEnd] ->
 *    - [Key] -> [KeyState]
 *    - [ObjectEnd] -> `onComplete(emptyObject)`
 *
 * @param onComplete transition to the completion state when the object is complete
 */
private data class StartObjectState(val onComplete: (JsonObject) -> State) : State {
    override fun invoke(input: JsonLexer) = when (val result = input.expect(KeyOrObjectEnd)) {
        is Key -> KeyState(result.key, JsonObject(), onComplete)
        is ObjectEnd -> onComplete(JsonObject())
    }
}

/**
 * The state that expects a value, and continues to add key-value pairs to the object.
 *
 * Transitions:
 * - [ElementStart] -> `elementState(token)` { add key-value pair } -> [CommaOrObjectEnd]
 *     - [Comma] -> [KeyState]
 *     - [ObjectEnd] -> `onComplete(jsonObject)`
 *
 * @param key the key of the current key-value pair
 * @param jsonObject the object to add key-value pairs to
 * @param onComplete transition to the completion state when the object is complete
 */
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

/**
 * The state that expects the end of the input, and holds the outermost element.
 *
 * Transitions:
 * - [EndOfInput] -> `onComplete`
 */
private data class FinalState(val element: JsonElement) : State {
    override fun invoke(input: JsonLexer): FinalState {
        input.expect(EndOfInput)
        return this
    }
}

