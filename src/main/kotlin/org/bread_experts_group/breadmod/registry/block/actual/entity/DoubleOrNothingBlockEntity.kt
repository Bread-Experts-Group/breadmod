package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.registries.DeferredHolder
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.network.clientbound.DoubleOrNothingPacket
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.sound.ModSounds

// todo an idea.. what if i used an AbstractTickableSoundInstance to preserve the stereo quality of the sounds,
//  while also attenuating the audio when you get further from the machine?
class DoubleOrNothingBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BlockEntity(ModBlockEntityTypes.DOUBLE_OR_NOTHING.get(), pos, state) {
	val random: RandomSource = RandomSource.create()
	var counter: Int = 0
	private val sounds: List<DeferredHolder<SoundEvent, SoundEvent>> = listOf(
		ModSounds.DOUBLE_1X,
		ModSounds.DOUBLE_2X,
		ModSounds.DOUBLE_3X,
		ModSounds.DOUBLE_4X,
		ModSounds.DOUBLE_5X,
		ModSounds.DOUBLE_6X,
		ModSounds.DOUBLE_7X,
		ModSounds.DOUBLE_8X,
		ModSounds.DOUBLE_9X,
		ModSounds.DOUBLE_JACKPOT
	)
	var currentPlayer: Player? = null
	var timeStarted: Long = 0
	var nothing: Boolean = false
	var jackpotTimer: Int = 0
	var hasJackpot: Boolean = false
	var cashout: Boolean = false
	var rewired: Boolean = false
	var blockhead: Boolean = false
	var zoom: Float = 0f
		set(value) {
			field = Math.clamp(value, 0f, 2f)
		}
	var tilt: Float = 0f
		set(value) {
			field = Math.clamp(value, -20f, 20f)
		}

	fun double(level: Level, player: Player, pos: BlockPos) {
		if (this.hasJackpot) return
		if (this.nothing) this.nothing = false
		if (this.currentPlayer == null) this.currentPlayer = player else if (this.currentPlayer != player) return
		this.timeStarted = level.gameTime
		if (level is ServerLevel) {
			val chance = this.random.nextInt(0, 10)
			if ((chance >= (if (this.rewired) 0 else 4) || this.counter == 0)) {
				this.applyTiltAndZoom()
				this.playSound(level, pos.above(), this.sounds[this.counter])
				if (this.counter < 10) this.counter += 1
				if (this.counter == 10) this.hasJackpot = true
				this.updateClients(level, false)
			} else {
				this.playSound(level, pos.above(), ModSounds.DOUBLE_NOTHING)
				this.nothing = true
				this.counter = 0
				this.updateClients(level, true)
			}
		}
	}

	private fun applyTiltAndZoom() {
		this.zoom = when (this.counter) {
			0, 1, 2, 3 -> 0.3f
			4          -> 0.4f
			5          -> 0.42f
			6          -> 0.47f
			7          -> 0.50f
			8          -> 0.52f
			9          -> 0.55f
			else       -> 0f
		}
		if (this.counter > 1) {
			val tiltIntensity = when (this.counter) {
				4    -> 10f
				5    -> 13f
				6    -> 15f
				7    -> 17f
				8    -> 19f
				9    -> 22f
				else -> 0f
			}
			this.tilt = if (this.random.nextInt(0, 2) == 1) tiltIntensity else -tiltIntensity
		}
	}

	private fun updateClients(level: ServerLevel, nothing: Boolean) =
		PacketDistributor.sendToPlayersTrackingChunk(
			level,
			ChunkPos(this.blockPos),
			DoubleOrNothingPacket(
				if (nothing) listOf(0.4f, 0f) else listOf(this.zoom, this.tilt),
				if (this.hasJackpot) listOf(false, true, false) else listOf(
					this.nothing,
					this.hasJackpot,
					this.cashout
				),
				this.counter,
				this.blockPos
			)
		)

	fun cashout(level: Level, player: Player, pos: BlockPos) {
		if (this.hasJackpot) return
		if (this.counter == 0) return
		LogManager.getLogger().info("cashout")
		this.timeStarted = level.gameTime
		if (level is ServerLevel) {
			this.playSound(level, pos.above(), ModSounds.DOUBLE_CASHOUT)
			this.cashout = true
			this.updateClients(level, false)
		}
	}

	fun playSound(level: Level, pos: BlockPos, sound: DeferredHolder<SoundEvent, SoundEvent>) {
		level.playSound(null, pos, sound.get(), BLOCKS, 1f, 1f)
	}

	fun tick(level: Level, pos: BlockPos, state: BlockState) {
		if (this.timeStarted + 600 < level.gameTime
			&& !this.hasJackpot && !this.nothing && !this.cashout && this.timeStarted != 0L
		) this.reset()

		if (this.cashout && this.timeStarted + 60 == level.gameTime) {
			this.reset()
			this.zoom = 0.3f
		}

		if (this.hasJackpot) {
			this.jackpotTimer++
			if (this.jackpotTimer > 1200) {
				this.hasJackpot = false
				this.jackpotTimer = 0
				this.reset()
			}
		}

		if (this.nothing) {
			if (this.timeStarted + 20 == level.gameTime) {
				this.reset()
				this.nothing = false
				this.zoom = 0.3f
			}
		}
	}

	private fun reset() {
		this.timeStarted = 0
		this.currentPlayer = null
		this.zoom = 0f
		this.tilt = 0f
		this.counter = 0
		this.cashout = false
	}
}