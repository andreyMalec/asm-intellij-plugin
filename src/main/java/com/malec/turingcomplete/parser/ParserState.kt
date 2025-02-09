package com.malec.turingcomplete.parser

import com.malec.turingcomplete.ASM
import com.malec.turingcomplete.Argument
import com.malec.turingcomplete.Argument.*
import com.malec.turingcomplete.MEM

data class ParserState(
    val reg: Int = 1,
    val varCount: Int = 0,
    val currentFunName: String = "",
    val asm: List<ASM> = listOf(),
    val arguments: ImmutableStack<Argument> = ImmutableStack(),
    val handleInvoke: Boolean = false
) {
    inline fun label(label: String): Value.Label {
        return Value.Label("${label}_$currentFunName")
    }

    inline fun address(value: String): Address {
        return Address((value.toInt() + varCount) * MEM)
    }

    inline fun value(value: String): Value.Number {
        return Value.Number(value.toInt())
    }

    inline fun register(index: Int): Register {
        return Register(index)
    }

    inline fun functionName(name: String): Value {
        val dot = name.indexOf(".").takeIf { it > 0 }?.plus(1) ?: 0
        return Value.Label(name.substring(dot))
    }
}