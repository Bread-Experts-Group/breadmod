package org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.UpgradeData
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.ticks.TickContainerAccess

class DummyChunk(x: Int, z: Int, private val level: DummyLevel) : ChunkAccess(
	ChunkPos(x, z),
	UpgradeData.EMPTY,
	level,
	level.registryAccess().registryOrThrow(Registries.BIOME),
	0,
	null,
	null
) {
	private var dummySections: Array<DummyChunkSection?> = arrayOf()

	init {
		this.dummySections = arrayOfNulls(this.level.sectionsCount)
		for (i in 0 ..< this.level.sectionsCount) {
			this.dummySections[i] = DummyChunkSection(this, i shl 4)
		}
	}

	override fun getBlockEntity(pos: BlockPos): BlockEntity? = this.level.getBlockEntity(pos)
	override fun getLevel(): DummyLevel = this.level
	override fun getBlockState(pos: BlockPos): BlockState = this.level.getBlockState(pos)
	override fun getFluidState(pos: BlockPos): FluidState = this.level.getFluidState(pos)
	override fun setBlockState(pos: BlockPos, state: BlockState, isMoving: Boolean): BlockState? = null
	override fun setBlockEntity(blockEntity: BlockEntity): Unit = this.level.setBlockEntity(blockEntity)
	override fun addEntity(entity: Entity) {}
	override fun getPersistedStatus(): ChunkStatus = ChunkStatus.LIGHT
	override fun removeBlockEntity(pos: BlockPos): Unit = this.level.removeBlockEntity(pos)
	override fun getSections(): Array<DummyChunkSection?> = this.dummySections

	override fun getBlockEntityNbtForSaving(pos: BlockPos, registries: Provider): CompoundTag? = null
	override fun getBlockTicks(): TickContainerAccess<Block> = DummyTickAccess.TBlock
	override fun getFluidTicks(): TickContainerAccess<Fluid> = DummyTickAccess.TFluid
	override fun getTicksForSerialization(): TicksToSave = throw UnsupportedOperationException()
}