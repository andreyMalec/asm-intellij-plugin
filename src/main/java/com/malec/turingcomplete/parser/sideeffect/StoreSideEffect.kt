package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.MOV
import com.malec.turingcomplete.ASM.STORE
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class StoreSideEffect : SideEffect(
    query = { statement ->
        statement[0] == "ISTORE"
    },
    effect = { statement ->
        val value = arguments.peek()
        val address = address(statement[1])
        listOf(
            ParserAction.Pop,
            when {
                value is Argument.Value.Number && value.value == 0 -> {
                    ParserAction.Instruction(STORE(address, Argument.Register.ZR))
                }

                value is Argument.Value -> {
                    ParserAction.Instructions(
                        listOf(
                            MOV(movReg, value),
                            STORE(address, movReg)
                        )
                    )
                }

                value is Argument.Register -> {
                    ParserAction.Instruction(STORE(address, value))
                }

                else -> throw IllegalArgumentException()
            }
        )

    }
)