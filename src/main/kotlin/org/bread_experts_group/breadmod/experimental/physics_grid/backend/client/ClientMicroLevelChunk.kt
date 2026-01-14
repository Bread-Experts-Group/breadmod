package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toPoint3
import org.bread_experts_group.numeric.geometry.point.Point3

class ClientMicroLevelChunk(val parent: ClientMicroLevel) : LevelChunk(parent, ChunkPos.ZERO) {
	val blocks: MutableMap<Point3<Int>, BlockState> = mutableMapOf()
	override fun getBlockState(pos: BlockPos): BlockState =
		this.blocks[pos.toPoint3()] ?: Blocks.AIR.defaultBlockState()

	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState()
	override fun setBlockState(pos: BlockPos, state: BlockState, isMoving: Boolean): BlockState? {
		val newState = this.blocks.put(pos.toPoint3(), state)

		this.getBlockEntity(pos)?.let { blockEntity ->
			if (!blockEntity.isValidBlockState(state)) this.removeBlockEntity(pos)
		}
//		if (state.hasBlockEntity()) {
//			val blockEntity = this.getBlockEntity(pos, EntityCreationType.CHECK)
//			if (blockEntity != null && !blockEntity.isValidBlockState(state))
//				this.removeBlockEntity(pos)
//		}
//		return super.setBlockState(pos, state, isMoving) TODO
		return newState ?: Blocks.AIR.defaultBlockState()
	}
}