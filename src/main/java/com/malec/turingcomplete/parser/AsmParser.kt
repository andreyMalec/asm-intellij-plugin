package com.malec.turingcomplete.parser

import com.malec.turingcomplete.ASM
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.AsmOptimizer.optimize
import com.malec.turingcomplete.parser.sideeffect.*

class AsmParser {

    companion object {
        val movReg = Argument.Register.R13
    }

    fun toAsm(bytecode: String): String {
        return optimize(toAsmLines(bytecode)).joinToString("\n")
    }

    private val sideEffects = listOf(
        AddSideEffect(),
        ConstSideEffect(),
        EndOfFunctionSideEffect(),
        FunctionSideEffect(),
        GotoSideEffect(),
        CmpSideEffect(),
        IfSideEffect(),
        IncSideEffect(),
        InvokeSideEffect(),
        LineSideEffect(),
        LoadSideEffect(),
        PlatformSideEffect(),
        PushConstantSideEffect(),
        ReturnSideEffect(),
        StoreSideEffect(),
        SubSideEffect()
    )

    fun toAsmLines(bytecode: String): List<ASM> {
        val lines = bytecode.split("\n")

        var fileLine = 0
        var state = ParserState()

        while (fileLine < lines.size) {
            val line = lines[fileLine].trim()
            val statement = line.split(" ")

            val actions = mutableListOf<ParserAction>()
            sideEffects.forEach { sideEffect ->
                try {
                    if (sideEffect.query(state, statement))
                        actions.addAll(sideEffect(state, statement))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            actions.forEach { action ->
                with(ParserReducer) {
                    state = state.reduce(action)
                }
            }

            if (!state.handleInvoke)
                fileLine++
        }

        return state.asm
    }


}