package org.bread_experts_group.breadmod.network

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import java.util.Objects

class KotlinPairCodec<F, S>(val first: Codec<F>, val second: Codec<S>) : Codec<Pair<F, S>> {
	override fun <T : Any> encode(
		input: Pair<F, S>,
		ops: DynamicOps<T>,
		prefix: T
	): DataResult<T> =
		this.second.encode(input.second, ops, prefix).flatMap {
			this.first.encode(input.first, ops, it)
		}

	override fun <T : Any> decode(
		ops: DynamicOps<T>,
		input: T
	): DataResult<com.mojang.datafixers.util.Pair<Pair<F, S>, T>> =
		this.first.decode(ops, input).flatMap { p1 ->
			this.second.decode(ops, input).map { p2 ->
				com.mojang.datafixers.util.Pair.of(p1.first to p2.first, p2.second)
			}
		}

	@Suppress("ImplicitThis")
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this::class.java != other::class.java) return false
		val pairCodec = other as? KotlinPairCodec<*, *>
		return Objects.equals(this.first, pairCodec?.first) &&
				Objects.equals(this.second, pairCodec?.second)
	}

	override fun hashCode(): Int = Objects.hash(this.first, this.second)

	override fun toString(): String = "KotlinPairCodec[${this.first}, ${this.second}]"
}