package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.*
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class IncSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "IINC"
    },
    effect = { statement ->
        val address = address(statement[1])
        val value = value(statement[2])
        listOf(
            ParserAction.Instructions(
                listOf(
                    LOAD(movReg, address),
                    ADD(movReg, movReg, value),
                    STORE(address, movReg)
                )
            ),
        )
    }
)