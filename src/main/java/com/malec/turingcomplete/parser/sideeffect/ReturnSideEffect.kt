package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.RET
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class ReturnSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "RETURN" || statement[0] == "IRETURN"
    },
    effect = { _ ->
        listOf(
            ParserAction.Instruction(RET),
            ParserAction.AddRegCount(-2),
//            reg = callStack.pop()
        )
    }
)