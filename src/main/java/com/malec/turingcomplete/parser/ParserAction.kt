package com.malec.turingcomplete.parser

import com.malec.turingcomplete.ASM
import com.malec.turingcomplete.Argument

sealed interface ParserAction {
    data object Ignore : ParserAction
    data class Push(val argument: Argument) : ParserAction
    data object Pop : ParserAction
    data object ClearArgs : ParserAction
    data object HandleInvoke : ParserAction
    data object InvokeHandled : ParserAction
    data class AddVarCount(val add: Int) : ParserAction
    data class AddRegCount(val add: Int) : ParserAction
    data class Instruction(val instruction: ASM) : ParserAction
    data class Instructions(val instructions: List<ASM>) : ParserAction
    data class CurrentFunction(val name: String) : ParserAction
}