package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractTickingBlockEntity<T : AbstractTickingBlockEntity<T>>(
	type: BlockEntityType<T>,
	pos: BlockPos,
	blockState: BlockState
) : BlockEntity(type, pos, blockState) {
	open fun commonTick(
		clientLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	): Unit = Unit

	open fun clientTick(
		clientLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	): Unit = Unit

	open fun serverTick(
		serverLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	): Unit = Unit
}