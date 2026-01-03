package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

class ClientMicroLevelChunk(val parent: ClientMicroLevel) : LevelChunk(parent, ChunkPos.ZERO) {
	override fun getBlockState(pos: BlockPos): BlockState = Blocks.AIR.defaultBlockState()
	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState()
}