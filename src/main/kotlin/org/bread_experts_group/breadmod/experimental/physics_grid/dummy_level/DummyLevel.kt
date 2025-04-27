package org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.SectionPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEvent.Context
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraft.world.level.storage.WritableLevelData
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.ticks.LevelTickAccess

class DummyLevel(level: Level, isClientSide: Boolean) : Level(
	level.levelData as WritableLevelData,
	level.dimension(),
	level.registryAccess(),
	level.registryAccess().holderOrThrow(BuiltinDimensionTypes.OVERWORLD),
	level.profilerSupplier,
	isClientSide,
	false,
	0L,
	1000000
) {
	private val chunkSource = DummyChunkSource(this)
	private val lightEngine: LevelLightEngine = LevelLightEngine(this.chunkSource, true, true)
	val blockMap: MutableMap<BlockPos, BlockState> = mutableMapOf()
	val blockEntityMap: MutableMap<BlockPos, BlockEntity> = mutableMapOf()

	override fun getEntities(): LevelEntityGetter<Entity> = DummyEntityGetter
	override fun players(): MutableList<out Player> = mutableListOf()
	override fun getShade(direction: Direction, shade: Boolean): Float = 1f

	override fun getLightEngine(): LevelLightEngine = this.lightEngine

	override fun getUncachedNoiseBiome(x: Int, y: Int, z: Int): Holder<Biome> =
		this.registryAccess().holderOrThrow(Biomes.PLAINS)

	override fun enabledFeatures(): FeatureFlagSet = FeatureFlagSet.of()
	override fun getBlockTicks(): LevelTickAccess<Block> = DummyTickAccess.LBlock
	override fun getFluidTicks(): LevelTickAccess<Fluid> = DummyTickAccess.LFluid
	override fun getChunkSource(): DummyChunkSource = this.chunkSource

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int) {}
	override fun gameEvent(gameEvent: Holder<GameEvent>, pos: Vec3, context: Context) {}
	override fun sendBlockUpdated(pos: BlockPos, oldState: BlockState, newState: BlockState, flags: Int) {}

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
	override fun getEntity(id: Int): Entity? = null
	override fun tickRateManager(): TickRateManager = TickRateManager()
	override fun getMapData(mapId: MapId): MapItemSavedData? = null
	override fun setMapData(mapId: MapId, mapData: MapItemSavedData) {}
	override fun getFreeMapId(): MapId = MapId(0)
	override fun destroyBlockProgress(breakerId: Int, pos: BlockPos, progress: Int) {}
	override fun getScoreboard(): Scoreboard = Scoreboard()
	override fun getRecipeManager(): RecipeManager = RecipeManager(this.registryAccess())
	override fun potionBrewing(): PotionBrewing = PotionBrewing.EMPTY
	override fun setDayTimeFraction(dayTimeFraction: Float) {}
	override fun getDayTimeFraction(): Float = 0f
	override fun getDayTimePerTick(): Float = 0f
	override fun setDayTimePerTick(dayTimePerTick: Float) {
	}

	override fun getChunk(pos: BlockPos): ChunkAccess =
		this.getChunk(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z), ChunkStatus.FULL)

	override fun getBlockState(pos: BlockPos): BlockState =
		this.blockMap[pos] ?: Blocks.AIR.defaultBlockState()

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		if (this.isOutsideBuildHeight(pos)) return false
		val immutable = pos.immutable()
		val oldState = this.getBlockState(immutable)
		if (oldState == state) return false

		this.blockMap[immutable] = state
		return true
	}

	fun setBlock(pos: BlockPos, state: BlockState): Boolean = this.setBlock(pos, state, 0)

	override fun getFluidState(pos: BlockPos): FluidState =
		this.blockMap[pos]?.fluidState ?: Blocks.AIR.defaultBlockState().fluidState

	override fun getBlockEntity(pos: BlockPos): BlockEntity? = this.blockEntityMap[pos]

	override fun setBlockEntity(blockEntity: BlockEntity) {
		val pos = blockEntity.blockPos
		this.blockEntityMap[pos] = blockEntity
	}

	override fun removeBlockEntity(pos: BlockPos) {
		this.blockEntityMap.remove(pos)
	}
}