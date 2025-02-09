package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.ADD
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class AddSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "IADD"
    },
    effect = { _ ->
        val dst = register(reg)
        val a = register(reg - 2)
        val b = register(reg - 1)
        listOf(
            ParserAction.Instruction(ADD(dst, a, b)),
            ParserAction.Push(dst),
            ParserAction.AddRegCount(-2)
        )
    }
)