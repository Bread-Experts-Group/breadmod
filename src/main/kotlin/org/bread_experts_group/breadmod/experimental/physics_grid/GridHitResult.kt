package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class GridHitResult(
	localHit: Vec3,
	direction: Direction,
	val localPos: BlockPos,
	val state: BlockState
) : BlockHitResult(localHit, direction, localPos, false) {
	override fun getType(): Type = Type.BLOCK

	override fun toString(): String =
		"GridHitResult[pos=${this.localPos}, state=${this.state.block}, direction=${this.direction}]"
}