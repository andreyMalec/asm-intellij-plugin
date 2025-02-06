import org.junit.Ignore
import org.junit.Test

class ArithmeticTest : ParserTest() {

	@Test
	fun test() {
		check(originalCode, bytecode, asmCode)
	}

	private val asmCode = listOf(
		"main:",
		"MOV r13, 1",
		"STORE [0], r13",
		"MOV r13, 2",
		"STORE [1], r13",
		"LOAD r1, [0]",
		"LOAD r2, [1]",
		"ADD r3, r1, r2",
		"STORE [2], r3",
		"LOAD r1, [0]",
		"LOAD r2, [1]",
		"SUB r3, r1, r2",
		"STORE [3], r3",
		"MOV r13, 2",
		"STORE [4], r13",
		";Optimized; LOAD r13, [4]",
		"ADD r13, 1",
		"STORE [4], r13",
		";Optimized; LOAD r13, [4]",
		"ADD r13, 2",
		"STORE [4], r13",
		";Optimized; LOAD r13, [4]",
		"ADD r13, -3",
		"STORE [4], r13",
		"RET"
	)

	private val originalCode = """fun main() {
	val a = 1
	val b = 2

	val c = a + b
	val d = a - b

	var aa = 2
	aa++
	aa += 2
	aa -= 3
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
    LINENUMBER 3 L1
    ICONST_2
    ISTORE 1
   L2
    LINENUMBER 5 L2
    ILOAD 0
    ILOAD 1
    IADD
    ISTORE 2
   L3
    LINENUMBER 6 L3
    ILOAD 0
    ILOAD 1
    ISUB
    ISTORE 3
   L4
    LINENUMBER 8 L4
    ICONST_2
    ISTORE 4
   L5
    LINENUMBER 9 L5
    IINC 4 1
   L6
    LINENUMBER 10 L6
    IINC 4 2
   L7
    LINENUMBER 11 L7
    IINC 4 -3
   L8
    LINENUMBER 12 L8
    RETURN
   L9
    LOCALVARIABLE a I L1 L9 0
    LOCALVARIABLE b I L2 L9 1
    LOCALVARIABLE c I L3 L9 2
    LOCALVARIABLE d I L4 L9 3
    LOCALVARIABLE aa I L5 L9 4
    MAXSTACK = 2
    MAXLOCALS = 5
}
"""
}