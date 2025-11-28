package parser

import org.junit.Ignore
import org.junit.Test
import com.malec.turingcomplete.MEM
import parser.base.ParserTest

class FunctionTest : ParserTest() {

    @Test
    fun test() {
        check(originalCode, bytecode, asmCode)
    }

    private val asmCode = listOf(
        "main:",
        "L0_main:",
        "CALL input",
        "STORE [${0 * MEM}], r13",
        "L1_main:",
        "CALL input",
        "STORE [${1 * MEM}], r13",
        "L2_main:",
        "LOAD r1, [${0 * MEM}]",
        "LOAD r2, [${1 * MEM}]",
        "CALL add",
        "STORE [${2 * MEM}], r13",
        "L3_main:",
        "MOV r1, 5",
        "MOV r2, 3",
        "CALL add",
        "STORE [${3 * MEM}], r13",
        "L4_main:",
        "LOAD r1, [${2 * MEM}]",
        "CALL output",
        "L5_main:",
        "LOAD r1, [${0 * MEM}]",
        "CALL output",
        "L6_main:",
        "RET",
        "",
        "L7_main:",
        "input:",
        "L0_input:",
        "MOV r13, 123",
        "STORE [${4 * MEM}], r13",
        "L1_input:",
        "MOV r13, 5",
        "STORE [${5 * MEM}], r13",
        "L2_input:",
        "LOAD r13, [${4 * MEM}]",
        "RET",
        "",
        "L3_input:",
        "output:",
        "L0_output:",
        "RET",
        "",
        "L1_output:",
        "add:",
        "L0_add:",
        "LOAD r1, [${7 * MEM}]",
        "LOAD r2, [${8 * MEM}]",
        "ADD r13, r1, r2",
        "RET",
        "",
        "L1_add:",
        "main:",
        "L0_main:",
        "CALL main",
        "RET",
        "",
        "L1_main:"
    )

    private val originalCode = """fun main() {
    var x = input()
    val y = input()
    var i = add(x, y)
    var c = add(5, 3)

    output(i)
    output(x)
}

fun input(): Int {
    val a = 123
    val b = 5
    return a
}

fun output(x: Int) {
}

fun add(x: Int, y: Int): Int {
    return x + y
}
"""

    private val bytecode = """// ================TestKt.class =================
// class version 52.0 (52)
// access flags 0x31
public final class TestKt {

  // compiled from: test.kt

  @Lkotlin/Metadata;(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u0012\n\u0000\n\u0002\u0010\u0008\n\u0002\u0008\u0004\n\u0002\u0010\u0002\n\u0002\u0008\u0002\u001a\u0016\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0006\u0010\u0004\u001a\u00020\u0001\u001a\u0006\u0010\u0005\u001a\u00020\u0006\u001a\u000e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u0001\u00a8\u0006\u0008"}, d2={"add", "", "x", "y", "input", "main", "", "output", "asm-intellij-plugin.test"})

  // access flags 0x19
  public final static main()V
   L0
    LINENUMBER 2 L0
    INVOKESTATIC TestKt.input ()I
    ISTORE 0
   L1
    LINENUMBER 3 L1
    INVOKESTATIC TestKt.input ()I
    ISTORE 1
   L2
    LINENUMBER 4 L2
    ILOAD 0
    ILOAD 1
    INVOKESTATIC TestKt.add (II)I
    ISTORE 2
   L3
    LINENUMBER 5 L3
    ICONST_5
    ICONST_3
    INVOKESTATIC TestKt.add (II)I
    ISTORE 3
   L4
    LINENUMBER 7 L4
    ILOAD 2
    INVOKESTATIC TestKt.output (I)V
   L5
    LINENUMBER 8 L5
    ILOAD 0
    INVOKESTATIC TestKt.output (I)V
   L6
    LINENUMBER 9 L6
    NOP
    RETURN
   L7
    LOCALVARIABLE x I L1 L7 0
    LOCALVARIABLE y I L2 L7 1
    LOCALVARIABLE i I L3 L7 2
    LOCALVARIABLE c I L4 L7 3
    MAXSTACK = 2
    MAXLOCALS = 4

  // access flags 0x19
  public final static input()I
   L0
    LINENUMBER 12 L0
    BIPUSH 123
    ISTORE 0
   L1
    LINENUMBER 13 L1
    ICONST_5
    ISTORE 1
   L2
    LINENUMBER 14 L2
    ILOAD 0
    IRETURN
   L3
    LOCALVARIABLE a I L1 L3 0
    LOCALVARIABLE b I L2 L3 1
    MAXSTACK = 1
    MAXLOCALS = 2

  // access flags 0x19
  public final static output(I)V
   L0
    LINENUMBER 18 L0
    NOP
    RETURN
   L1
    LOCALVARIABLE x I L0 L1 0
    MAXSTACK = 0
    MAXLOCALS = 1

  // access flags 0x19
  public final static add(II)I
   L0
    LINENUMBER 21 L0
    ILOAD 0
    ILOAD 1
    IADD
    IRETURN
   L1
    LOCALVARIABLE x I L0 L1 0
    LOCALVARIABLE y I L0 L1 1
    MAXSTACK = 2
    MAXLOCALS = 2

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