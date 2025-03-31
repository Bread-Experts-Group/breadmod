package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class DoubleOrNothingBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BlockEntity(ModBlockEntityTypes.DOUBLE_OR_NOTHING.get(), pos, state) {
	private val random = RandomSource.create()
}