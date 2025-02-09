package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.LOAD
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class LoadSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "ILOAD"
    },
    effect = { statement ->
        val address = address(statement[1])
        val dst = register(reg)
        listOf(
            ParserAction.Instruction(LOAD(dst, address)),
            ParserAction.Push(dst),
            ParserAction.AddRegCount(1)
        )
    }
)