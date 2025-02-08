package org.objectweb.asm.idea.plugin

import java.util.*

class AsmParser {
    companion object {

        private val functionMatcher = Regex("(.+)(\\(.*\\).)")
        private val lineMatcher = Regex("L(\\d+)")

        private val movReg = Instruction.Register(13)

        fun optimize(asmLines: List<String>): List<String> {
            val storeRegex = Regex("STORE \\[(\\d+)\\], r(\\d+)")
            val loadRegex = Regex("LOAD r(\\d+), \\[(\\d+)\\]")

            val lines = mutableListOf(
                asmLines[0]
            )

            var currentLine = asmLines[0]
            var prevLine: String
            var i = 1
            while (i in 1..asmLines.lastIndex) {
                prevLine = currentLine
                currentLine = asmLines[i]

                if (prevLine.matches(storeRegex) && currentLine.matches(loadRegex)) {
                    val storeGroup = storeRegex.find(prevLine)?.groupValues?.drop(1) ?: listOf()
                    val loadGroup = loadRegex.find(currentLine)?.groupValues?.drop(1) ?: listOf()
                    if (storeGroup == loadGroup.reversed()) {
                        //skip LOAD instruction
                        lines.add(";Optimized; $currentLine")
                        i++
                        continue
                    }
                }
                if (currentLine.matches(storeRegex) && prevLine.matches(loadRegex)) {
                    val storeGroup = storeRegex.find(currentLine)?.groupValues?.drop(1) ?: listOf()
                    val loadGroup = loadRegex.find(prevLine)?.groupValues?.drop(1) ?: listOf()
                    if (storeGroup == loadGroup.reversed()) {
                        //skip STORE instruction
                        lines.add(";Optimized; $currentLine")
                        i++
                        continue
                    }
                }

                //skip synthetic main
                if (currentLine == "CALL main" && prevLine == "L0_main:") {
                    lines.removeLast()
                    lines.removeLast()
                    i += 4
                    continue
                }

                lines.add(currentLine)
                i++
            }

            return lines
        }
    }

    var reg = 1

    var varCount = 0
    var currentFunName: String = ""

    val asm = mutableListOf<String>()

    fun toAsm(bytecode: String): String {
        return optimize(toAsmLines(bytecode)).joinToString("\n")
    }

