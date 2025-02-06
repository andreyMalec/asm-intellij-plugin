package org.objectweb.asm.idea.plugin

import java.util.*

class AsmParser {
	companion object {

		private val functionMatcher = Regex("(.+)(\\(.*\\).)")

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
				if (currentLine == "CALL main" && prevLine == "main:") {
					lines.removeLast()
					i += 2
					continue
				}

				lines.add(currentLine)
				i++
			}

			return lines
		}
	}

	var reg = 1

	var ifCount = 0
	var ifElseGOTO = "√"
	var ifGOTO = "√"
	val asm = mutableListOf<String>()

	fun toAsm(bytecode: String): String {
		return optimize(toAsmLines(bytecode)).joinToString("\n")
	}

	fun toAsmLines(bytecode: String): List<String> {
		val lines = bytecode.split("\n")

		var fileLine = 0
		var L = 0


		val instructions = Stack<Instruction>()
		val callStack = Stack<Int>()
		while (fileLine < lines.size) {
			val line = lines[fileLine].trim()
			val statement = line.split(" ")

			handleConst(instructions, statement[0])
			handleIf(instructions, statement)
			when (statement[0]) {
				ifElseGOTO -> {
					ifElseGOTO = "√"
					L++
					instructions.clear()
					asm.add("IF_ELSE_${ifCount - 1}:")
					fileLine++
					continue
				}

				ifGOTO -> {
					ifGOTO = "√"
					L++
					instructions.clear()
					asm.add("END_IF${ifCount - 1}:")
					fileLine++
					continue
				}

				"GOTO" -> {
					ifGOTO = statement[1]
					asm.add("JMP END_IF${ifCount - 1}")
				}

				"L$L" -> {
					L++
					instructions.clear()
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
					val address = statement[1].toInt()
					val value = statement[2].toInt()
					asm.add("LOAD $movReg, [${address}]")
					asm.add("ADD $movReg, $value")
					asm.add("STORE [${address}], $movReg")
				}


				"INVOKESTATIC" -> {
					val name = statement[1].functionName
					val argCount = statement[2].indexOf(')') - 1
					if (argCount > 0 && instructions.isNotEmpty()) {
						val arg = instructions.pop()
						asm.add("MOV r$reg, $arg")
						reg++
					}
					asm.add("CALL $name")
					if (argCount > 0)
						instructions.push(Instruction.Register(reg))
				}

				"ILOAD" -> {
					val address = statement[1].toInt()
					asm.add("LOAD r${reg}, [${address}]")
					instructions.push(Instruction.Register(reg))
					reg++
				}

				"ISTORE" -> {
					try {

						val value = instructions.pop()
						val address = statement[1].toInt()
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
					reg = callStack.pop()
				}
			}

			//Function definition
			if (statement[0] != "INVOKESTATIC" && statement.last().matches(functionMatcher)) {
				val name = functionMatcher.find(statement.last())?.groups?.get(1)?.value?.functionName
				asm.add("$name:")
				callStack.push(reg)
				reg = 1
			}

			fileLine++
		}

		return asm
	}

	private fun handleIf(instructions: Stack<Instruction>, statement: List<String>) {
		when (statement[0]) {
			"IFNE", "IFEQ" -> {
				val r = instructions.pop()
				asm.add("CMP $r, zr")
				reg -= 1

				val instr = if (statement[0] == "IFNE") "JNE" else "JE"
				asm.add("$instr IF_ELSE_${ifCount}")
				ifCount++
				ifElseGOTO = statement[1]
				instructions.push(Instruction.Register(reg))
			}

			"IF_ICMPNE", "IF_ICMPEQ" -> {
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
				val instr = if (statement[0] == "IF_ICMPNE") "JNE" else "JE"
				asm.add("$instr IF_ELSE_${ifCount}")
				ifCount++
				ifElseGOTO = statement[1]
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

	private val String.functionName: String
		get() {
			val dot = indexOf(".").takeIf { it > 0 }?.plus(1) ?: 0
			return substring(dot)
		}
}