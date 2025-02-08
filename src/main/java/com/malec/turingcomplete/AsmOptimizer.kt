package com.malec.turingcomplete

object AsmOptimizer {

    fun optimize(asmLines: List<ASM>): List<ASM> {
        return asmLines
//            val storeRegex = Regex("STORE \\[(\\d+)\\], r(\\d+)")
//            val loadRegex = Regex("LOAD r(\\d+), \\[(\\d+)\\]")
//
//            val lines = mutableListOf(
//                asmLines[0]
//            )
//
//            var currentLine = asmLines[0]
//            var prevLine: String
//            var i = 1
//            while (i in 1..asmLines.lastIndex) {
//                prevLine = currentLine
//                currentLine = asmLines[i]
//
//                if (prevLine.matches(storeRegex) && currentLine.matches(loadRegex)) {
//                    val storeGroup = storeRegex.find(prevLine)?.groupValues?.drop(1) ?: listOf()
//                    val loadGroup = loadRegex.find(currentLine)?.groupValues?.drop(1) ?: listOf()
//                    if (storeGroup == loadGroup.reversed()) {
//                        //skip LOAD instruction
//                        lines.add(";Optimized; $currentLine")
//                        i++
//                        continue
//                    }
//                }
//                if (currentLine.matches(storeRegex) && prevLine.matches(loadRegex)) {
//                    val storeGroup = storeRegex.find(currentLine)?.groupValues?.drop(1) ?: listOf()
//                    val loadGroup = loadRegex.find(prevLine)?.groupValues?.drop(1) ?: listOf()
//                    if (storeGroup == loadGroup.reversed()) {
//                        //skip STORE instruction
//                        lines.add(";Optimized; $currentLine")
//                        i++
//                        continue
//                    }
//                }
//
//                //skip synthetic main
//                if (currentLine == "CALL main" && prevLine == "L0_main:") {
//                    lines.removeLast()
//                    lines.removeLast()
//                    i += 4
//                    continue
//                }
//
//                lines.add(currentLine)
//                i++
//            }
//
//            return lines
    }
}