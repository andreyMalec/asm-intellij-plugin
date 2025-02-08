package parser

import org.junit.Test
import org.objectweb.asm.idea.plugin.MEM
import parser.base.ParserTest

class WhileTest : ParserTest() {

    @Test
    fun test() {
        check(originalCode, bytecode, asmCode)
    }

    private val asmCode = listOf(
        "main:",
        "L0_main:",
        "MOV r13, 31",
        "STORE [${0 * MEM}], r13",
        "L1_main:",
        "MOV r13, 9",
        "STORE [${1 * MEM}], r13",
        "L2_main:",
        "STORE [${2 * MEM}], zr",
        "L3_main:",
        "LOAD r1, [${0 * MEM}]",
        "LOAD r2, [${1 * MEM}]",
        "CMP r1, r2",
        "JB L4_main",
        "L5_main:",
        "LOAD r3, [${2 * MEM}]",
        "LOAD r13, [${2 * MEM}]",
        "ADD r13, r13, 1",
        "STORE [${2 * MEM}], r13",
        "L6_main:",
        "LOAD r4, [${0 * MEM}]",
        "LOAD r5, [${1 * MEM}]",
        "SUB r6, r4, r5",
        "STORE [${0 * MEM}], r6",
        "JMP L3_main",
        "L4_main:",
        "LOAD r4, [${2 * MEM}]",
        "OUT r4",
        "L7_main:",
        "LOAD r5, [${0 * MEM}]",
        "OUT r5",
        "L8_main:",
        "RET",
        "",
        "L9_main:"
    )

    private val originalCode = """fun main() {
    var x = 31
    val y = 9
    var i = 0

    while (x >= y) {
        i++
        x -= y
    }

    output(i)
    output(x)
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
    LINENUMBER 4 L0
    BIPUSH 31
    ISTORE 0
   L1
    LINENUMBER 5 L1
    BIPUSH 9
    ISTORE 1
   L2
    LINENUMBER 6 L2
    ICONST_0
    ISTORE 2
   L3
    LINENUMBER 8 L3
   FRAME APPEND [I I I]
    ILOAD 0
    ILOAD 1
    IF_ICMPLT L4
   L5
    LINENUMBER 9 L5
    ILOAD 2
    IINC 2 1
    POP
   L6
    LINENUMBER 10 L6
    ILOAD 0
    ILOAD 1
    ISUB
    ISTORE 0
    GOTO L3
   L4
    LINENUMBER 13 L4
   FRAME SAME
    ILOAD 2
    INVOKESTATIC framework/IoKt.output (I)V
   L7
    LINENUMBER 14 L7
    ILOAD 0
    INVOKESTATIC framework/IoKt.output (I)V
   L8
    LINENUMBER 15 L8
    NOP
    RETURN
   L9
    LOCALVARIABLE x I L1 L9 0
    LOCALVARIABLE y I L2 L9 1
    LOCALVARIABLE i I L3 L9 2
    MAXSTACK = 2
    MAXLOCALS = 3

  // access flags 0x1009
  public static synthetic main([Ljava/lang/String;)V
   L0
    INVOKESTATIC TestKt.main ()V
    NOP
    RETURN
   L1
    LOCALVARIABLE args [Ljava/lang/String; L0 L1 0
    MAXSTACK = 0
    MAXLOCALS = 1
}
"""
}