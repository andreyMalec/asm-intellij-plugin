package parser

import org.junit.Test
import org.objectweb.asm.idea.plugin.MEM
import parser.base.ParserTest

class GeneralTest : ParserTest() {

	@Test
	fun test() {
		check(originalCode, bytecode, asmCode)
	}

	private val asmCode = listOf(
		"main:",
		"L0_main:",
		"MOV r13, 123",
		"STORE [${0 * MEM}], r13",
		"L1_main:",
		"MOV r13, 6",
		"STORE [${1 * MEM}], r13",
		"L2_main:",
		"MOV r13, 8",
		"STORE [${2 * MEM}], r13",
		"L3_main:",
		"LOAD r1, [${0 * MEM}]",
		"MOV r2, 5",
		"CALL add",
		"STORE [${3 * MEM}], r3",
		"L4_main:",
		"LOAD r3, [${3 * MEM}]",
		"CMP r3, 128",
		"JNE L5_main",
		"L6_main:",
		"MOV r13, 2",
		"STORE [${0 * MEM}], r13",
		"L7_main:",
		"STORE [${3 * MEM}], zr",
		"JMP L8_main",
		"L5_main:",
		"MOV r13, 3",
		"STORE [${0 * MEM}], r13",
		"L8_main:",
		"RET",
		"",
		"L9_main:",
		"add:",
		"L0_add:",
		"LOAD r1, [${4 * MEM}]",
		"LOAD r2, [${5 * MEM}]",
		"ADD r3, r1, r2",
		"RET",
		"",
		"L1_add:"
	)

	private val originalCode = """fun main() {
    var a = 123
    var six = 2 + 2 * 2
    var eight = (2 + 2) * 2
    var c = add(a, 5)

    if (c == 128) {
        a = 2
        c = 0
    } else {
        a = 3
    }
}

fun add(a: Int, b: Int): Int {
    return a + b
}
"""

	private val bytecode = """// ================TestKt.class =================
// class version 52.0 (52)
// access flags 0x31
public final class TestKt {

  // compiled from: test.kt

  @Lkotlin/Metadata;(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u0010\n\u0000\n\u0002\u0010\u0008\n\u0002\u0008\u0003\n\u0002\u0010\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0006\u0010\u0004\u001a\u00020\u0005\u00a8\u0006\u0006"}, d2={"add", "", "a", "b", "main", "", "kiss.main"})

  // access flags 0x19
  public final static main()V
   L0
    LINENUMBER 2 L0
    BIPUSH 123
    ISTORE 0
   L1
    LINENUMBER 3 L1
    BIPUSH 6
    ISTORE 1
   L2
    LINENUMBER 4 L2
    BIPUSH 8
    ISTORE 2
   L3
    LINENUMBER 5 L3
    ILOAD 0
    ICONST_5
    INVOKESTATIC TestKt.add (II)I
    ISTORE 3
   L4
    LINENUMBER 7 L4
    ILOAD 3
    SIPUSH 128
    IF_ICMPNE L5
   L6
    LINENUMBER 8 L6
    ICONST_2
    ISTORE 0
   L7
    LINENUMBER 9 L7
    ICONST_0
    ISTORE 3
    GOTO L8
   L5
    LINENUMBER 11 L5
   FRAME FULL [I I I I] []
    ICONST_3
    ISTORE 0
   L8
    LINENUMBER 13 L8
   FRAME SAME
    RETURN
   L9
    LOCALVARIABLE a I L1 L9 0
    LOCALVARIABLE six I L2 L9 1
    LOCALVARIABLE eight I L3 L9 2
    LOCALVARIABLE c I L4 L9 3
    MAXSTACK = 2
    MAXLOCALS = 4

  // access flags 0x19
  public final static add(II)I
   L0
    LINENUMBER 16 L0
    ILOAD 0
    ILOAD 1
    IADD
    IRETURN
   L1
    LOCALVARIABLE a I L0 L1 0
    LOCALVARIABLE b I L0 L1 1
    MAXSTACK = 2
    MAXLOCALS = 2
}
"""
}