package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.LABEL
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

private val lineMatcher = Regex("L(\\d+)")

class LineSideEffect : SideEffect(
    query = { statement ->
        statement[0].matches(lineMatcher)
    },
    effect = { statement ->
        val label = label(statement[0])
        listOf(
            ParserAction.Instruction(LABEL(label)),
            ParserAction.ClearArgs
        )
    }
)