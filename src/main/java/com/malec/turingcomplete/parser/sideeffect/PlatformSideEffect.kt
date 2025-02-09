package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.IN
import com.malec.turingcomplete.ASM.OUT
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class PlatformSideEffect : SideEffect(
    query = { statement ->
        !handleInvoke && statement[0] == "INVOKESTATIC"
    },
    effect = effect@{ statement ->
        val actions = mutableListOf<ParserAction>()

        if (statement[1] == "framework/IoKt.output") {
            actions.add(ParserAction.Instruction(OUT(register(reg - 1))))
            return@effect actions
        }
        if (statement[1] == "framework/IoKt.input") {
            actions.add(ParserAction.Instruction(IN(movReg)))
            actions.add(ParserAction.Push(movReg))
            return@effect actions
        }

        actions.add(ParserAction.HandleInvoke)
        actions
    }
)