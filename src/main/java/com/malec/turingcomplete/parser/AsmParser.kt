package com.malec.turingcomplete.parser

import com.malec.turingcomplete.*
import com.malec.turingcomplete.ASM.*
import com.malec.turingcomplete.AsmOptimizer.optimize
import java.util.*

class AsmParser {

    companion object {
        private val functionMatcher = Regex("(.+)(\\(.*\\).)")
        private val lineMatcher = Regex("L(\\d+)")
        private val movReg = Argument.Register.R13
    }

    var reg = 1

    var varCount = 0
    var currentFunName: String = ""

    val asm = mutableListOf<ASM>()

    fun toAsm(bytecode: String): String {
        return optimize(toAsmLines(bytecode)).joinToString("\n")
    }

    fun toAsmLines(bytecode: String): List<ASM> {
        val lines = bytecode.split("\n")

        var fileLine = 0


        val arguments = Stack<Argument>()
        val callStack = Stack<Int>()
        while (fileLine < lines.size) {
            val line = lines[fileLine].trim()
            val statement = line.split(" ")

            handleConst(arguments, statement[0])
            handleIf(arguments, statement)
            handleLine(arguments, statement[0])

            when (statement[0]) {
                "MAXLOCALS" -> {
                    varCount += statement.last().toInt()
                }

                "GOTO" -> {
                    asm.add(JMP(label(statement[1])))
                }

                "BIPUSH", "SIPUSH" -> {
                    arguments.push(value(statement[1]))
                }

                "IADD" -> {
                    reg -= 2
                    val dst = Argument.Register(reg + 2)
                    val a = Argument.Register(reg)
                    val b = Argument.Register(reg + 1)
                    asm.add(ADD(dst, a, b))
                    arguments.push(dst)
                }

                "ISUB" -> {
                    reg -= 2
                    val dst = Argument.Register(reg + 2)
                    val a = Argument.Register(reg)
                    val b = Argument.Register(reg + 1)
                    asm.add(SUB(dst, a, b))
                    arguments.push(dst)
                }

                "IINC" -> {
                    val address = address(statement[1])
                    val value = value(statement[2])
                    asm.add(LOAD(movReg, address))
                    asm.add(ADD(movReg, movReg, value))
                    asm.add(STORE(address, movReg))
                }

                "INVOKESTATIC" -> {
                    if (!handleFrameworkCalls(arguments, statement)) {
                        val argCount = statement[2].indexOf(')') - 1
                        val retCount = statement[2].length - statement[2].indexOf(')')
                        if (argCount > 0 && arguments.isNotEmpty() && arguments.peek() is Argument.Value) {
                            val arg = arguments.pop()
                            asm.add(MOV(register(reg), arg))
                            reg++
                        }
                        asm.add(CALL(functionName(statement[1])))
                        if (retCount > 0)
                            arguments.push(Argument.Register(reg))
                    }
                }

                "ILOAD" -> {
                    val address = address(statement[1])
                    val dst = register(reg)
                    asm.add(LOAD(dst, address))
                    arguments.push(dst)
                    reg++
                }

                "ISTORE" -> {
                    try {
                        val value = arguments.pop()
                        val address = address(statement[1])
                        when {
                            value is Argument.Value && value.value == 0 -> {
                                asm.add(STORE(address, Argument.Register.ZR))
                            }

                            value is Argument.Value -> {
                                asm.add(MOV(movReg, value))
                                asm.add(STORE(address, movReg))
                            }

                            value is Argument.Register -> {
                                asm.add(STORE(address, value))
                            }

                            else -> throw IllegalArgumentException()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                "RETURN", "IRETURN" -> {
                    asm.add(RET)
                    reg = callStack.pop()
                }
            }

            //Function definition
            if (statement[0] != "INVOKESTATIC" && statement.last().matches(functionMatcher)) {
                val name = functionName(functionMatcher.find(statement.last())?.groups?.get(1)?.value!!)
                currentFunName = name.value.toString()
                asm.add(LABEL(name))
                callStack.push(reg)
                reg = 1
            }

            fileLine++
        }

        return asm
    }

    /**
     * @return true if line taken
     */
    private fun handleFrameworkCalls(instructions: Stack<Argument>, statement: List<String>): Boolean {
        if (statement[1] == "framework/IoKt.output") {
            asm.add(OUT(register(reg - 1)))
            return true
        }
        if (statement[1] == "framework/IoKt.input") {
            asm.add(IN(movReg))
            instructions.push(movReg)
            return true
        }
        return false
    }

    private fun handleLine(instructions: Stack<Argument>, statement: String) {
        if (statement.matches(lineMatcher)) {
            asm.add(LABEL(label(statement)))
            instructions.clear()
        }
    }

    private fun handleIf(instructions: Stack<Argument>, statement: List<String>) {
        when (statement[0]) {
            "IFNE", "IFEQ" -> {
                val r = instructions.pop()
                if (r !is Argument.Register)
                    throw IllegalArgumentException()
                asm.add(CMP(r, Argument.Register.ZR))
                reg -= 1

                val label = label(statement[1])
                val a = when (statement[0]) {
                    "IFNE" -> JNE(label)
                    else -> JE(label)
                }
                asm.add(a)
                instructions.push(Argument.Register(reg))
            }

            "IF_ICMPNE",
            "IF_ICMPEQ",
            "IF_ICMPGT",
            "IF_ICMPGTE",
            "IF_ICMPLT",
            "IF_ICMPLE" -> {

                when (instructions.size) {
                    1 -> {
                        val instr = instructions.pop()
                        asm.add(CMP(register(reg - 1), instr))
                        reg -= 2
                    }

                    2 -> {
                        val instr2 = instructions.pop()
                        val instr1 = instructions.pop()
                        if (instr1 !is Argument.Register)
                            throw IllegalArgumentException()
                        asm.add(CMP(instr1, instr2))
                    }

                    0 -> {
                        asm.add(CMP(register(reg - 2), register(reg - 1)))
                        reg -= 3
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
                asm.add(instr)
                instructions.push(Argument.Register(reg))
            }
        }
    }

    private fun handleConst(instructions: Stack<Argument>, statement: String) {
        when (statement) {
            "ICONST_0" -> {
                instructions.push(Argument.Value(0))
            }

            "ICONST_1" -> {
                instructions.push(Argument.Value(1))
            }

            "ICONST_2" -> {
                instructions.push(Argument.Value(2))
            }

            "ICONST_3" -> {
                instructions.push(Argument.Value(3))
            }

            "ICONST_4" -> {
                instructions.push(Argument.Value(4))
            }

            "ICONST_5" -> {
                instructions.push(Argument.Value(5))
            }

            "ICONST_6" -> {
                instructions.push(Argument.Value(6))
            }

            "ICONST_7" -> {
                instructions.push(Argument.Value(7))
            }
        }
    }

}