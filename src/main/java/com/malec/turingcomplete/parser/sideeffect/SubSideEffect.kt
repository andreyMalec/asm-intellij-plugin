package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.SUB
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class SubSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "ISUB"
    },
    effect = { _ ->
        val dst = register(reg)
        val (a, b) = arguments.peek2()
        if (a !is Argument.Register)
            throw IllegalArgumentException()
        listOf(
            ParserAction.Pop,
            ParserAction.Pop,
            ParserAction.Instruction(SUB(dst, a, b)),
            ParserAction.Push(dst),
            ParserAction.AddRegCount(-2)
        )
    }
)