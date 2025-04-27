package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player

class MachSoundInstance(
	soundEvent: SoundEvent,
	private val player: Player
) : AbstractTickableSoundInstance(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom()) {
	var stopped: Boolean = false

	init {
		this.delay = 0
	}

	fun kill() {
		this.volume = 0f
		this.stopped = true
		this.stop()
	}

	override fun tick() {
		if (this.stopped) {
			this.kill()
			return
		}
		if (!this.player.isRemoved) {
			this.x = this.player.x
			this.y = this.player.y
			this.z = this.player.z
		} else this.kill()
	}

	override fun isLooping(): Boolean = true
}