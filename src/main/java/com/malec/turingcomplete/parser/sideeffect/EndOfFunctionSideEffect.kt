package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class EndOfFunctionSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "MAXLOCALS"
    },
    effect = { statement ->
        listOf(
            ParserAction.AddVarCount(statement.last().toInt())
        )
    }
)