package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class DieselGeneratorBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<DieselGeneratorBlockEntity>(ModBlockEntityTypes.DIESEL_GENERATOR.get(), pos, state) {
}