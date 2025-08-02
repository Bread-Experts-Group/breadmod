package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

interface ParentedHandler<T : Any> {
	var parent: T
	val stateListeners: MutableList<() -> Unit>
	fun ParentedHandler<BreadModBlockEntity>.stateUpdated() {
		(this.parent.blockState.block as BreadModBlock).synchronizeEntity(this.parent)
		this.stateListeners.forEach { it() }
	}

	fun parentReady() {}
}