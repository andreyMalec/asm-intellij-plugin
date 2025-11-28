package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.LOAD
import com.malec.turingcomplete.ASM.RET
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class ReturnSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "RETURN" || statement[0] == "IRETURN"
    },
    effect = { _ ->
        val actions = mutableListOf<ParserAction>()
        val returnValue = asm.last().takeIf { it is LOAD } as? LOAD
        if (returnValue != null) {
            actions.add(ParserAction.DropLastInstruction)
            actions.add(ParserAction.Instruction(returnValue.copy(dst = movReg)))
        }
        actions.add(ParserAction.Instruction(RET))
        actions.add(ParserAction.AddRegCount(-2))
        actions
    }
)