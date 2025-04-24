package org.bread_experts_group.breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

class GridBlockHitResult(
	location: Vec3,
	direction: Direction,
	blockPos: BlockPos,
	val grid: PhysicsGrid,
	val localBlockPos: BlockPos,
	val state: BlockState
) : BlockHitResult(location, direction, blockPos, true) {
	val locationGridRelative: Vec3 = Vec3.atLowerCornerOf(this.localBlockPos) + this.grid.position

	override fun getBlockPos(): BlockPos = this.localBlockPos
}