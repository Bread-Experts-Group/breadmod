package org.bread_experts_group.breadmod.util.handlers

import java.math.BigDecimal

interface HandlerLimits {
	var capacity: BigDecimal?
	var maxIn: BigDecimal?
	var maxOut: BigDecimal?
	var amount: BigDecimal
}