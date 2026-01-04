package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.dimension.DimensionType
import net.neoforged.neoforge.event.EventHooks
import org.bread_experts_group.breadmod.client.render.executeOnRenderThread
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import java.util.function.Supplier

class ClientMicroLevel(
	private val sourceLevel: ClientLevel,
	private val grid: PhysicsGrid
) : ClientLevel(
	null, null, null, null,
	0, 0, null, null, false,
	0
) {
	private val chunkSource: ClientMicroLevelChunkSource = ClientMicroLevelChunkSource(this)
	override fun getChunkSource(): ClientChunkCache = this.chunkSource

	override fun setServerVerifiedBlockState(pos: BlockPos, state: BlockState, flags: Int) {
		// TODO("$pos, $state, $flags VER") if (!this.blockStatePredictionHandler.updateKnownServerState(pos, state)) {
		this.setBlock(pos, state, flags, 512)
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
//		return super.setBlock(pos, state, flags, recursionLeft) TODO !
		val status = this.getChunk(0, 0).setBlockState(pos, state, flags and 64 != 0) != null
		if (status) executeOnRenderThread {
			PhysicsGrid.Companion.gridMeshes.forEach { (_, mesh) -> mesh.markForRecompile() }
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

	private val worldBorder: WorldBorder = WorldBorder()
	override fun getWorldBorder(): WorldBorder = this.worldBorder

	override fun toString(): String = "ClientMicroLevel"
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
}