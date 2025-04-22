package org.bread_experts_group.breadmod.experimental.fake_level

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.material.FluidState

class FakeChunkSection(private val chunk: FakeChunk, private val yStart: Int) : LevelChunkSection(
	chunk.fakeLevel.registryAccess().registryOrThrow(Registries.BIOME)
) {
	private val xStart: Int = this.chunk.pos.minBlockX
	private val zStart: Int = this.chunk.pos.minBlockZ
	override fun getBlockState(x: Int, y: Int, z: Int): BlockState =
		this.chunk.fakeLevel.getBlockState(x + this.xStart, y + this.yStart, z + this.zStart)

	override fun getFluidState(x: Int, y: Int, z: Int): FluidState = this.getBlockState(x, y, z).fluidState
	override fun setBlockState(x: Int, y: Int, z: Int, state: BlockState, useLocks: Boolean): BlockState =
		throw UnsupportedOperationException()
}