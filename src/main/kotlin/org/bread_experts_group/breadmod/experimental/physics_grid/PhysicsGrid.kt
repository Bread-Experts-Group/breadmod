package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.datafixers.DataFixerUpper
import com.mojang.serialization.MapCodec
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.IntSortedSets
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.server.level.progress.ChunkProgressListener
import net.minecraft.world.Difficulty
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.GameType.SURVIVAL
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.border.WorldBorder.Settings
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.FlatLevelSource
import net.minecraft.world.level.levelgen.GenerationStep.Carving
import net.minecraft.world.level.levelgen.Heightmap.Types
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraft.world.level.timers.TimerCallbacks
import net.minecraft.world.level.timers.TimerQueue
import net.minecraft.world.level.validation.DirectoryValidator
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.fml.loading.FMLPaths
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.unaryMinus
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.math.pow

abstract class PhysicsGrid protected constructor(level: Level, posA: BlockPos, posB: BlockPos) : ServerLevel(
	localClient.singleplayerServer!!,
	Executors.newVirtualThreadPerTaskExecutor(),
	Companion.STORAGE_ACCESS,
	Companion.createLevelData(level),
	Level.OVERWORLD,
	LevelStem(
		level.registryAccess().holderOrThrow(BuiltinDimensionTypes.OVERWORLD),
		Companion.createChunkGenerator(level)
	),
	object : ChunkProgressListener {
		override fun updateSpawnPos(center: ChunkPos) {}
		override fun onStatusChange(chunkPos: ChunkPos, chunkStatus: ChunkStatus?) {}
		override fun start() {}
		override fun stop() {}
	},
	false,
	0L,
	emptyList(),
	true,
	null
) {
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
//		this.tick { true }
	}
}