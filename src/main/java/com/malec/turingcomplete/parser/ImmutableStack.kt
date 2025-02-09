package com.malec.turingcomplete.parser

import java.util.*

class ImmutableStack<T>() {

    constructor(other: ImmutableStack<T>) : this() {
        stack = other.stack.clone() as Stack<T>
    }

    private var stack = Stack<T>()

    fun peek() = stack.peek()

    fun peek2(): Pair<T, T> {
        val a=stack[stack.lastIndex - 1] to stack[stack.lastIndex]
        return a
    }

    fun push(item: T): ImmutableStack<T> {
        val s = ImmutableStack<T>()
        s.stack.push(item)
        return s
    }

    fun pop(): Pair<ImmutableStack<T>, T> {
        val s = ImmutableStack(this)
        val item = s.stack.pop()
        return s to item
    }

    val size: Int
        get() = stack.size

    val isEmpty: Boolean
        get() = stack.isEmpty()

    val isNotEmpty: Boolean
        get() = stack.isNotEmpty()
}