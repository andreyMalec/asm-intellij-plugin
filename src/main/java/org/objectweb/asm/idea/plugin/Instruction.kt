package org.objectweb.asm.idea.plugin

sealed interface Instruction {
	data class Register(val index: Int) : Instruction {
		override fun toString(): String {
			return "r$index"
		}
	}

	data class Value(val value: Int) : Instruction {
		override fun toString(): String {
			return value.toString()
		}
	}
}
