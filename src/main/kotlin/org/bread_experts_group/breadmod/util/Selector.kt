package org.bread_experts_group.breadmod.util

class Selector<A, B>(
	val a: A? = null,
	val b: B? = null
) {
	init {
		require(!(this.a == null && this.b == null)) { "An existing option must be provided" }
		require(!(this.a != null && this.b != null)) { "Only one option must be given" }
	}

	fun <R> select(a: (A) -> R, b: (B) -> R): R? {
		return if (this.a != null) a(this.a)
		else b(this.b ?: return null)
	}
}