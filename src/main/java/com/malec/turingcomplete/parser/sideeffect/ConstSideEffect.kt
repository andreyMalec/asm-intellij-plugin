package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

private val regex = Regex("ICONST_(\\d+)")

class ConstSideEffect : SideEffect(
    query = { statement ->
        statement[0].matches(regex)
    },
    effect = { statement ->
        val value = regex.find(statement[0])?.groups?.get(1)?.value!!
        listOf(
            ParserAction.Push(value(value))
        )
    }
)