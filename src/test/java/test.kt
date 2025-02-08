import framework.input
import framework.output

fun main() {
    var x = input()
    val y = input()
    var i = 0

    while (x >= y) {
        i++
        x -= y
    }

    output(i)
    output(x)
}
