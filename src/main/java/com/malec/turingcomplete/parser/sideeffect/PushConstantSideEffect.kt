package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class PushConstantSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "BIPUSH" || statement[0] == "SIPUSH"
    },
    effect = { statement ->
        listOf(
            ParserAction.Push(value(statement[1]))
        )
    }
)