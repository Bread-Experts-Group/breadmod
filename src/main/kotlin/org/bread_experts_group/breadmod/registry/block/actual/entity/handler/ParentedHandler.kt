package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

sealed class ParentedHandler<T : Any> {
	lateinit var parent: T
	protected fun ParentedHandler<BreadModBlockEntity>.stateUpdated() {
		(this.parent.blockState.block as BreadModBlock).synchronizeEntity(this.parent)
	}
}