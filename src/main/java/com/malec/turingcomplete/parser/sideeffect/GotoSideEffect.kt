package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.*
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class GotoSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "GOTO"
    },
    effect = { statement ->
        listOf(
            ParserAction.Instruction(JMP(label(statement[1])))
        )
    }
)