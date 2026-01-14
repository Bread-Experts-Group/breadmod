package org.bread_experts_group.breadmod.experimental.physics_grid.backend.server

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
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
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.TickingBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.levelgen.XoroshiroRandomSource
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.ticks.LevelTicks
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.entity.PartEntity
import net.neoforged.neoforge.event.EventHooks
import net.neoforged.neoforge.network.PacketDistributor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.physics_grid.BlockNamesHuffmanSavedData
import org.bread_experts_group.breadmod.experimental.physics_grid.GridPacket
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelBlockEvent
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelEntityGetter
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelGameEventDispatcher
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelTicks
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateBlockDestructionPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateBlockEventPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateLevelEventPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateSoundEntityPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateSoundPhysicsGridPacket
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.toBlockPos
import org.bread_experts_group.breadmod.util.toVec3
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.function.BooleanSupplier
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * The super constructor in this class is replaced at runtime with a no-args constructor via the breadmod agent.
 */
@Suppress("KDocMissingDocumentation")
class ServerMicroLevel(
	val grid: PhysicsGrid,
	val sourceLevel: ServerLevel
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
		this.chunkSource.blockChanged(pos)
		// TODO: Entities?
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
//		if (flags and 4 != 0) TODO("Prevent rerender")
//		if (flags and 8 != 0) TODO("Force main thread rerender")
//		if (flags and 32 != 0) TODO("Prevent neighbor drops")
		val oldState = this.getChunk(0, 0).setBlockState(pos, state, flags and 64 != 0) ?: return false
		val setState = this.getBlockState(pos)
		if (setState == state) {
			// TODO: setBlocksDirty
			if (flags and 2 != 0 && flags and 4 == 0) this.sendBlockUpdated(pos, oldState, state, flags)
			if (flags and 1 == 1) {
				this.blockUpdated(pos, oldState.block)
				if (state.hasAnalogOutputSignal()) this.updateNeighbourForOutputSignal(pos, state.block)
			}
			if (flags and 16 != 0) {
				val modified = flags and 0b11111111111111111111111111011110.toInt()
				oldState.updateIndirectNeighbourShapes(this, pos, modified, recursionLeft - 1)
				state.updateNeighbourShapes(this, pos, modified, recursionLeft - 1)
				state.updateIndirectNeighbourShapes(this, pos, modified, recursionLeft - 1)
			}
			this.onBlockStateChange(pos, oldState, setState)
			state.onBlockStateChange(this, pos, oldState)
		}
		GridPacket.recompileGridMeshOnClient(this.grid.id)
		return true
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
	override fun addFreshEntity(entity: Entity): Boolean {
		entity.addDeltaMovement(this.grid.delta)
		entity.setPos(entity.position().add(this.grid.pos))
		return this.sourceLevel.addFreshEntity(entity)
	}

	private val chunkSource: ServerMicroLevelChunkCache = ServerMicroLevelChunkCache(this)
	private val worldBorder: WorldBorder = WorldBorder()
	override fun getChunkSource(): ServerChunkCache = this.chunkSource
	override fun getWorldBorder(): WorldBorder = this.worldBorder

	// TODO: Lighting inheriting local light and sky light from the source level
	private val levelLightEngine: LevelLightEngine = object : LevelLightEngine(this.chunkSource, true, false) {
		override fun getRawBrightness(blockPos: BlockPos, amount: Int): Int {
			val source = this@ServerMicroLevel.sourceLevel
			val grid = this@ServerMicroLevel.grid
			val adjustedPos = grid.pos.toBlockPos().offset(blockPos)
			return source.getRawBrightness(adjustedPos, amount)
		}
	}

	override fun getLightEngine(): LevelLightEngine = this.levelLightEngine

	// Ticking
	// TODO: shouldTickBlocksAt
	override fun shouldTickBlocksAt(chunkPos: Long): Boolean = true

	override fun addBlockEntityTicker(ticker: TickingBlockEntity) {
		this.blockEntityTickers.add(ticker)
	}

	override fun destroyBlockProgress(breakerId: Int, pos: BlockPos, progress: Int) {
		this.sourceLevel.server.playerList.players.forEach { player ->
			if (player.level() === this.sourceLevel && player.id == breakerId) {
				val d0: Double = (pos.x + this.grid.pos.x) - player.x
				val d1: Double = (pos.y + this.grid.pos.y) - player.y
				val d2: Double = (pos.z + this.grid.pos.z) - player.z
				if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0) {
					PacketDistributor.sendToPlayer(
						player,
						EncapsulateBlockDestructionPhysicsGridPacket(
							this.grid.id,
							ClientboundBlockDestructionPacket(breakerId, pos, progress)
						)
					)
				}
			}
		}
	}

	override fun getRandomSequence(
		location: ResourceLocation
	): RandomSource = this.sourceLevel.getRandomSequence(location)

	private val events: ArrayDeque<MicroLevelBlockEvent> = ArrayDeque()
	private val blockTicks: LevelTicks<Block> = MicroLevelTicks()
	private val fluidTicks: LevelTicks<Fluid> = MicroLevelTicks()
	override fun getBlockTicks(): LevelTicks<Block> = this.blockTicks
	override fun getFluidTicks(): LevelTicks<Fluid> = this.fluidTicks
	override fun tick(hasTimeLeft: BooleanSupplier) {
		this.lightEngine.runLightUpdates()
		this.chunkSource.tick(hasTimeLeft, true)
		if (this.tickRateManager.runsNormally()) {
			this.blockTicks.tick(this.gameTime, 65536, this::tickBlock)
			this.fluidTicks.tick(this.gameTime, 65536, this::tickFluid)
			while (this.events.isNotEmpty()) {
				val (pos, block, eventID, eventParam) = this.events.removeLast()
				val state = this.getBlockState(pos)
				if (state.`is`(block) && state.triggerEvent(this, pos, eventID, eventParam)) {
					val position = pos.toVec3() + this.grid.pos
					this.server.playerList.broadcast(
						null,
						position.x,
						position.y,
						position.z,
						64.0,
						this.sourceLevel.dimension(),
						ClientboundCustomPayloadPacket(
							EncapsulateBlockEventPhysicsGridPacket(
								this.grid.id,
								ClientboundBlockEventPacket(
									pos,
									block,
									eventID,
									eventParam
								)
							)
						)
					)
				}
			}
		}
//		// todo client packet testing... MixinClientPacketListener
//		(this.getChunk(0, 0) as ServerMicroLevelChunk).let { chunkSource ->
////			logDebugInfo(chunkSource)
//			if (chunkSource.isUnsaved) {
//				chunkSource.blockEntities.forEach { (_, entity) ->
//					entity.updatePacket?.let { packet ->
//						this.players().forEach { it.connection.send(packet) }
//					}
//				}
//				chunkSource.isUnsaved = false
//			}
//		}
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
			(chunk as ServerMicroLevelChunk).blocks.forEach { (pos, state) ->
				if (skipping-- > 0) return@forEach
				else if (skipping <= 0) skipping = this.random.nextInt(0, (16 * 16 * 16) / randomTickSpeed)
				if (state.isRandomlyTicking) state.randomTick(this, pos.toBlockPos(), this.random)
			}
		}
	}

	private val tickRateManager: TickRateManager = /*object : TickRateManager() {
		override fun runsNormally(): Boolean = true
	}*/ this.sourceLevel.tickRateManager()

	override fun tickRateManager(): TickRateManager = this.tickRateManager
	override fun getBiomeManager(): BiomeManager = this.sourceLevel.biomeManager
	override fun blockEvent(pos: BlockPos, block: Block, eventID: Int, eventParam: Int) {
		this.events.add(MicroLevelBlockEvent(pos, block, eventID, eventParam))
	}

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int) {
		this.server.playerList.broadcast(
			player,
			pos.x.toDouble(),
			pos.y.toDouble(),
			pos.z.toDouble(),
			64.0,
			this.sourceLevel.dimension(),
			ClientboundCustomPayloadPacket(
				EncapsulateLevelEventPhysicsGridPacket(
					this.grid.id,
					ClientboundLevelEventPacket(
						type,
						pos,
						data,
						false
					)
				)
			)
		)
	}

	override fun <T : Entity?> getEntities(
		entityTypeTest: EntityTypeTest<Entity, T>,
		bounds: AABB,
		predicate: Predicate<in T>
	): List<T> = this.sourceLevel.getEntities(entityTypeTest, bounds.move(this.grid.pos), predicate)

	private val gameEventDispatcher: MicroLevelGameEventDispatcher = MicroLevelGameEventDispatcher(this)
	override fun gameEvent(gameEvent: Holder<GameEvent>, pos: Vec3, context: GameEvent.Context) {
		super.gameEvent(gameEvent, pos, context)
		if (CommonHooks.onVanillaGameEvent(this, gameEvent, pos, context))
			this.gameEventDispatcher.post(gameEvent, pos, context)
	}

	override fun playSeededSound(
		player: Player?,
		entity: Entity,
		sound: Holder<SoundEvent?>,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		seed: Long
	) {
		val event = EventHooks.onPlaySoundAtEntity(entity, sound, category, volume, pitch)
		val eventSound = event.sound
		if (event.isCanceled || eventSound == null) return
		val eventVolume = event.newVolume
		val eventCategory = event.source
		this.server.playerList.broadcast(
			player,
			entity.x,
			entity.y,
			entity.z,
			eventSound.value().getRange(eventVolume).toDouble(),
			this.dimension(),
			ClientboundCustomPayloadPacket(
				EncapsulateSoundEntityPhysicsGridPacket(
					this.grid.id,
					ClientboundSoundEntityPacket(
						eventSound,
						eventCategory,
						entity,
						eventVolume,
						event.newPitch,
						seed
					)
				)
			)
		)
	}

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
	) {
		val event = EventHooks.onPlaySoundAtPosition(this, x, y, z, sound, category, volume, pitch)
		val eventSound = event.sound
		if (event.isCanceled || eventSound == null) return
		val eventVolume = event.newVolume
		val eventCategory = event.source
		this.server.playerList.broadcast(
			player,
			x + this.grid.pos.x,
			y + this.grid.pos.y,
			z + this.grid.pos.z,
			eventSound.value().getRange(eventVolume).toDouble(),
			this.dimension(),
			ClientboundCustomPayloadPacket(
				EncapsulateSoundPhysicsGridPacket(
					this.grid.id,
					ClientboundSoundPacket(
						eventSound,
						eventCategory,
						x, y, z,
						eventVolume,
						event.newPitch,
						seed
					)
				)
			)
		)
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
	override fun getServer(): MinecraftServer = this.sourceLevel.server
}