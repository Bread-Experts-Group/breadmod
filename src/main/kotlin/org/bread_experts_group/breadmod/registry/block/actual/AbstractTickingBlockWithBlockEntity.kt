package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.actual.entity.AbstractTickingBlockEntity

abstract class AbstractTickingBlockWithBlockEntity(
	properties: Properties
) : Block(properties), EntityBlock {
	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T> = if (level.isClientSide)
		BlockEntityTicker<T> { clientLevel: Level, pos: BlockPos, tState: BlockState, entity: T ->
			(entity as AbstractTickingBlockEntity<*>).commonTick(clientLevel, pos, tState, entity)
			entity.clientTick(clientLevel, pos, tState, entity)
		}
	else
		BlockEntityTicker<T> { serverLevel: Level, pos: BlockPos, tState: BlockState, entity: T ->
			(entity as AbstractTickingBlockEntity<*>).commonTick(serverLevel, pos, tState, entity)
			entity.serverTick(serverLevel, pos, tState, entity)
		}
}