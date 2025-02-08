package framework

import kotlin.random.Random

fun input(): Int {
    val a = Random.nextInt(256)
    println("Input = $a")
    return a
}

fun output(x: Int) {
    println("Output = $x")
}