    fun toAsmLines(bytecode: String): List<String> {
        val lines = bytecode.split("\n")

        var fileLine = 0


        val instructions = Stack<Instruction>()
        val callStack = Stack<Int>()
        while (fileLine < lines.size) {
            val line = lines[fileLine].trim()
            val statement = line.split(" ")

            handleConst(instructions, statement[0])
            handleIf(instructions, statement)
            handleLine(instructions, statement[0])

            when (statement[0]) {
                "MAXLOCALS" -> {
                    varCount += statement.last().toInt()
                }

                "GOTO" -> {
                    asm.add("JMP ${statement[1].lineLabel}")
                }

                "BIPUSH", "SIPUSH" -> {
                    val value = statement[1].toInt()
                    instructions.push(Instruction.Value(value))
                }

                "IADD" -> {
                    reg -= 2
                    asm.add("ADD r${reg + 2}, r${reg}, r${reg + 1}")
                    instructions.push(Instruction.Register(reg + 2))
                }

                "ISUB" -> {
                    reg -= 2
                    asm.add("SUB r${reg + 2}, r${reg}, r${reg + 1}")
                    instructions.push(Instruction.Register(reg + 2))
                }

                "IINC" -> {
                    val address = statement[1].address
                    val value = statement[2].toInt()
                    asm.add("LOAD $movReg, [${address}]")
                    asm.add("ADD $movReg, $movReg, $value")
                    asm.add("STORE [${address}], $movReg")
                }

                "INVOKESTATIC" -> {
                    if (!handleFrameworkCalls(instructions, statement)) {
                        val name = statement[1].functionName
                        val argCount = statement[2].indexOf(')') - 1
                        val retCount = statement[2].length - statement[2].indexOf(')')
                        if (argCount > 0 && instructions.isNotEmpty() && instructions.peek() is Instruction.Value) {
                            val arg = instructions.pop()
                            asm.add("MOV r$reg, $arg")
                            reg++
                        }
                        asm.add("CALL $name")
                        if (retCount > 0)
                            instructions.push(Instruction.Register(reg))
                    }
                }

                "ILOAD" -> {
                    val address = statement[1].address
                    asm.add("LOAD r${reg}, [${address}]")
                    instructions.push(Instruction.Register(reg))
                    reg++
                }

                "ISTORE" -> {
                    try {

                        val value = instructions.pop()
                        val address = statement[1].address
                        when {
                            value is Instruction.Value && value.value == 0 -> {
                                asm.add("STORE [${address}], zr")
                            }

                            value is Instruction.Value -> {
                                asm.add("MOV $movReg, $value")
                                asm.add("STORE [${address}], $movReg")
                            }

                            else -> {
//								reg--
                                asm.add("STORE [${address}], $value")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                "RETURN", "IRETURN" -> {
                    asm.add("RET")
                    asm.add("")
                    reg = callStack.pop()
                }
            }

            //Function definition
            if (statement[0] != "INVOKESTATIC" && statement.last().matches(functionMatcher)) {
                val name = functionMatcher.find(statement.last())?.groups?.get(1)?.value?.functionName!!
                currentFunName = name
                asm.add("$name:")
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
    private fun handleFrameworkCalls(instructions: Stack<Instruction>, statement: List<String>): Boolean {
        if (statement[1] == "framework/IoKt.output") {
            asm.add("OUT r${reg - 1}")
            return true
        }
        if (statement[1] == "framework/IoKt.input") {
            asm.add("IN $movReg")
            instructions.push(movReg)
            return true
        }
        return false
    }

    private fun handleLine(instructions: Stack<Instruction>, statement: String) {
        if (statement.matches(lineMatcher)) {
            asm.add("${statement.lineLabel}:")
            instructions.clear()
        }
    }

    private fun handleIf(instructions: Stack<Instruction>, statement: List<String>) {
        when (statement[0]) {
            "IFNE", "IFEQ" -> {
                val r = instructions.pop()
                asm.add("CMP $r, zr")
                reg -= 1

                val instr = if (statement[0] == "IFNE") "JNE" else "JE"
                asm.add("$instr ${statement[1].lineLabel}")
                instructions.push(Instruction.Register(reg))
            }

            "IF_ICMPNE",
            "IF_ICMPEQ",
            "IF_ICMPGT",
            "IF_ICMPGTE",
            "IF_ICMPLT",
            "IF_ICMPLE" -> {
                fun jumpInstruction(ifInstruction: String) = when (ifInstruction) {
                    "IF_ICMPNE" -> "JNE"
                    "IF_ICMPEQ" -> "JE"
                    "IF_ICMPGT" -> "JA"
                    "IF_ICMPGTE" -> "JAE"
                    "IF_ICMPLT" -> "JB"
                    "IF_ICMPLE" -> "JBE"

                    else -> throw IllegalArgumentException(ifInstruction)
                }

                when (instructions.size) {
                    1 -> {
                        val instr = instructions.pop()
                        asm.add("CMP r${reg - 1}, $instr")
                        reg -= 2
                    }

                    2 -> {
                        val instr2 = instructions.pop()
                        val instr1 = instructions.pop()
                        asm.add("CMP $instr1, $instr2")
                    }

                    0 -> {
                        asm.add("CMP r${reg - 2}, r${reg - 1}")
                        reg -= 3
                    }
                }
                val instr = jumpInstruction(statement[0])
                asm.add("$instr ${statement[1].lineLabel}")
                instructions.push(Instruction.Register(reg))
            }
        }
    }

    private fun handleConst(instructions: Stack<Instruction>, statement: String) {
        when (statement) {
            "ICONST_0" -> {
                instructions.push(Instruction.Value(0))
            }

            "ICONST_1" -> {
                instructions.push(Instruction.Value(1))
            }

            "ICONST_2" -> {
                instructions.push(Instruction.Value(2))
            }

            "ICONST_3" -> {
                instructions.push(Instruction.Value(3))
            }

            "ICONST_4" -> {
                instructions.push(Instruction.Value(4))
            }

            "ICONST_5" -> {
                instructions.push(Instruction.Value(5))
            }

            "ICONST_6" -> {
                instructions.push(Instruction.Value(6))
            }

            "ICONST_7" -> {
                instructions.push(Instruction.Value(7))
            }
        }
    }

    private val String.lineLabel: String
        get() {
            return "${this}_$currentFunName"
        }

    private val String.functionName: String
        get() {
            val dot = indexOf(".").takeIf { it > 0 }?.plus(1) ?: 0
            return substring(dot)
        }

    private val String.address: Int
        get() {
            return (toInt() + varCount) * MEM
        }
}