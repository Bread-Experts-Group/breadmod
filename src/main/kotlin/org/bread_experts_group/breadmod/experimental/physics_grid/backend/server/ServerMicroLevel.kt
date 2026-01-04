package org.bread_experts_group.breadmod.experimental.physics_grid.backend.server

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.TickRateManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.TickingBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.levelgen.XoroshiroRandomSource
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.redstone.NeighborUpdater
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.phys.Vec3
import net.minecraft.world.ticks.LevelTicks
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.entity.PartEntity
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.physics_grid.BlockNamesHuffmanSavedData
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelBlockEvent
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelEntityGetter
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelGameEventDispatcher
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.function.BooleanSupplier
import java.util.function.Supplier

/**
 * The super constructor in this class is replaced at runtime with a no-args constructor via the breadmod agent.
 */
class ServerMicroLevel(
	private val grid: PhysicsGrid,
	private val sourceLevel: Level
) : ServerLevel(
	null, null, null, null,
	null, null, null, false,
	0, null, true, null
) {
	companion object {
		fun getLevelBaseForData(server: MinecraftServer): ServerLevel? = server.getLevel(OVERWORLD)
		fun getNameSpaceAndNameHuffmanSD(server: MinecraftServer): BlockNamesHuffmanSavedData? {
			val dataBase = this.getLevelBaseForData(server) ?: return null
			return dataBase.dataStorage.get(
				BlockNamesHuffmanSavedData.FACTORY,
				"__beg_microlevel_blocks_huffman"
			)
		}

		fun computeNameSpaceAndNameHuffmanSD(server: MinecraftServer) {
			val dataBase = this.getLevelBaseForData(server) ?: return
			dataBase.dataStorage.set(
				"__beg_microlevel_blocks_huffman",
				BlockNamesHuffmanSavedData.create(BuiltInRegistries.BLOCK)
			)
		}
	}

	init {
		this.players = this.grid.playersInGrid
	}

	private val logger: Logger = LogManager.getLogger("ServerMicroLevel")
	private val randomSeeder: SecureRandom = SecureRandom()
	private val seederBuffer: ByteBuffer = ByteBuffer.allocate(16)
	override fun getRandom(): RandomSource {
		this.seederBuffer.clear()
		val seedBytes = this.randomSeeder.generateSeed(16)
		this.seederBuffer.put(seedBytes)
		this.seederBuffer.flip()
		return XoroshiroRandomSource(this.seederBuffer.getLong(), this.seederBuffer.getLong())
	}

	override fun players(): List<ServerPlayer> = this.grid.playersInGrid

	override fun sendBlockUpdated(pos: BlockPos, oldState: BlockState, newState: BlockState, flags: Int) {
//		this.chunkSource.blockChanged(pos)
	}

	override fun neighborChanged(pos: BlockPos, block: Block, fromPos: BlockPos) {
		val toState = this.getBlockState(pos)
		NeighborUpdater.executeUpdate(this, toState, pos, block, fromPos, false)
	}

	override fun neighborChanged(state: BlockState, pos: BlockPos, block: Block, fromPos: BlockPos, isMoving: Boolean) {
		TODO("NOTIFY $pos, $block, $fromPos, $isMoving")
	}

	override fun updateNeighborsAt(pos: BlockPos, block: Block) {
		/*
		 net.neoforged.neoforge.event.EventHooks.onNeighborNotify(this, pos, this.getBlockState(pos), java.util.EnumSet.allOf(Direction.class), false).isCanceled();
		 */
		NeighborUpdater.UPDATE_ORDER.forEach { direction ->
			val toPos = pos.relative(direction)
			val toState = this.getBlockState(toPos)
			NeighborUpdater.executeUpdate(this, toState, toPos, block, pos, false)
		}
	}

	override fun updateNeighborsAtExceptFromFacing(pos: BlockPos, blockType: Block, skipSide: Direction) {
		/*
		java.util.EnumSet<Direction> directions = java.util.EnumSet.allOf(Direction.class);
        directions.remove(skipSide);
        if (net.neoforged.neoforge.event.EventHooks.onNeighborNotify(this, pos, this.getBlockState(pos), directions, false).isCanceled())
            return;
		 */
		super.updateNeighborsAtExceptFromFacing(pos, blockType, skipSide)
		NeighborUpdater.UPDATE_ORDER.forEach { direction ->
			if (direction == skipSide) return@forEach
			val toPos = pos.relative(direction)
			val toState = this.getBlockState(toPos)
			NeighborUpdater.executeUpdate(this, toState, toPos, blockType, pos, false)
		}
	}

	override fun destroyBlock(pos: BlockPos, dropBlock: Boolean, entity: Entity?, recursionLeft: Int): Boolean {
		println("Want to destroy $pos, $dropBlock, $entity, $recursionLeft")
		return false
//		return super.destroyBlock(pos, dropBlock, entity, recursionLeft)
	}

	override fun getMinBuildHeight(): Int = -64
	override fun getMaxBuildHeight(): Int = 365
	override fun hasChunk(chunkX: Int, chunkZ: Int): Boolean = true
	override fun mayInteract(player: Player, pos: BlockPos): Boolean = true

	override fun getBlockState(pos: BlockPos): BlockState = this.getChunk(pos).getBlockState(pos)
	override fun getFluidState(pos: BlockPos): FluidState = this.getChunk(pos).getFluidState(pos)
	override fun getBlockEntity(pos: BlockPos): BlockEntity? = this.getChunk(pos).getBlockEntity(pos)

	private val entityGetter: MicroLevelEntityGetter = MicroLevelEntityGetter()
	override fun getEntities(): LevelEntityGetter<Entity> = this.entityGetter
	override fun getPartEntities(): Collection<PartEntity<*>> = emptyList() // TODO: Part entities

	private val chunkSource: ServerMicroLevelChunkSource = ServerMicroLevelChunkSource(this)
	private val worldBorder: WorldBorder = WorldBorder()
	override fun getChunkSource(): ServerChunkCache = this.chunkSource
	override fun getWorldBorder(): WorldBorder = this.worldBorder

	// TODO: Lighting
	private val levelLightEngine: LevelLightEngine = object : LevelLightEngine(this.chunkSource, false, false) {
		override fun getRawBrightness(blockPos: BlockPos, amount: Int): Int = 16
	}

	override fun getLightEngine(): LevelLightEngine = this.levelLightEngine

	// Ticking
	override fun shouldTickBlocksAt(chunkPos: Long): Boolean {
		// TODO: shouldTickBlocksAt
		return true
	}

	@Suppress("PROPERTY_HIDES_JAVA_FIELD")
	private val blockEntityTickers: MutableList<TickingBlockEntity> = mutableListOf()
	override fun addBlockEntityTicker(ticker: TickingBlockEntity) {
		this.blockEntityTickers.add(ticker)
	}

	private val events: ArrayDeque<MicroLevelBlockEvent> = ArrayDeque()
	private val blockTicks: LevelTicks<Block> = ServerMicroLevelTicks()
	private val fluidTicks: LevelTicks<Fluid> = ServerMicroLevelTicks()
	override fun getBlockTicks(): LevelTicks<Block> = this.blockTicks
	override fun getFluidTicks(): LevelTicks<Fluid> = this.fluidTicks
	override fun tick(hasTimeLeft: BooleanSupplier) {
		println("Ticking ${this.sourceLevel}")
		this.chunkSource.tick(hasTimeLeft, true)
		if (this.tickRateManager.runsNormally()) {
			this.blockTicks.tick(this.gameTime, 65536, this::tickBlock)
			this.fluidTicks.tick(this.gameTime, 65536, this::tickFluid)
			while (this.events.isNotEmpty()) {
				val (pos, block, eventID, eventParam) = this.events.removeLast()
				val state = this.getBlockState(pos)
				if (state.`is`(block) && state.triggerEvent(this, pos, eventID, eventParam)) {
					this.logger.fatal("B This message must be sent to the client micro level! : $pos, $block, $eventID, $eventParam [${this.grid}]")
//				val position = pos.toVec3() + this.grid.pos
//				this.sourceLevel.server?.playerList?.broadcast(
//					null,
//					position.x,
//					position.y,
//					position.z,
//					64.0,
//					this.sourceLevel.dimension(),
//					ClientboundBlockEventPacket(
//						BlockPos(position.toVec3i()),
//						block,
//						eventID,
//						eventParam
//					)
//				) TODO: This packet must contain the local grid, as it is sent from the server. For now, playing locally..
				}
			}
		}
		this.blockEntityTickers.removeIf {
			if (it.isRemoved) true
			else {
				if (this.shouldTickBlocksAt(it.pos)) it.tick()
				false
			}
		}
	}

	override fun tickChunk(chunk: LevelChunk, randomTickSpeed: Int) {
		if (randomTickSpeed > 0) {
			var skipping = 0
			(chunk as ServerMicroLevelChunkAccess).blocks.forEach { (pos, state) ->
				if (skipping-- > 0) return@forEach
				else if (skipping <= 0) skipping = this.random.nextInt(0, (16 * 16 * 16) / randomTickSpeed)
				if (state.isRandomlyTicking) state.randomTick(this, pos.toBlockPos(), this.random)
			}
		}
	}

	private val tickRateManager: TickRateManager = object : TickRateManager() {
		override fun runsNormally(): Boolean = true
	}

	override fun tickRateManager(): TickRateManager = this.tickRateManager

	override fun playSeededSound(
		player: Player?,
		x: Double,
		y: Double,
		z: Double,
		sound: Holder<SoundEvent?>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	): Unit = this.sourceLevel.playSeededSound(
		player,
		x + this.grid.pos.x,
		y + this.grid.pos.y,
		z + this.grid.pos.z,
		sound, category, volume, pitch, seed
	)

	override fun blockEvent(pos: BlockPos, block: Block, eventID: Int, eventParam: Int) {
		this.events.add(MicroLevelBlockEvent(pos, block, eventID, eventParam))
	}

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int) {
		this.logger.fatal("L This message must be sent to the client micro level! : $player, $type, $pos, $data [${this.grid}]")
	}

	private val gameEventDispatcher: MicroLevelGameEventDispatcher = MicroLevelGameEventDispatcher(this)
	override fun gameEvent(gameEvent: Holder<GameEvent>, pos: Vec3, context: GameEvent.Context) {
		if (CommonHooks.onVanillaGameEvent(this, gameEvent, pos, context))
			this.gameEventDispatcher.post(gameEvent, pos, context)
	}

	override fun toString(): String = "ServerMicroLevel"
	override fun registryAccess(): RegistryAccess = this.sourceLevel.registryAccess()
	override fun getGameTime(): Long = this.sourceLevel.gameTime
	override fun getLevelData(): LevelData = this.sourceLevel.levelData
	override fun getRecipeManager(): RecipeManager = this.sourceLevel.recipeManager
	override fun dimensionType(): DimensionType = this.sourceLevel.dimensionType()
	override fun getGameRules(): GameRules = this.sourceLevel.gameRules
	override fun dimensionTypeRegistration(): Holder<DimensionType> = this.sourceLevel.dimensionTypeRegistration()
	override fun getProfilerSupplier(): Supplier<ProfilerFiller> = this.sourceLevel.profilerSupplier
	override fun getProfiler(): ProfilerFiller = this.sourceLevel.profiler
	override fun enabledFeatures(): FeatureFlagSet = this.sourceLevel.enabledFeatures()
	override fun dimension(): ResourceKey<Level> = this.sourceLevel.dimension()
}