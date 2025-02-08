package parser

import org.junit.Ignore
import org.junit.Test
import com.malec.turingcomplete.MEM
import parser.base.ParserTest

class FunctionTest : ParserTest() {

    @Test
    @Ignore
    fun test() {
        check(originalCode, bytecode, asmCode)
    }

    private val asmCode = listOf(
        "main:",
        "MOV r13, 1",
        "STORE [${0 * MEM}], r13",
        "MOV r13, 2",
        "STORE [${1 * MEM}], r13",
        "LOAD r1, [${0 * MEM}]",
        "LOAD r2, [${1 * MEM}]",
        "ADD r3, r1, r2",
        "STORE [${2 * MEM}], r3",
        "LOAD r1, [${0 * MEM}]",
        "LOAD r2, [${1 * MEM}]",
        "SUB r3, r1, r2",
        "STORE [${3 * MEM}], r3",
        "MOV r13, 2",
        "STORE [${4 * MEM}], r13",
        ";Optimized; LOAD r13, [${4 * MEM}]",
        "ADD r13, 1",
        "STORE [${4 * MEM}], r13",
        ";Optimized; LOAD r13, [${4 * MEM}]",
        "ADD r13, 2",
        "STORE [${4 * MEM}], r13",
        ";Optimized; LOAD r13, [${4 * MEM}]",
        "ADD r13, -3",
        "STORE [${4 * MEM}], r13",
        "RET"
    )

    private val originalCode = """fun main() {
    var x = input()
    val y = input()
    var i = add(x, y)

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
    LINENUMBER 6 L3
    ILOAD 2
    INVOKESTATIC TestKt.output (I)V
   L4
    LINENUMBER 7 L4
    ILOAD 0
    INVOKESTATIC TestKt.output (I)V
   L5
    LINENUMBER 8 L5
    NOP
    RETURN
   L6
    LOCALVARIABLE x I L1 L6 0
    LOCALVARIABLE y I L2 L6 1
    LOCALVARIABLE i I L3 L6 2
    MAXSTACK = 2
    MAXLOCALS = 3

  // access flags 0x19
  public final static input()I
   L0
    LINENUMBER 11 L0
    BIPUSH 123
    ISTORE 0
   L1
    LINENUMBER 12 L1
    ICONST_5
    ISTORE 1
   L2
    LINENUMBER 13 L2
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
    LINENUMBER 17 L0
    NOP
    RETURN
   L1
    LOCALVARIABLE x I L0 L1 0
    MAXSTACK = 0
    MAXLOCALS = 1

  // access flags 0x19
  public final static add(II)I
   L0
    LINENUMBER 20 L0
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