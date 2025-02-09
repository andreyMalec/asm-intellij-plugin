package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.*
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class IfSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "IFNE" || statement[0] == "IFEQ"
    },
    effect = { statement ->
        val actions = mutableListOf<ParserAction>()

        val r = arguments.peek()
        if (r !is Argument.Register)
            throw IllegalArgumentException()
        actions.add(ParserAction.Pop)
        actions.add(ParserAction.Instruction(CMP(r, Argument.Register.ZR)))
        actions.add(ParserAction.AddRegCount(-1))

        val label = label(statement[1])
        val a = when (statement[0]) {
            "IFNE" -> JNE(label)
            else -> JE(label)
        }
        actions.add(ParserAction.Instruction(a))
        actions.add(ParserAction.Push(register(reg - 1)))

        actions
    }
)