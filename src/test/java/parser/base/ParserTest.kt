package parser.base

import com.malec.turingcomplete.ASM
import com.malec.turingcomplete.AsmOptimizer
import org.junit.Assert.assertEquals
import com.malec.turingcomplete.parser.AsmParser
import com.malec.turingcomplete.MEM

abstract class ParserTest {
    protected open val printForTestEdit: Boolean = false

    protected fun printCode(asmCode: List<String>, originalCode: String? = null, printForTestEdit: Boolean = false) {
        println("")
        println("________________ ${this::class.simpleName} ________________")
        if (printForTestEdit) {
            val addressMatcher = Regex(".*\\[(\\d+)\\].*")
            println(asmCode.joinToString(",\n") { line ->
                if (line.matches(addressMatcher)) {
                    val address = addressMatcher.find(line)?.groups?.get(1)?.value?.toInt() ?: 0
                    val newLine = line.replace("[${address}]", "[\${${address / MEM} * MEM}]")
                    "\"$newLine\""
                } else
                    "\"$line\""
            })
        } else {
            var i = 0
            val maxLen = asmCode.maxBy { it.length }.length
            val orig = originalCode?.split("\n")
            println(asmCode.joinToString("\n") {
                val addLen = " ".repeat(maxLen - it.length)
                val o = (orig?.getOrNull(i)?.let { "| $it" }) ?: ""
                "${String.format("%03d", i++)}    $it    $addLen$o"
            })
        }
        println("________________ ${this::class.simpleName} ________________")
        println("")
    }

    protected fun check(
        originalCode: String? = null,
        bytecode: String,
        asmCode: List<String>,
        optimize: Boolean = true
    ) {
        val p = AsmParser()
        val asm = if (optimize)
            AsmOptimizer.optimize(p.toAsmLines(bytecode))
        else
            p.toAsmLines(bytecode)
        val asmString = mutableListOf<String>()
        asm.forEach {
            if (it is ASM.RET) {
                asmString.add(it.toString())
                asmString.add("")
            } else
                asmString.add(it.toString())
        }
        printCode(asmString, originalCode, printForTestEdit)

        assert(asmCode, asmString)
    }

    protected fun assert(expected: List<String>, actual: List<String>) {
        expected.forEachIndexed { index, s ->
            try {
                assertEquals(s, actual[index])
            } catch (e: AssertionError) {
                println("------> Error line [$index]")
                throw e
            }
        }
    }
}