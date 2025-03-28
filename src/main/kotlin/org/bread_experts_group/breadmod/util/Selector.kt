package org.bread_experts_group.breadmod.util

class Selector<A, B>(
	val a: A? = null,
	val b: B? = null
) {
	init {
		if (this.a == null && this.b == null) throw IllegalArgumentException("An existing option must be provided")
		if (this.a != null && this.b != null) throw IllegalArgumentException("Only one option must be given")
	}

	fun <R> select(a: (A) -> R, b: (B) -> R): R =
		if (this.a != null) a(this.a)
		else b(this.b!!)
}