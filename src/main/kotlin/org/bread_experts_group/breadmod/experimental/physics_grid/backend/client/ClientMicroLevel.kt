package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.TerrainParticle
import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
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
import net.minecraft.world.level.block.entity.TickingBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelDataManager
import net.neoforged.neoforge.entity.PartEntity
import net.neoforged.neoforge.event.EventHooks
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelEntityGetter
import org.bread_experts_group.breadmod.experimental.physics_grid.render.GridMesh
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.toBlockPos
import java.util.function.BooleanSupplier
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class ClientMicroLevel(
	private val sourceLevel: ClientLevel,
	val grid: PhysicsGrid
) : ClientLevel(
	null, null, null, null,
	0, 0, null, null, false,
	0
) {
	private val entityGetter: MicroLevelEntityGetter = MicroLevelEntityGetter()
	override fun getEntities(): LevelEntityGetter<Entity> = this.entityGetter
	override fun getPartEntities(): Collection<PartEntity<*>> = emptyList() // TODO: Part entities

	private val chunkSource: ClientMicroLevelChunkCache = ClientMicroLevelChunkCache(this)
	override fun getChunkSource(): ClientChunkCache = this.chunkSource

	override fun addBlockEntityTicker(ticker: TickingBlockEntity) {
		this.blockEntityTickers.add(ticker)
	}

	override fun tickRateManager(): TickRateManager = this.sourceLevel.tickRateManager()

	override fun tick(hasTimeLeft: BooleanSupplier) {
		this.lightEngine.runLightUpdates()
		this.chunkSource.tick(hasTimeLeft, true)
		this.blockEntityTickers.removeIf {
			if (it.isRemoved) true
			else {
				if (this.shouldTickBlocksAt(it.pos)) it.tick()
				false
			}
		}
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		val status = super.setBlock(pos, state, flags, recursionLeft)
		executeOnRenderThread {
			GridMesh.meshes.forEach { (_, mesh) -> mesh.recompile() }
		}
		return status
	}

	override fun addParticle(
		particleData: ParticleOptions,
		forceAlwaysRender: Boolean,
		x: Double,
		y: Double,
		z: Double,
		xSpeed: Double,
		ySpeed: Double,
		zSpeed: Double
	) {
		this.sourceLevel.addParticle(
			particleData, forceAlwaysRender,
			x + this.grid.pos.x,
			y + this.grid.pos.y,
			z + this.grid.pos.z,
			xSpeed, ySpeed, zSpeed
		)
	}

	override fun addParticle(
		particleData: ParticleOptions,
		x: Double,
		y: Double,
		z: Double,
		xSpeed: Double,
		ySpeed: Double,
		zSpeed: Double
	) {
		this.sourceLevel.addParticle(
			particleData,
			x + this.grid.pos.x,
			y + this.grid.pos.y,
			z + this.grid.pos.z,
			xSpeed, ySpeed, zSpeed
		)
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
		if (event.isCanceled || eventSound == null || player != localClient.player) return
		localClient.soundManager.play(
			EntityBoundSoundInstance(
				eventSound.value(),
				event.source,
				event.newVolume,
				event.newPitch,
				entity,
				seed
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
		if (event.isCanceled || eventSound == null || player != localClient.player) return
		localClient.soundManager.play(
			SimpleSoundInstance(
				sound.value(),
				event.source,
				event.newVolume,
				event.newPitch,
				RandomSource.create(seed),
				x + this.grid.pos.x,
				y + this.grid.pos.y,
				z + this.grid.pos.z
			)
		)
	}

	private fun playSoundGrid(
		x: Double, y: Double, z: Double,
		sound: SoundEvent,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		distanceDelay: Boolean
	) {
		val rX = x + this.grid.pos.x
		val rY = y + this.grid.pos.y
		val rZ = z + this.grid.pos.z
		val d0 = localClient.gameRenderer.mainCamera.getPosition().distanceToSqr(rX, rY, rZ)
		val ssi = SimpleSoundInstance(
			sound, category, volume, pitch, RandomSource.create(this.random.nextLong()),
			rX, rY, rZ
		)
		if (distanceDelay && d0 > 100.0) {
			val d1 = sqrt(d0) / 40.0
			localClient.soundManager.playDelayed(ssi, (d1 * 20.0).toInt())
		} else localClient.soundManager.play(ssi)
	}

	override fun playLocalSound(
		x: Double,
		y: Double,
		z: Double,
		sound: SoundEvent,
		category: SoundSource,
		volume: Float,
		pitch: Float,
		distanceDelay: Boolean
	): Unit = this.playSoundGrid(x, y, z, sound, category, volume, pitch, distanceDelay)

	private val manager: ModelDataManager = ModelDataManager(this)
	override fun getModelData(pos: BlockPos): ModelData = this.manager.getAt(pos)

	private fun destroyEffectsGrid(pos: BlockPos, state: BlockState) {
		if (!state.isAir && !IClientBlockExtensions.of(state)
				.addDestroyEffects(state, this, pos, localClient.particleEngine)
		) {
			val shape: VoxelShape = state.getShape(this, pos)
			shape.forAllBoxes { xPosAdjBound: Double, yPosAdjBound: Double, zPosAdjBound: Double,
				xPosAdjMax: Double, yPosAdjMax: Double, zPosAdjMax: Double ->
				val xPosAdj = min(1.0, xPosAdjMax - xPosAdjBound)
				val yPosAdj = min(1.0, yPosAdjMax - yPosAdjBound)
				val zPosAdj = min(1.0, zPosAdjMax - zPosAdjBound)
				val xPlanes = max(2, Mth.ceil(xPosAdj / 0.25))
				val yPlanes = max(2, Mth.ceil(yPosAdj / 0.25))
				val zPlanes = max(2, Mth.ceil(zPosAdj / 0.25))
				for (xPlane in 0 ..< xPlanes) {
					for (yPlane in 0 ..< yPlanes) {
						for (zPlane in 0 ..< zPlanes) {
							val xSpeed = (xPlane + 0.5) / xPlanes
							val ySpeed = (yPlane + 0.5) / yPlanes
							val zSpeed = (zPlane + 0.5) / zPlanes
							val xAdj = xSpeed * xPosAdj + xPosAdjBound
							val yAdj = ySpeed * yPosAdj + yPosAdjBound
							val zAdj = zSpeed * zPosAdj + zPosAdjBound
							localClient.particleEngine.add(
								TerrainParticle(
									this,
									pos.x + this.grid.pos.x + xAdj,
									pos.y + this.grid.pos.y + yAdj,
									pos.z + this.grid.pos.z + zAdj,
									xSpeed - 0.5,
									ySpeed - 0.5,
									zSpeed - 0.5,
									state,
									pos
								)/*.updateSprite(state, pos)*/
								// TODO: this might cause weirdness with grass and such, not sure how to proceed yet
							)
						}
					}
				}
			}
		}
	}

	override fun levelEvent(player: Player?, type: Int, pos: BlockPos, data: Int) {
		when (type) {
			2001 -> {
				val state = Block.stateById(data)
				if (!state.isAir && !IClientBlockExtensions.of(state)
						.playBreakSound(state, this, pos)
				) {
					val soundType = state.getSoundType(this, pos, null)
					this.playLocalSound(
						pos,
						soundType.breakSound,
						SoundSource.BLOCKS,
						(soundType.getVolume() + 1.0f) / 2.0f,
						soundType.getPitch() * 0.8f,
						false
					)
				}

				this.destroyEffectsGrid(pos, state)
			}
			else -> TODO("Level event type $type in LevelRenderer.java/levelEvent")
		}
	}

	override fun globalLevelEvent(id: Int, pos: BlockPos, data: Int) {
		when (id) {
			else -> TODO("Level event type $id in LevelRenderer.java/globalLevelEvent")
		}
	}

	override fun animateTick(posX: Int, posY: Int, posZ: Int) {
		val (x, y, z) = this.grid.pos
		super.animateTick(posX - x.toInt(), posY - y.toInt(), posZ - z.toInt())
	}

	override fun getBiomeManager(): BiomeManager = this.sourceLevel.biomeManager

	// TODO: Lighting inheriting local light and sky light from the source level
	private val levelLightEngine: LevelLightEngine = object : LevelLightEngine(this.chunkSource, true, false) {
		override fun getRawBrightness(blockPos: BlockPos, amount: Int): Int {
			val source = this@ClientMicroLevel.sourceLevel
			val grid = this@ClientMicroLevel.grid
			val adjustedPos = grid.pos.toBlockPos().offset(blockPos)
			return source.getRawBrightness(adjustedPos, amount)
		}
	}

	override fun getLightEngine(): LevelLightEngine = this.levelLightEngine

	override fun effects(): DimensionSpecialEffects = this.sourceLevel.effects()

	private val worldBorder: WorldBorder = WorldBorder()
	override fun getWorldBorder(): WorldBorder = this.worldBorder

	override fun toString(): String = "ClientMicroLevel"
	override fun getShade(direction: Direction, shade: Boolean): Float = this.sourceLevel.getShade(direction, shade)
	override fun getShade(normalX: Float, normalY: Float, normalZ: Float, shade: Boolean): Float = this.sourceLevel
		.getShade(normalX, normalY, normalZ, shade)

	override fun enabledFeatures(): FeatureFlagSet = this.sourceLevel.enabledFeatures()
	override fun registryAccess(): RegistryAccess = this.sourceLevel.registryAccess()
	override fun dimension(): ResourceKey<Level?> = this.sourceLevel.dimension()
	override fun dimensionType(): DimensionType = this.sourceLevel.dimensionType()
	override fun dimensionTypeRegistration(): Holder<DimensionType?> = this.sourceLevel.dimensionTypeRegistration()
	override fun getGameTime(): Long = this.sourceLevel.gameTime
	override fun getRecipeManager(): RecipeManager = this.sourceLevel.recipeManager
	override fun getGameRules(): GameRules = this.sourceLevel.gameRules
	override fun getProfilerSupplier(): Supplier<ProfilerFiller> = this.sourceLevel.profilerSupplier
	override fun getProfiler(): ProfilerFiller = this.sourceLevel.profiler
	override fun getLevelData(): ClientLevelData = this.sourceLevel.levelData
}