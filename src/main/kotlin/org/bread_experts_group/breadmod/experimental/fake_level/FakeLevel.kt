package org.bread_experts_group.breadmod.experimental.fake_level

import it.unimi.dsi.fastutil.objects.Object2ShortMap
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.SectionPos
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkSource
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEvent.Context
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraft.world.level.storage.WritableLevelData
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.ticks.LevelTickAccess

class FakeLevel(
	val level: Level,
	private val minBuildHeight: Int,
	private val height: Int,
	private val biomeOffset: Vec3i = Vec3i.ZERO
) : Level(
	level.levelData as WritableLevelData,
	level.dimension(),
	level.registryAccess(),
	level.dimensionTypeRegistration(),
	level.profilerSupplier,
	true,
	false,
	0,
	0
) {
	private val entityGetter: FakeEntityGetter<Entity> = FakeEntityGetter()
	private val chunkSource = FakeChunkSource(this)
	private val lightEngine = LevelLightEngine(this.chunkSource, true, false)
	val nonemptyBlocks: Object2ShortMap<SectionPos> = Object2ShortOpenHashMap()
	val blockStates: MutableMap<BlockPos, BlockState> = hashMapOf()
	val blockEntities: MutableMap<BlockPos, BlockEntity> = hashMapOf()

	override fun getMinBuildHeight(): Int = this.minBuildHeight
	override fun getHeight(): Int = this.height

	fun setBlockEntities(blockEntities: MutableList<BlockEntity>) {
		this.blockEntities.clear()
		blockEntities.forEach(this::setBlockEntity)
	}

	fun runLightEngine() {
		val chunkPosSet: MutableSet<ChunkPos> = ObjectOpenHashSet()
		this.nonemptyBlocks.object2ShortEntrySet().forEach { entry ->
			if (entry.shortValue > 0) chunkPosSet.add(entry.key.chunk())
		}
		for (pos in chunkPosSet) this.lightEngine.propagateLightSources(pos)
		this.lightEngine.runLightUpdates()
	}

	fun clear() {
		this.blockStates.clear()
		this.blockEntities.clear()
		this.nonemptyBlocks.forEach { (sectionPos, nonEmptyBlockCount) ->
			if (nonEmptyBlockCount > 0) this.lightEngine.updateSectionStatus(sectionPos, true)
		}
		this.nonemptyBlocks.clear()
		this.runLightEngine()
	}

	override fun setBlockEntity(blockEntity: BlockEntity) {
		val pos = blockEntity.blockPos
		if (!this.isOutsideBuildHeight(pos)) {
			this.blockEntities[pos] = blockEntity
		}
	}

	override fun removeBlockEntity(pos: BlockPos) {
		if (!this.isOutsideBuildHeight(pos)) this.blockStates.remove(pos)
	}

	override fun getBlockState(pos: BlockPos): BlockState {
		if (this.isOutsideBuildHeight(pos)) return Blocks.VOID_AIR.defaultBlockState()
		return this.blockStates[pos] ?: Blocks.AIR.defaultBlockState()
	}

	override fun getFluidState(pos: BlockPos): FluidState {
		if (this.isOutsideBuildHeight(pos)) return Fluids.EMPTY.defaultFluidState()
		return this.getBlockState(pos).fluidState
	}

	fun setBlock(pos: BlockPos, state: BlockState): Boolean = this.setBlock(pos, state, 0)

	override fun setBlock(pos: BlockPos, newState: BlockState, flags: Int): Boolean {
		if (this.isOutsideBuildHeight(pos)) return false
		val immutable = pos.immutable()
		val oldState = this.getBlockState(immutable)
		if (oldState == newState) return false

		this.blockStates[immutable] = newState
		val section = SectionPos.of(immutable)
		var nonEmptyBlocks = this.nonemptyBlocks.getShort(section)
		val prevEmpty: Boolean = nonEmptyBlocks.toInt() == 0
		if (!oldState.isAir) nonEmptyBlocks--
		if (!newState.isAir) nonEmptyBlocks++
		this.nonemptyBlocks[section] = nonEmptyBlocks
		val nowEmpty = nonEmptyBlocks.toInt() == 0
		if (prevEmpty != nowEmpty) this.lightEngine.updateSectionStatus(section, nowEmpty)

		this.lightEngine.checkBlock(immutable)
		return true
	}

	override fun getChunk(pos: BlockPos): ChunkAccess =
		this.getChunk(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z), ChunkStatus.FULL)

	fun getBlockState(x: Int, y: Int, z: Int): BlockState = this.getBlockState(BlockPos.MutableBlockPos().set(x, y, z))

	override fun getLightEngine(): LevelLightEngine = this.lightEngine

	override fun getEntities(): LevelEntityGetter<Entity> = this.entityGetter

	override fun players(): MutableList<out Player> = mutableListOf()

	override fun getShade(direction: Direction, shade: Boolean): Float = 1f

	override fun getUncachedNoiseBiome(x: Int, y: Int, z: Int): Holder<Biome> =
		this.level.getUncachedNoiseBiome(x + this.biomeOffset.x, y + this.biomeOffset.y, z + this.biomeOffset.z)

	override fun getNoiseBiome(x: Int, y: Int, z: Int): Holder<Biome> =
		this.level.getNoiseBiome(x + this.biomeOffset.x, y + this.biomeOffset.y, z + this.biomeOffset.z)

	override fun enabledFeatures(): FeatureFlagSet = this.level.enabledFeatures()

	override fun getBlockTicks(): LevelTickAccess<Block> = this.level.blockTicks
	override fun getFluidTicks(): LevelTickAccess<Fluid> = this.level.fluidTicks
	override fun getChunkSource(): ChunkSource = this.chunkSource
	override fun levelEvent(p0: Player?, p1: Int, p2: BlockPos, p3: Int) {}
	override fun gameEvent(p0: Holder<GameEvent>, p1: Vec3, p2: Context) {}
	override fun sendBlockUpdated(p0: BlockPos, p1: BlockState, p2: BlockState, p3: Int) {}

	override fun playSeededSound(
		player: Player?,
		x: Double,
		y: Double,
		z: Double,
		sound: Holder<SoundEvent>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	) {
	}

	override fun getMaxBuildHeight(): Int = this.minBuildHeight + this.height
	override fun getSectionsCount(): Int = this.maxSection - this.minSection
	override fun getMaxSection(): Int = SectionPos.blockToSectionCoord((this.maxBuildHeight - 1) + 1)
	override fun getMinSection(): Int = SectionPos.blockToSectionCoord(this.minBuildHeight)
	override fun isOutsideBuildHeight(pos: BlockPos): Boolean = this.isOutsideBuildHeight(pos.y)
	override fun isOutsideBuildHeight(y: Int): Boolean = y < this.minBuildHeight || y >= this.maxBuildHeight
	override fun getSectionIndex(y: Int): Int = this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(y))
	override fun getSectionIndexFromSectionY(sectionIndex: Int): Int = sectionIndex - this.minSection
	override fun getSectionYFromSectionIndex(sectionIndex: Int): Int = sectionIndex + this.minSection

	override fun playSeededSound(
		player: Player?,
		entity: Entity,
		sound: Holder<SoundEvent>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	) {
	}

	override fun gatherChunkSourceStats(): String = ""
	override fun getEntity(p0: Int): Entity? = null
	override fun tickRateManager(): TickRateManager = this.level.tickRateManager()
	override fun getMapData(p0: MapId): MapItemSavedData? = null
	override fun setMapData(p0: MapId, p1: MapItemSavedData) {}
	override fun getFreeMapId(): MapId = MapId(0)
	override fun destroyBlockProgress(p0: Int, p1: BlockPos, p2: Int) {}
	override fun getScoreboard(): Scoreboard = Scoreboard()
	override fun getRecipeManager(): RecipeManager = this.level.recipeManager
	override fun potionBrewing(): PotionBrewing = this.level.potionBrewing()

	override fun setDayTimeFraction(p0: Float) {
		this.level.dayTimeFraction = p0
	}

	override fun getDayTimeFraction(): Float = this.level.dayTimeFraction
	override fun getDayTimePerTick(): Float = this.level.dayTimePerTick

	override fun setDayTimePerTick(p0: Float) {
		this.level.dayTimePerTick = p0
	}
}