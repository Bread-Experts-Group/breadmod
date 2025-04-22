package org.bread_experts_group.breadmod.experimental.fake_level

import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.shorts.ShortList
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.chunk.UpgradeData
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.Heightmap.Types
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureStart
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.ticks.BlackholeTickAccess
import net.minecraft.world.ticks.TickContainerAccess
import java.util.function.BiConsumer
import java.util.function.Predicate
import kotlin.collections.MutableMap.MutableEntry

class FakeChunk(val fakeLevel: FakeLevel, x: Int, z: Int) : ChunkAccess(
	ChunkPos(x, z),
	UpgradeData.EMPTY,
	fakeLevel,
	fakeLevel.registryAccess().registryOrThrow(Registries.BIOME),
	0,
	null,
	null
) {
	private var fakeSections: Array<LevelChunkSection?> = arrayOf()
	override fun getLevel(): Level = this.fakeLevel
	private var needsLight = false

	init {
		this.fakeSections = arrayOfNulls(this.fakeLevel.sectionsCount)
		for (i in 0 ..< this.fakeLevel.sectionsCount) {
			this.fakeSections[i] = FakeChunkSection(this, i shl 4)
		}
		this.needsLight = true
	}

	override fun getBlockEntity(p0: BlockPos): BlockEntity? = this.fakeLevel.getBlockEntity(p0)
	override fun getBlockState(p0: BlockPos): BlockState = this.fakeLevel.getBlockState(p0)
	override fun getFluidState(p0: BlockPos): FluidState = this.fakeLevel.getFluidState(p0)
	override fun setBlockState(p0: BlockPos, p1: BlockState, p2: Boolean): BlockState? = null
	override fun setBlockEntity(p0: BlockEntity) {}
	override fun addEntity(p0: Entity) {}
	override fun getPersistedStatus(): ChunkStatus = ChunkStatus.LIGHT
	override fun removeBlockEntity(p0: BlockPos) {}
	override fun getBlockEntityNbtForSaving(p0: BlockPos, p1: Provider): CompoundTag? = null
	override fun getBlockTicks(): TickContainerAccess<Block> = BlackholeTickAccess.emptyContainer()
	override fun getFluidTicks(): TickContainerAccess<Fluid> = BlackholeTickAccess.emptyContainer()
	override fun getTicksForSerialization(): TicksToSave = throw UnsupportedOperationException()

	override fun findBlocks(predicate: Predicate<BlockState>, output: BiConsumer<BlockPos, BlockState>) {
		super.findBlocks(predicate, output)
	}

	override fun getSections(): Array<LevelChunkSection?> = this.fakeSections

	override fun getReferencesForStructure(structure: Structure): LongSet = LongSet.of()
	override fun addReferenceForStructure(structure: Structure, reference: Long) {}
	override fun getAllReferences(): MutableMap<Structure, LongSet> = mutableMapOf()
	override fun setAllReferences(structureReferencesMap: MutableMap<Structure, LongSet>) {}
	override fun setUnsaved(unsaved: Boolean) {}
	override fun isUnsaved(): Boolean = false
	override fun getPostProcessing(): Array<ShortList> = arrayOf(ShortList.of(0))
	override fun getBlockEntityNbt(pos: BlockPos): CompoundTag? = null
	override fun getHeightmaps(): MutableCollection<MutableEntry<Types, Heightmap>> = mutableSetOf()
	override fun setHeightmap(type: Types, data: LongArray) {}
	override fun getOrCreateHeightmapUnprimed(type: Types): Heightmap =
		Heightmap(this, Heightmap.Types.WORLD_SURFACE_WG)

	override fun getHeight(): Int = 0
	override fun getStartForStructure(structure: Structure): StructureStart? = null
	override fun setStartForStructure(structure: Structure, structureStart: StructureStart) {}
	override fun setAllStarts(structureStarts: MutableMap<Structure, StructureStart>) {}
	override fun getAllStarts(): MutableMap<Structure, StructureStart> = mutableMapOf()

	override fun getInhabitedTime(): Long = 0
	override fun setInhabitedTime(inhabitedTime: Long) {}

	override fun isLightCorrect(): Boolean = this.needsLight

	override fun setLightCorrect(lightCorrect: Boolean) {
		this.needsLight = lightCorrect
	}
}