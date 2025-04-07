package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.localClient

class MachSoundInstance(
	soundEvent: SoundEvent,
	private val range: IntRange,
	private val player: Player?,
	var timer: Int = 0
) : AbstractTickableSoundInstance(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom()) {
	private var stopped = false
	var shouldLoop: Boolean = false
	var kill: Boolean = false

	init {
		this.delay = 0
	}

	override fun tick() {
		if (this.kill) {
			this.stop()
			this.volume = 0f
			return
		}
		val currentPlayer = this.player ?: localClient.player ?: return
		LogManager.getLogger().warn("ticking sound instance")
		if (!currentPlayer.isRemoved && this.timer in this.range && !this.stopped) {
			this.stopped = false
			this.looping = true
			this.x = currentPlayer.x
			this.y = currentPlayer.y
			this.z = currentPlayer.z
		} else this.stop()
	}

	override fun isStopped(): Boolean = this.stopped
	override fun isLooping(): Boolean = this.shouldLoop
}