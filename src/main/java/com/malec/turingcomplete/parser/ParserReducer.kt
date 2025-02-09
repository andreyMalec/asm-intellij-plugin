package com.malec.turingcomplete.parser

object ParserReducer {
    fun ParserState.reduce(action: ParserAction): ParserState {
        return when (action) {
            is ParserAction.Instruction -> copy(
                asm = asm + action.instruction
            )

            is ParserAction.Instructions -> copy(
                asm = asm + action.instructions
            )

            is ParserAction.AddRegCount -> copy(
                reg = reg + action.add
            )

            is ParserAction.AddVarCount -> copy(
                varCount = varCount + action.add
            )

            is ParserAction.HandleInvoke -> copy(
                handleInvoke = true
            )

            is ParserAction.InvokeHandled -> copy(
                handleInvoke = false
            )

            is ParserAction.CurrentFunction -> copy(
                currentFunName = action.name
            )

            is ParserAction.Push -> copy(
                arguments = arguments.push(action.argument)
            )

            is ParserAction.Pop -> copy(
                arguments = arguments.pop().first
            )

            is ParserAction.ClearArgs -> copy(
                arguments = ImmutableStack()
            )

            else -> copy()
        }
    }
}