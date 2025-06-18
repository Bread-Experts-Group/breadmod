package org.bread_experts_group.breadmod.util.handlers

import java.math.BigDecimal

interface HandlerLimits {
	var capacity: BigDecimal?
	var maxIn: BigDecimal?
	var maxOut: BigDecimal?
	var amount: BigDecimal

	fun fillDecimal(
		count: BigDecimal,
		simulate: Boolean,
		additional: MutableList<Any> = mutableListOf()
	): Pair<BigDecimal, List<Any>> {
		val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
		val sum = (this.amount + actualCount).let { this.capacity?.let(it::min) ?: it }
		val saved = this.amount
		val delta = sum - saved
		if (!simulate && delta > BigDecimal.ZERO) {
			this.amount = sum
		}
		return delta to mutableListOf()
	}

	fun fillDecimal(count: BigDecimal, simulate: Boolean): Pair<BigDecimal, List<Any>> =
		this.fillDecimal(count, simulate, mutableListOf())

	fun drainDecimal(
		count: BigDecimal,
		simulate: Boolean,
		additional: MutableList<Any> = mutableListOf()
	): Pair<BigDecimal, List<Any>> {
		val toRemove = (if (this.maxOut != null) count.min(this.maxOut) else count).min(this.amount)
		if (!simulate) this.amount -= toRemove
		return toRemove to mutableListOf()
	}

	fun drainDecimal(count: BigDecimal, simulate: Boolean): Pair<BigDecimal, List<Any>> =
		this.drainDecimal(count, simulate, mutableListOf())
}