package org.bread_experts_group.breadmod.registry.block.handler

import net.minecraft.core.component.DataComponentMap
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput

interface DataComponentSerializable {
	fun serializeDataComponent(map: DataComponentMap.Builder)
	fun deserializeDataComponent(from: DataComponentInput)
	fun collectHoverText(tooltipComponents: MutableList<Component>) {}
}