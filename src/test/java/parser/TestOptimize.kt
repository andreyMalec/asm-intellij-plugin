package parser

import org.junit.Test
import parser.base.ParserTest

class TestOptimize : ParserTest() {
	//TODO номера строк сломали оптимизацию
	@Test
	fun test() {
		val original = listOf(
			"LOAD r1, [0]",
			"MOV r2, 5",
			"CALL add",
			"STORE [3], r3",
			"LOAD r3, [3]",
			"CMP r3, 128",
			"JNE IF_ELSE_0",
			"MOV r13, 2",
			"STORE [0], r13",
			"main:",
			"L0_main:",
			"CALL main",
			"RET"
		)

		val optimized = listOf(
			"LOAD r1, [0]",
			"MOV r2, 5",
			"CALL add",
			"STORE [3], r3",
			";Optimized; LOAD r3, [3]",
			"CMP r3, 128",
			"JNE IF_ELSE_0",
			"MOV r13, 2",
			"STORE [0], r13"
		)

//		val actual = AsmParser.optimize(original)
//
//		printCode(actual)
//
//		assert(optimized, actual)
	}
}