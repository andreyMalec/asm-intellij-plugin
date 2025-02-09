package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.CALL
import com.malec.turingcomplete.ASM.MOV
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

class InvokeSideEffect : SideEffect(
    query = { statement ->
        handleInvoke && statement[0] == "INVOKESTATIC"
    },
    effect = { statement ->
        val actions = mutableListOf<ParserAction>()

        val argCount = statement[2].indexOf(')') - 1
        val retCount = statement[2].length - statement[2].indexOf(')')
        if (argCount > 0 && arguments.isNotEmpty && arguments.peek() is Argument.Value) {
            val arg = arguments.peek()
            actions.add(ParserAction.Pop)
            actions.add(ParserAction.Instruction(MOV(register(reg), arg)))
            actions.add(ParserAction.AddRegCount(1))
        }
        actions.add(ParserAction.Instruction(CALL(functionName(statement[1]))))
        if (retCount > 0)
            actions.add(ParserAction.Push(register(reg - 1)))

        actions.add(ParserAction.InvokeHandled)
        actions
    }
)