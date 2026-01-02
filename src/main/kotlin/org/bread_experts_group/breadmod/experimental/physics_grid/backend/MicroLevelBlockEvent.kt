package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block

data class MicroLevelBlockEvent(
	val pos: BlockPos,
	val block: Block,
	val eventID: Int,
	val eventParam: Int
)