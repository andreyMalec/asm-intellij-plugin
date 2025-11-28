package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.CALL
import com.malec.turingcomplete.ASM.MOV
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.AsmParser.Companion.movReg
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

private val regex = Regex("\\((\\D*)\\)(\\D)")

class InvokeSideEffect : SideEffect(
    query = { statement ->
        handleInvoke && statement[0] == "INVOKESTATIC"
    },
    effect = { statement ->
        val actions = mutableListOf<ParserAction>()

        val signature = regex.find(statement.last())?.groups!!
        val argCount = signature[1]!!.value.length
        val retCount = if (signature[2]?.value == "V") 0 else 1
        if (argCount > 0 && arguments.size > 0) {
            val args = arguments.peek(argCount)
            var r = reg
            args.forEach {
                actions.add(ParserAction.Pop)
                if (it is Argument.Value) {
                    actions.add(ParserAction.Instruction(MOV(register(r), it)))
                    r++
                } else {
                    actions.add(ParserAction.AddRegCount(-1))
                }
            }
        }
        actions.add(ParserAction.Instruction(CALL(functionName(statement[1]))))
        if (retCount > 0)
            actions.add(ParserAction.Push(movReg))

        actions.add(ParserAction.InvokeHandled)
        actions
    }
)