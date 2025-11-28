package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.ADD
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class AddSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "IADD"
    },
    effect = { _ ->
        val dst = movReg
        val (a, b) = arguments.peek2()
        if (a !is Argument.Register)
            throw IllegalArgumentException()
        listOf(
            ParserAction.Pop,
            ParserAction.Pop,
            ParserAction.Instruction(ADD(movReg, a, b)),
            ParserAction.Push(dst),
            ParserAction.AddRegCount(-2)
        )
    }
)