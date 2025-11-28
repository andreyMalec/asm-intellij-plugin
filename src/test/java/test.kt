fun main() {
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