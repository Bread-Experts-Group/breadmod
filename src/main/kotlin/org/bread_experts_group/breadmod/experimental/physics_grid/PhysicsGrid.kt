package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.AbortableIterationConsumer
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape.INVISIBLE
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkSource
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEvent.Context
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.saveddata.maps.MapId
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import net.minecraft.world.level.storage.WritableLevelData
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.ticks.LevelTickAccess
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.util.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import java.util.UUID
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import kotlin.math.pow

@Suppress("UnstableApiUsage")
abstract class PhysicsGrid(level: Level, posA: BlockPos, posB: BlockPos) : Level(
	level.levelData as WritableLevelData,
	level.dimension(),
	level.registryAccess(),
	level.registryAccess().holderOrThrow(BuiltinDimensionTypes.OVERWORLD),
	level.profilerSupplier,
	level.isClientSide,
	false,
	0L,
	1000000
), LevelEntityGetter<Entity> {
	val localChunkSource: ChunkSource = object : ChunkSource() {
		inner class LocalChunk(x: Int, z: Int) : LevelChunk(this@PhysicsGrid, ChunkPos(x, z))

		val chunks: MutableMap<Long, LocalChunk> = mutableMapOf()
		override fun getLevel(): BlockGetter = this@PhysicsGrid
		override fun getChunk(x: Int, z: Int, chunkStatus: ChunkStatus, requireChunk: Boolean): ChunkAccess =
			this.chunks.getOrPut((x.toLong() shl 32) or z.toLong()) { LocalChunk(x, z) }

		override fun tick(hasTimeLeft: BooleanSupplier, tickChunks: Boolean): Unit =
			throw UnsupportedOperationException()

		override fun gatherStats(): String = throw UnsupportedOperationException()
		override fun getLoadedChunksCount(): Int = throw UnsupportedOperationException()
		override fun getLightEngine(): LevelLightEngine = this@PhysicsGrid.localLightEngine
	}
	val localLightEngine: LevelLightEngine = LevelLightEngine(this.localChunkSource, true, true)
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
			LogManager.getLogger().info("without offset: ${this.getBlockState(immutablePos)}")
			LogManager.getLogger().info("with offset: ${this.getBlockState(offset)}")
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
	}

	override fun getEntities(): LevelEntityGetter<Entity> = this
	override fun players(): MutableList<out Player> = throw UnsupportedOperationException()
	override fun getShade(direction: Direction, shade: Boolean): Float {
		if (!shade) return 1f
		return when (direction) {
			Direction.DOWN                   -> 0.5f
			Direction.UP                     -> 1.0f
			Direction.NORTH, Direction.SOUTH -> 0.8f
			Direction.WEST, Direction.EAST   -> 0.6f
		}
	}

	override fun getLightEngine(): LevelLightEngine = this.localLightEngine
	override fun getUncachedNoiseBiome(x: Int, y: Int, z: Int): Holder<Biome> = throw UnsupportedOperationException()
	override fun enabledFeatures(): FeatureFlagSet = throw UnsupportedOperationException()
	override fun getBlockTicks(): LevelTickAccess<Block> = throw UnsupportedOperationException()
	override fun getFluidTicks(): LevelTickAccess<Fluid> = throw UnsupportedOperationException()
	override fun getChunkSource(): ChunkSource = this.localChunkSource

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
	override fun tickRateManager(): TickRateManager = TickRateManager()
	override fun getMapData(mapId: MapId): MapItemSavedData = throw UnsupportedOperationException()
	override fun setMapData(mapId: MapId, mapData: MapItemSavedData): Unit = throw UnsupportedOperationException()
	override fun getFreeMapId(): MapId = throw UnsupportedOperationException()
	override fun destroyBlockProgress(breakerId: Int, pos: BlockPos, progress: Int): Unit =
		throw UnsupportedOperationException()

	override fun getScoreboard(): Scoreboard = throw UnsupportedOperationException()
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