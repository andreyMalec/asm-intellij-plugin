package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.SUB
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class SubSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "ISUB"
    },
    effect = { _ ->
        val dst = register(reg)
        val a = register(reg - 2)
        val b = register(reg - 1)
        listOf(
            ParserAction.Instruction(SUB(dst, a, b)),
            ParserAction.Push(dst),
            ParserAction.AddRegCount(-2)
        )
    }
)