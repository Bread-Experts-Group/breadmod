package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class GridHitResult(
	localHit: Vec3,
	direction: Direction,
	val pos: BlockPos,
	val state: BlockState
) : BlockHitResult(localHit, direction, pos, false) {
	override fun toString(): String =
		"GridHitResult[pos=${this.pos}, state=${this.state.block}, direction=${this.direction}]"
}