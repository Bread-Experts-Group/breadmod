package org.bread_experts_group.breadmod.util.handlers

import java.math.BigDecimal

typealias ListenerHandler =
			(count: BigDecimal, simulate: Boolean, unitIndex: Int, additional: MutableList<Any>) -> BigDecimal?

interface HandlerListener {
	var receiveAction: ListenerHandler
	var extractAction: ListenerHandler
}