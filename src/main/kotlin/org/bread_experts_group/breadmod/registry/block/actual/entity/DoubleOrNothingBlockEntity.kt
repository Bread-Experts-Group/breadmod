package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.sound.ModSounds

// todo an idea.. what if i used an AbstractTickableSoundInstance to preserve the stereo quality of the sounds,
//  while also attenuating the audio when you get further from the machine?
class DoubleOrNothingBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BlockEntity(ModBlockEntityTypes.DOUBLE_OR_NOTHING.get(), pos, state) {
	private val random = RandomSource.create(42)
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
		ModSounds.DOUBLE_9X
	)
	var currentPlayer: Player? = null
	var timeStarted: Long = 0
	var nothing: Boolean = false

	fun double(level: Level, player: Player, pos: BlockPos) {
		if (this.nothing) this.nothing = false
		this.currentPlayer = player
		this.timeStarted = level.gameTime
		if ((this.random.nextInt(0, 2) == 1 || this.counter == 0)) {
			this.playSound(level, pos.above(), this.sounds[this.counter])
			if (this.counter < 8) this.counter += 1
		} else {
			this.playSound(level, pos.above(), ModSounds.DOUBLE_NOTHING)
			this.nothing = true
			this.counter = 0
		}
	}

	override fun saveAdditional(tag: CompoundTag, registries: Provider) {
		tag.putInt("counter", this.counter)
	}

	override fun loadAdditional(tag: CompoundTag, registries: Provider) {
		this.counter = tag.getInt("counter")
	}

	override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)

	override fun getUpdateTag(registries: Provider): CompoundTag =
		CompoundTag().also { this.saveAdditional(it, registries) }

	fun cashout(level: Level, player: Player, pos: BlockPos) {
		this.playSound(level, pos.above(), ModSounds.DOUBLE_CASHOUT)
	}

	fun playSound(level: Level, pos: BlockPos, sound: DeferredHolder<SoundEvent, SoundEvent>) {
		level.playSound(null, pos, sound.get(), BLOCKS, 1f, 1f)
	}

	fun tick(level: Level, pos: BlockPos, state: BlockState) {
		if (this.timeStarted + 100 == level.gameTime) {
			this.currentPlayer = null
			this.timeStarted = 0
			this.counter = 0
			this.nothing = false
			LogManager.getLogger().info("resetting player and time")
		}
	}
}