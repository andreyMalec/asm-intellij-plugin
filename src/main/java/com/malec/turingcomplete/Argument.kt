package com.malec.turingcomplete

import com.malec.turingcomplete.Argument.Address
import com.malec.turingcomplete.Argument.Value
import com.malec.turingcomplete.Argument.Register
import com.malec.turingcomplete.parser.AsmParser

sealed interface Argument {
    open class Register(val index: Int) : Argument {
        override fun toString(): String {
            return if (index > 0)
                "r$index"
            else
                "zr"
        }

        object ZR : Register(0)
        object R1 : Register(1)
        object R2 : Register(2)
        object R3 : Register(3)
        object R4 : Register(4)
        object R5 : Register(5)
        object R6 : Register(6)
        object R7 : Register(7)
        object R8 : Register(8)
        object R9 : Register(9)
        object R10 : Register(10)
        object R11 : Register(11)
        object R12 : Register(12)
        object R13 : Register(13)
        object SP : Register(14)
        object FLAGS : Register(15)
    }

    data class Value(val value: Any) : Argument {
        override fun toString(): String {
            return value.toString()
        }
    }

    data class Address(val value: Int) : Argument {
        override fun toString(): String {
            return "[${value}]"
        }
    }

    private val String.functionName: String
        get() {
            val dot = indexOf(".").takeIf { it > 0 }?.plus(1) ?: 0
            return substring(dot)
        }
}

inline fun AsmParser.label(label: String): Value {
    return Value("${label}_$currentFunName")
}

inline fun AsmParser.address(value: String): Address {
    return Address((value.toInt() + varCount) * MEM)
}

inline fun AsmParser.value(value: String): Value {
    return Value(value)
}

inline fun AsmParser.register(index: Int): Register {
    return Register(index)
}

inline fun AsmParser.functionName(name: String): Value {
    val dot = name.indexOf(".").takeIf { it > 0 }?.plus(1) ?: 0
    return Value(name.substring(dot))
}