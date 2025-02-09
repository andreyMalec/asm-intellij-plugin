package com.malec.turingcomplete.parser.sideeffect

import com.malec.turingcomplete.ASM.LABEL
import com.malec.turingcomplete.parser.ParserAction
import com.malec.turingcomplete.parser.SideEffect

private val functionMatcher = Regex("(.+)(\\(.*\\).)")

class FunctionSideEffect : SideEffect(
    query = { statement ->
        statement[0] != "INVOKESTATIC" && statement.last().matches(functionMatcher)
    },
    effect = { statement ->
        val name = functionName(functionMatcher.find(statement.last())?.groups?.get(1)?.value!!)

        listOf(
            ParserAction.CurrentFunction(name.value.toString()),
            ParserAction.Instruction(LABEL(name)),
//            callStack.Push(reg),
            ParserAction.AddRegCount(-reg + 1),
        )
    }
)