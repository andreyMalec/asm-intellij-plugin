package parser

import org.junit.Test
import com.malec.turingcomplete.MEM
import parser.base.ParserTest

class IfTest : ParserTest() {

    @Test
    fun test() {
        check(originalCode, bytecode, asmCode)
    }

    private val asmCode = listOf(
        "main:",
        "L0_main:",
        "MOV r13, 1",
        "STORE [${0 * MEM}], r13",
        "L1_main:",
        "LOAD r1, [${0 * MEM}]",
        "CMP r1, zr",
        "JE L2_main",
        "L3_main:",
        "STORE [${0 * MEM}], zr",
        "L2_main:",
        "LOAD r1, [${0 * MEM}]",
        "CMP r1, zr",
        "JNE L4_main",
        "L5_main:",
        "MOV r13, 1",
        "STORE [${0 * MEM}], r13",
        "L4_main:",
        "LOAD r1, [${0 * MEM}]",
        "CMP r1, 1",
        "JNE L6_main",
        "L7_main:",
        "MOV r13, 1",
        "STORE [${0 * MEM}], r13",
        "JMP L8_main",
        "L6_main:",
        "MOV r13, 2",
        "STORE [${0 * MEM}], r13",
        "L8_main:",
        "LOAD r2, [${0 * MEM}]",
        "CMP r2, 5",
        "JE L9_main",
        "L10_main:",
        "MOV r13, 3",
        "STORE [${0 * MEM}], r13",
        "JMP L11_main",
        "L9_main:",
        "MOV r13, 4",
        "STORE [${0 * MEM}], r13",
        "L11_main:",
        "RET",
        "",
        "L12_main:"
    )

    private val originalCode = """fun main() {
	var a = 1

	if (a != 0)
		a = 0

	if (a == 0)
		a = 1

	if (a == 1)
		a = 1
	else
		a = 2

	if (a != 5)
		a = 3
	else
		a = 4
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
    ICONST_1
    ISTORE 0
   L1
    LINENUMBER 4 L1
    ILOAD 0
    IFEQ L2
   L3
    LINENUMBER 5 L3
    ICONST_0
    ISTORE 0
   L2
    LINENUMBER 7 L2
   FRAME APPEND [I]
    ILOAD 0
    IFNE L4
   L5
    LINENUMBER 8 L5
    ICONST_1
    ISTORE 0
   L4
    LINENUMBER 10 L4
   FRAME SAME
    ILOAD 0
    ICONST_1
    IF_ICMPNE L6
   L7
    LINENUMBER 11 L7
    ICONST_1
    ISTORE 0
    GOTO L8
   L6
    LINENUMBER 13 L6
   FRAME SAME
    ICONST_2
    ISTORE 0
   L8
    LINENUMBER 15 L8
   FRAME SAME
    ILOAD 0
    ICONST_5
    IF_ICMPEQ L9
   L10
    LINENUMBER 16 L10
    ICONST_3
    ISTORE 0
    GOTO L11
   L9
    LINENUMBER 18 L9
   FRAME SAME
    ICONST_4
    ISTORE 0
   L11
    LINENUMBER 19 L11
   FRAME SAME
    NOP
    RETURN
   L12
    LOCALVARIABLE a I L1 L12 0
    MAXSTACK = 2
    MAXLOCALS = 1
}
"""
}