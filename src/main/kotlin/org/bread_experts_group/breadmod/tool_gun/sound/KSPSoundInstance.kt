package org.bread_experts_group.breadmod.tool_gun.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.sounds.SoundSource
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import java.util.concurrent.CompletableFuture

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

	override fun getStream(
		soundBuffers: SoundBufferLibrary,
		sound: Sound,
		looping: Boolean
	): CompletableFuture<AudioStream> {
		return super.getStream(soundBuffers, sound, looping)
	}

	override fun isLooping(): Boolean = true
}