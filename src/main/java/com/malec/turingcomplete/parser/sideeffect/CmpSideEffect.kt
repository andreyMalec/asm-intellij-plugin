package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.*
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

private val regex = Regex("IF_ICMP(\\D+)")

class CmpSideEffect : SideEffect(
    query = { statement ->
        statement[0].matches(regex)
    },
    effect = { statement ->
        val actions = mutableListOf<ParserAction>()

        var r = reg

        when (arguments.size) {
            1 -> {
                val instr = arguments.peek()
                actions.add(ParserAction.Pop)
                actions.add(ParserAction.Instruction(CMP(register(reg - 1), instr)))
                actions.add(ParserAction.AddRegCount(-2))
                r -= 2
            }

            2 -> {
                val (instr1, instr2) = arguments.peek2()
                if (instr1 !is Argument.Register)
                    throw IllegalArgumentException()
                actions.add(ParserAction.Pop)
                actions.add(ParserAction.Pop)
                actions.add(ParserAction.Instruction(CMP(instr1, instr2)))
            }

            0 -> {
                actions.add(ParserAction.Instruction(CMP(register(reg - 2), register(reg - 1))))
                actions.add(ParserAction.AddRegCount(-3))
                r -= 3
            }
        }
        val label = label(statement[1])
        val instr = when (statement[0]) {
            "IF_ICMPNE" -> JNE(label)
            "IF_ICMPEQ" -> JE(label)
            "IF_ICMPGT" -> JA(label)
            "IF_ICMPGTE" -> JAE(label)
            "IF_ICMPLT" -> JB(label)
            "IF_ICMPLE" -> JBE(label)

            else -> throw IllegalArgumentException()
        }
        actions.add(ParserAction.Instruction(instr))
        actions.add(ParserAction.Push(register(r)))
        actions
    }
)