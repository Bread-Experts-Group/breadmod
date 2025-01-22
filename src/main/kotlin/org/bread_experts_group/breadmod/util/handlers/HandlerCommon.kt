package org.bread_experts_group.breadmod.util.handlers

import java.math.BigDecimal

object HandlerCommon {
	fun HandlerLimits.calculateSum(count: BigDecimal): Pair<BigDecimal, BigDecimal> {
		val actualCount = if (this.maxIn != null) count.min(this.maxIn) else count
		return (this.amount + actualCount).let { this.capacity?.let { c -> it.min(c) } ?: it } to this.amount
	}

	fun HandlerLimits.calculateAndSave(count: BigDecimal, simulate: Boolean): BigDecimal {
		val (sum, saved) = this.calculateSum(count)
		if (!simulate) this.amount = sum
		return sum - saved
	}
}