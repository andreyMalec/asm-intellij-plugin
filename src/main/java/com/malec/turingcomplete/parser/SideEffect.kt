package com.malec.turingcomplete.parser

open class SideEffect(
    val query: ParserState.(line: List<String>) -> Boolean,
    private val effect: ParserState.(line: List<String>) -> List<ParserAction>
) {
    operator fun invoke(state: ParserState, line: List<String>) = effect(state, line)
}