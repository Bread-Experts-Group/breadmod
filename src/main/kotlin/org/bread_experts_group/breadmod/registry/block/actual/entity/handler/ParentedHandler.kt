package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

sealed class ParentedHandler<T : Any> {
	lateinit var parent: T
}