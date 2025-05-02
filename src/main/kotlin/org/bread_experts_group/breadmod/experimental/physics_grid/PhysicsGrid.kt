package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.datafixers.DataFixerUpper
import com.mojang.serialization.MapCodec
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.IntSortedSets
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerScoreboard
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ThreadedLevelLightEngine
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.server.level.progress.ChunkProgressListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.AbortableIterationConsumer
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.util.profiling.metrics.MetricCategory
import net.minecraft.world.Difficulty
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.GameType.SURVIVAL
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder.Settings
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.entity.ChunkStatusUpdateListener
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEvent.Context
import net.minecraft.world.level.levelgen.FlatLevelSource
import net.minecraft.world.level.levelgen.GenerationStep.Carving
import net.minecraft.world.level.levelgen.Heightmap.Types
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraft.world.level.storage.DimensionDataStorage
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraft.world.level.timers.TimerCallbacks
import net.minecraft.world.level.timers.TimerQueue
import net.minecraft.world.level.validation.DirectoryValidator
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.ticks.LevelTicks
import net.neoforged.fml.loading.FMLPaths
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.unaryMinus
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.pow

@Suppress("UnstableApiUsage")
abstract class PhysicsGrid protected constructor(level: Level, posA: BlockPos, posB: BlockPos) : ServerLevel(
	level.server!!,
	Executors.newVirtualThreadPerTaskExecutor(),
	Companion.STORAGE_ACCESS,
	Companion.createLevelData(level),
	Level.OVERWORLD,
	LevelStem(
		level.registryAccess().holderOrThrow(BuiltinDimensionTypes.OVERWORLD),
		Companion.createChunkGenerator(level)
	),
	Companion.PROGRESS_LISTENER,
	false,
	0L,
	emptyList(),
	true,
	null
), LevelEntityGetter<Entity> {
	companion object {
		val PHYSICS_GRID_PATH: Path = FMLPaths.GAMEDIR.get().resolve("PhysicsGrid")
		val BLANK_FIXER_UPPER: DataFixerUpper =
			object : DataFixerUpper(Int2ObjectAVLTreeMap(), mutableListOf(), IntSortedSets.EMPTY_SET) {}
		val STORAGE_ACCESS: LevelStorageAccess = LevelStorageSource(
			Companion.PHYSICS_GRID_PATH,
			Companion.PHYSICS_GRID_PATH.resolve("backup"),
			DirectoryValidator { it.endsWith(Companion.PHYSICS_GRID_PATH.resolve("validator")) },
			Companion.BLANK_FIXER_UPPER
		).createAccess("breadmod_highly_experimental")

		fun createLevelData(level: Level): ServerLevelData = object : ServerLevelData {
			override fun getSpawnPos(): BlockPos = BlockPos.ZERO
			override fun getSpawnAngle(): Float = 0f
			override fun getGameTime(): Long = 0
			override fun getDayTime(): Long = 0
			override fun isThundering(): Boolean = false
			override fun isRaining(): Boolean = false
			override fun setRaining(raining: Boolean) {}
			override fun isHardcore(): Boolean = false
			override fun getGameRules(): GameRules = GameRules()
			override fun getDifficulty(): Difficulty = Difficulty.PEACEFUL
			override fun isDifficultyLocked(): Boolean = false
			override fun setSpawn(spawnPoint: BlockPos, spawnAngle: Float) {}
			override fun getLevelName(): String = "PhysicsGridLevel"
			override fun setThundering(thundering: Boolean) {}
			override fun getRainTime(): Int = 0
			override fun setRainTime(time: Int) {}
			override fun setThunderTime(time: Int) {}
			override fun getThunderTime(): Int = 0
			override fun getClearWeatherTime(): Int = 0
			override fun setClearWeatherTime(time: Int) {}
			override fun getWanderingTraderSpawnDelay(): Int = 0
			override fun setWanderingTraderSpawnDelay(delay: Int) {}
			override fun getWanderingTraderSpawnChance(): Int = 0
			override fun setWanderingTraderSpawnChance(chance: Int) {}
			override fun getWanderingTraderId(): UUID? = null
			override fun setWanderingTraderId(id: UUID) {}
			override fun getGameType(): GameType = SURVIVAL
			override fun setWorldBorder(serializer: Settings) {}
			override fun getWorldBorder(): Settings = level.worldBorder.createSettings()
			override fun isInitialized(): Boolean = false
			override fun setInitialized(initialized: Boolean) {}
			override fun isAllowCommands(): Boolean = false
			override fun setGameType(type: GameType) {}
			override fun getScheduledEvents(): TimerQueue<MinecraftServer> = TimerQueue(TimerCallbacks())
			override fun setGameTime(time: Long) {}
			override fun setDayTime(time: Long) {}
			override fun getDayTimeFraction(): Float = 0f
			override fun getDayTimePerTick(): Float = 0f
			override fun setDayTimeFraction(dayTimeFraction: Float) {}
			override fun setDayTimePerTick(dayTimePerTick: Float) {}
		}

		val PROGRESS_LISTENER: ChunkProgressListener = object : ChunkProgressListener {
			override fun updateSpawnPos(center: ChunkPos) {}
			override fun onStatusChange(chunkPos: ChunkPos, chunkStatus: ChunkStatus?) {}
			override fun start() {}
			override fun stop() {}
		}

		fun createChunkGenerator(level: Level): ChunkGenerator =
			object : ChunkGenerator(FixedBiomeSource(level.registryAccess().holderOrThrow(Biomes.PLAINS))) {
				override fun codec(): MapCodec<out ChunkGenerator> = FlatLevelSource.CODEC

				override fun applyCarvers(
					level: WorldGenRegion,
					seed: Long,
					random: RandomState,
					biomeManager: BiomeManager,
					structureManager: StructureManager,
					chunk: ChunkAccess,
					step: Carving
				) {
				}

				override fun buildSurface(
					level: WorldGenRegion,
					structureManager: StructureManager,
					random: RandomState,
					chunk: ChunkAccess
				) {
				}

				override fun spawnOriginalMobs(level: WorldGenRegion) {}
				override fun getGenDepth(): Int = 0

				override fun fillFromNoise(
					blender: Blender,
					randomState: RandomState,
					structureManager: StructureManager,
					chunk: ChunkAccess
				): CompletableFuture<ChunkAccess> = CompletableFuture.completedFuture(chunk)

				override fun getSeaLevel(): Int = 0

				override fun getMinY(): Int = -64

				override fun getBaseHeight(
					x: Int,
					z: Int,
					type: Types,
					level: LevelHeightAccessor,
					random: RandomState
				): Int = 0

				override fun getBaseColumn(
					x: Int,
					z: Int,
					height: LevelHeightAccessor,
					random: RandomState
				): NoiseColumn = NoiseColumn(0, arrayOf())

				override fun addDebugScreenInfo(info: MutableList<String>, random: RandomState, pos: BlockPos) {}
			}
	}

	val localChunkSource: ServerChunkCache = object : ServerChunkCache(
		this@PhysicsGrid,
		Companion.STORAGE_ACCESS,
		Companion.BLANK_FIXER_UPPER,
		StructureTemplateManager(
			ResourceManager.Empty.INSTANCE,
			Companion.STORAGE_ACCESS,
			Companion.BLANK_FIXER_UPPER,
			BuiltInRegistries.BLOCK.asLookup()
		),
		Executor { },
		Companion.createChunkGenerator(this@PhysicsGrid),
		10,
		10,
		false,
		Companion.PROGRESS_LISTENER,
		ChunkStatusUpdateListener { _, _ -> },
		{
			DimensionDataStorage(
				Companion.PHYSICS_GRID_PATH.resolve("storage").toFile(),
				Companion.BLANK_FIXER_UPPER,
				level.registryAccess()
			)
		}
	) {
		inner class LocalChunk(x: Int, z: Int) : LevelChunk(this@PhysicsGrid, ChunkPos(x, z))

		val chunks: MutableMap<Long, LocalChunk> = mutableMapOf()
		override fun getLevel(): Level = this@PhysicsGrid
		override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess =
			this.chunks.getOrPut((x.toLong() shl 32) or z.toLong()) { this.LocalChunk(x, z) }

		override fun tick(hasTimeLeft: BooleanSupplier, tickChunks: Boolean): Unit =
			throw UnsupportedOperationException()

		override fun gatherStats(): String = throw UnsupportedOperationException()
		override fun getLoadedChunksCount(): Int = throw UnsupportedOperationException()
		override fun getLightEngine(): ThreadedLevelLightEngine = this@PhysicsGrid.localLightEngine
	}
	val localLightEngine: ThreadedLevelLightEngine = ThreadedLevelLightEngine(
		this.localChunkSource,
		null,
		true,
		null,
		null
	)
	val localProfilerFiller: ProfilerFiller = object : ProfilerFiller {
		override fun startTick() {
			TODO("Not yet implemented")
		}

		override fun endTick() {
			TODO("Not yet implemented")
		}

		override fun push(name: String) {
			TODO("Not yet implemented")
		}

		override fun push(nameSupplier: Supplier<String?>) {
			TODO("Not yet implemented")
		}

		override fun pop() {
			TODO("Not yet implemented")
		}

		override fun popPush(name: String) {
			TODO("Not yet implemented")
		}

		override fun popPush(nameSupplier: Supplier<String?>) {
			TODO("Not yet implemented")
		}

		override fun markForCharting(category: MetricCategory) {
			TODO("Not yet implemented")
		}

		override fun incrementCounter(counterName: String, increment: Int) {
			TODO("Not yet implemented")
		}

		override fun incrementCounter(
			counterNameSupplier: Supplier<String?>,
			increment: Int
		) {
			TODO("Not yet implemented")
		}
	}
	val localTickManager: TickRateManager = TickRateManager()
	val localBlockTicker: LevelTicks<Block> = LevelTicks({ true }, this::localProfilerFiller)
	val id: Int = ++PhysicsGridGlobals.idCounter
	val logger: Logger = LogManager.getLogger("PhysicsGrid ${this.id}")
	val voxelShapes: MutableMap<BlockPos, VoxelShape> = mutableMapOf()
	var boundingBox: AABB = AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)
	var position: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var velocity: Vec3 = Vec3.ZERO

	init {
		val aabb = AABB.encapsulatingFullBlocks(posA, posB)
		// Populating Block and VoxelShape Data
		BlockPos.betweenClosedStream(aabb).forEach { pos ->
			val immutablePos = pos.immutable()
			val state = level.getBlockState(immutablePos)
			val offset = immutablePos.offset(-posA)
			if (state.renderShape == INVISIBLE) return@forEach
			this.setBlock(offset, state, 0)
			this.voxelShapes[offset] = state.getShape(this, pos)
			val blockEntity = level.getBlockEntity(pos)
			if (blockEntity != null) this.setBlockEntity(blockEntity)
		}
		// Recomputing Grid Position and BoundingBox
		this.position = posA.toVec3()
		this.boundingBox = aabb
	}

	fun getWorldVoxelShapes(): List<VoxelShape> = this.voxelShapes.map { (local, shape) ->
		shape.move(
			local.x + this.position.x,
			local.y + this.position.y,
			local.z + this.position.z
		)
	}

	open fun setPos(newPos: Vec3): PhysicsGrid {
		this.position -= this.position - newPos
		return this
	}

	private val airDensity: Double = 1.225
	private val dragCoefficient: Double = 1.05
	private val crossSectionArea: Int = 25 // TODO calculate
	private val mass: Int = 1 // TODO calculate
	open fun tick() {
		val dragAcceleration = (0.5 * this.airDensity * this.dragCoefficient * this.crossSectionArea *
				this.velocity.length().pow(2.0)) / this.mass
		this.velocity = this.velocity.subtract(this.velocity.scale(dragAcceleration / 20))
		this.position = this.position.add(this.velocity)
		this.localBlockTicker.tick(this.gameTime, 65536) { pos: BlockPos, block: Block ->
			val blockstate = this.getBlockState(pos)
			if (blockstate.`is`(block)) blockstate.tick(this, pos, this.random)
		}
//		this.fluidTicks.tick(k, 65536, BiConsumer { pos: BlockPos?, fluid: Fluid? -> this.tickFluid(pos, fluid) })
	}

	override fun getEntities(): LevelEntityGetter<Entity> = this
	override fun getShade(direction: Direction, shade: Boolean): Float {
		if (!shade) return 1f
		return when (direction) {
			Direction.DOWN                 -> 0.5f
			Direction.UP                   -> 1.0f
			Direction.NORTH, Direction.SOUTH -> 0.8f
			Direction.WEST, Direction.EAST -> 0.6f
		}
	}

	override fun getLightEngine(): LevelLightEngine = this.localLightEngine
	override fun getUncachedNoiseBiome(x: Int, y: Int, z: Int): Holder<Biome> = throw UnsupportedOperationException()
	override fun enabledFeatures(): FeatureFlagSet = throw UnsupportedOperationException()
	override fun getBlockTicks(): LevelTicks<Block> = this.localBlockTicker
	override fun getFluidTicks(): LevelTicks<Fluid> = throw UnsupportedOperationException()
	override fun getChunkSource(): ServerChunkCache = this.localChunkSource

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int): Unit =
		throw UnsupportedOperationException()

	override fun gameEvent(gameEvent: Holder<GameEvent>, pos: Vec3, context: Context): Unit =
		throw UnsupportedOperationException()

	override fun sendBlockUpdated(pos: BlockPos, oldState: BlockState, newState: BlockState, flags: Int): Unit =
		throw UnsupportedOperationException()

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
	): Unit = throw UnsupportedOperationException()

	override fun playSeededSound(
		player: Player?,
		entity: Entity,
		sound: Holder<SoundEvent>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	): Unit = throw UnsupportedOperationException()

	override fun gatherChunkSourceStats(): String = throw UnsupportedOperationException()
	override fun getEntity(id: Int): Entity = throw UnsupportedOperationException()
	override fun tickRateManager(): TickRateManager = this.localTickManager
	override fun getMapData(mapId: MapId): MapItemSavedData = throw UnsupportedOperationException()
	override fun setMapData(mapId: MapId, mapData: MapItemSavedData): Unit = throw UnsupportedOperationException()
	override fun getFreeMapId(): MapId = throw UnsupportedOperationException()
	override fun destroyBlockProgress(breakerId: Int, pos: BlockPos, progress: Int): Unit =
		throw UnsupportedOperationException()

	override fun getScoreboard(): ServerScoreboard = throw UnsupportedOperationException()
	override fun getRecipeManager(): RecipeManager = throw UnsupportedOperationException()
	override fun potionBrewing(): PotionBrewing = throw UnsupportedOperationException()
	override fun setDayTimeFraction(dayTimeFraction: Float): Unit = throw UnsupportedOperationException()
	override fun getDayTimeFraction(): Float = throw UnsupportedOperationException()
	override fun getDayTimePerTick(): Float = throw UnsupportedOperationException()
	override fun setDayTimePerTick(dayTimePerTick: Float): Unit = throw UnsupportedOperationException()

	override fun get(id: Int): Entity = throw UnsupportedOperationException()
	override fun get(uuid: UUID): Entity = throw UnsupportedOperationException()
	override fun getAll(): Iterable<Entity> = throw UnsupportedOperationException()
	override fun <U : Entity> get(
		test: EntityTypeTest<Entity, U>,
		consumer: AbortableIterationConsumer<U>
	): Unit = throw UnsupportedOperationException()

	override fun get(
		boundingBox: AABB,
		consumer: Consumer<Entity>
	): Unit = throw UnsupportedOperationException()

	override fun <U : Entity> get(
		test: EntityTypeTest<Entity, U>,
		bounds: AABB,
		consumer: AbortableIterationConsumer<U>
	): Unit = throw UnsupportedOperationException()
}