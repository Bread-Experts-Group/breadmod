package org.bread_experts_group.breadmod.tool_gun.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundSource
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class KSPSoundInstance : AbstractTickableSoundInstance(
	ModSounds.KSP_BUILDMODE.get(),
	SoundSource.MASTER,
	SoundInstance.createUnseededRandom()
) {
	var shouldPlay: Boolean = false

	init {
		this.volume = 0.5f
	}

	override fun tick() {
		if (this.shouldPlay) {
			this.pitch = 1f
			if (this.volume < 0.5f) this.volume += 0.1f
		} else {
			if (this.volume > 0f) {
				this.volume -= 0.1f
			} else this.pitch = 0f
		}
	}

	override fun isLooping(): Boolean = true
}