package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState

class ItemPedestalBlock : BreadModBlock(Properties.of()) {
	override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
		val itemEntity = entity as? ItemEntity ?: return
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
}