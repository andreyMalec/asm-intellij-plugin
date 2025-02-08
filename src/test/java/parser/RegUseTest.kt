package parser

import org.junit.Test
import com.malec.turingcomplete.MEM
import parser.base.ParserTest

class RegUseTest : ParserTest() {

	@Test
	fun test() {
		check(originalCode, bytecode, asmCode)
	}

	private val asmCode = listOf(
		"main:",
		"L0_main:",
		"MOV r13, 13",
		"STORE [${0 * MEM}], r13",
		"L1_main:",
		"MOV r13, 7",
		"STORE [${1 * MEM}], r13",
		"L2_main:",
		"LOAD r1, [${0 * MEM}]",
		"STORE [${1 * MEM}], r1",
		"L3_main:",
		"LOAD r2, [${1 * MEM}]",
		"STORE [${2 * MEM}], r2",
		"L4_main:",
		"MOV r13, 12345",
		"STORE [${3 * MEM}], r13",
		"L5_main:",
		"MOV r13, 127",
		"STORE [${4 * MEM}], r13",
		"L6_main:",
		"MOV r13, 16383",
		"STORE [${5 * MEM}], r13",
		"L7_main:",
		"RET",
		"",
		"L8_main:"
	)

	private val originalCode = """fun main() {
	val a = 13
	var b = 7
	b = a
	var c = b
	val d = 12345
	val e = 0b1111111
	val f = 0b1111111_1111111
}
"""

	private val bytecode = """// ================TestKt.class =================
// class version 52.0 (52)
// access flags 0x31
public final class TestKt {

  // compiled from: test.kt

  @Lkotlin/Metadata;(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u0008\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\u0006\u0010\u0000\u001a\u00020\u0001\u00a8\u0006\u0002"}, d2={"main", "", "asm-intellij-plugin.test"})

  // access flags 0x19
  public final static main()V
   L0
    LINENUMBER 2 L0
    BIPUSH 13
    ISTORE 0
   L1
    LINENUMBER 3 L1
    BIPUSH 7
    ISTORE 1
   L2
    LINENUMBER 4 L2
    ILOAD 0
    ISTORE 1
   L3
    LINENUMBER 5 L3
    ILOAD 1
    ISTORE 2
   L4
    LINENUMBER 6 L4
    SIPUSH 12345
    ISTORE 3
   L5
    LINENUMBER 7 L5
    BIPUSH 127
    ISTORE 4
   L6
    LINENUMBER 8 L6
    SIPUSH 16383
    ISTORE 5
   L7
    LINENUMBER 9 L7
    RETURN
   L8
    LOCALVARIABLE a I L1 L8 0
    LOCALVARIABLE b I L2 L8 1
    LOCALVARIABLE c I L4 L8 2
    LOCALVARIABLE d I L5 L8 3
    LOCALVARIABLE e I L6 L8 4
    LOCALVARIABLE f I L7 L8 5
    MAXSTACK = 1
    MAXLOCALS = 6
}
"""
}