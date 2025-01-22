package org.bread_experts_group.breadmod.util.handlers

import java.math.BigDecimal

interface HandlerListener {
	var receiveAction: (count: BigDecimal, simulate: Boolean, divisionIndex: Int) -> BigDecimal?
	var extractAction: (count: BigDecimal, simulate: Boolean, divisionIndex: Int) -> BigDecimal?
}