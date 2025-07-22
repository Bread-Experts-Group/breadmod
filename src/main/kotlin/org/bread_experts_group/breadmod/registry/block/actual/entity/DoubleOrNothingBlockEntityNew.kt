package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.registries.DeferredHolder
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.network.clientbound.DoubleOrNothingPacketNew
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class DoubleOrNothingBlockEntityNew(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<DoubleOrNothingBlockEntityNew>(
	ModBlockEntityTypes.DOUBLE_OR_NOTHING.get(),
	pos, state
), LerpTicker {
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
	override val lerpParams: Array<LerpTicker.LerpParams> = arrayOf(
		LerpTicker.LerpParams(clampMin = 0f, clampMax = 0.55f), // zoom
		LerpTicker.LerpParams(clampMin = 0f, clampMax = 20f), // tiltPositive
		LerpTicker.LerpParams(clampMin = -20f, clampMax = 0f) // tiltNegative
	)
	val random: RandomSource = RandomSource.create()

	// counters
	var jackpotTimer: Int = 0
	var doubleCounter: Int = 0

	// states
	var nothing: Boolean = false
	var jackpot: Boolean = false
	var cashout: Boolean = false
	var data: DoubleOrNothingData? = null
	var useNegativeTilt: Boolean = false

	// funny stuff
	var rewired: Boolean = false
	var blockhead: Boolean = false

	fun triggerDouble(level: Level, player: Player) {
		if (this.jackpot) return
		if (this.nothing) this.nothing = false
		if (this.data == null) this.data = DoubleOrNothingData.create(player, level)
		if (level is ServerLevel) this.data?.let { data ->
			if (this.isDouble()) {
				this.playSound(this.sounds[this.doubleCounter])
				this.updateClients(false)
				this.handleDouble(data, level, player, false)
			} else {
				this.playSound(ModSounds.DOUBLE_NOTHING)
				this.handleDouble(data, level, player, true)
				this.updateClients(true)
			}
		}
	}

	fun handleDouble(data: DoubleOrNothingData, level: Level, player: Player, nothing: Boolean) {
		LogManager.getLogger().info(this.doubleCounter)
		if (nothing) {
			this.doubleCounter = 0
			this.applyZoom()
			this.nothing = true
		} else {
			this.applyTilt()
			this.applyZoom()
			if (this.doubleCounter < 10) this.doubleCounter++
			if (this.doubleCounter == 10) this.jackpot = true
		}
	}

	fun triggerCashout(level: Level, player: Player) {

	}

	fun applyZoom() {
		this.setParamPosition(
			0, when (this.doubleCounter) {
				0, 1, 2, 3 -> 0.3f
				4          -> 0.4f
				5          -> 0.42f
				6          -> 0.47f
				7          -> 0.50f
				8          -> 0.52f
				9          -> 0.55f
				else       -> 0f
			}
		)
	}

	fun applyTilt() {
		if (this.doubleCounter < 4) return
		val tiltIntensity = when (this.doubleCounter) {
			4    -> 10f
			5    -> 13f
			6    -> 15f
			7    -> 17f
			8    -> 19f
			9    -> 22f
			else -> 0f
		}
		this.setParamPosition(1, tiltIntensity)
		this.setParamPosition(2, -tiltIntensity)
		this.useNegativeTilt = this.random.nextInt(0, 2) == 1
	}

	fun isDouble(): Boolean {
		val chance = this.random.nextInt(0, 10)
		return chance >= (if (this.rewired) 0 else 4) || this.doubleCounter == 0
	}

	fun playSound(sound: DeferredHolder<SoundEvent, SoundEvent>) {
		val level = this.level ?: return
		level.playSound(null, this.blockPos.above(), sound.get(), SoundSource.BLOCKS, 1f, 1f)
	}

	fun updateClients(nothing: Boolean) {
		val level = this.level as? ServerLevel ?: return
		PacketDistributor.sendToPlayersTrackingChunk(
			level,
			this.getChunkPos(),
			DoubleOrNothingPacketNew(this.blockPos, nothing)
		)
	}

	private fun tickZoom(params: (LerpTicker.LerpParams) -> Unit): Unit = this.tickCustom(0, params)
	private fun tickTilts(params: (LerpTicker.LerpParams) -> Unit) {
		this.tickCustom(1, params)
		this.tickCustom(2, params)
	}

	override fun commonTick(level: Level) {
		this.rewired = true
		val multiplier = when (this.doubleCounter) {
			7    -> 0.7f
			8    -> 0.5f
			9    -> 0.2f
			else -> 1f
		}

		this.tickZoom { it.setClampedPos(-0.1f * multiplier) }
		this.tickTilts {
			if (it.clampMax == 0f) it.setClampedPos(2f * multiplier + 0.05f) else it.setClampedPos(-2f * multiplier + 0.05f)
		}

		this.data?.let { data ->
			if (data.timeStarted + 600 < level.gameTime
				&& !this.jackpot
				&& !this.nothing
				&& !this.cashout
				&& data.timeStarted != 0L
			) this.reset()

			if (this.cashout && data.timeStarted + 60 == level.gameTime) {
				this.reset()
			}

			if (this.jackpot) {
				this.jackpotTimer++
				if (this.jackpotTimer > 1200) {
					this.jackpot = false
					this.jackpotTimer = 0
					this.reset()
				}
			}

			if (this.nothing) {
				if (data.timeStarted + 30 == level.gameTime) {
					this.reset()
					this.nothing = false
				}
			}
		}
	}

	fun reset() {
		this.data = null
		this.doubleCounter = 0
		this.cashout = false
		this.setParamPosition(0, 0.3f)
		this.setParamPosition(1, 0f)
	}

	data class DoubleOrNothingData(val player: Player, val wager: ItemStack, val timeStarted: Long) {
		companion object {
			fun create(player: Player, level: Level): DoubleOrNothingData =
				DoubleOrNothingData(player, ItemStack.EMPTY, level.gameTime)
		}
	}
}