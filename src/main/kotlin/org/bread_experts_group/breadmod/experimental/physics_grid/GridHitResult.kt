package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GridHitResult(
	val relativePos: Vec3,
	val shape: VoxelShape,
	hitResult: BlockHitResult
) : BlockHitResult(hitResult.location, hitResult.direction, hitResult.blockPos, false) {
	companion object {
		fun miss(location: Vec3): GridHitResult = GridHitResult(
			location, Shapes.block(),
			BlockHitResult.miss(location, Direction.NORTH, BlockPos.ZERO),
		)
	}
}