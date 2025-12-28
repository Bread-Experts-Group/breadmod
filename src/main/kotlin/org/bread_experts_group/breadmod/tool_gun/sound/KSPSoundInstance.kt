package org.bread_experts_group.breadmod.tool_gun.sound

import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundSource
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.BreadModTickingSoundInstance
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class KSPSoundInstance : BreadModTickingSoundInstance(
	localClient.player!!,
	10.0,
	ModSounds.KSP_BUILDMODE.get(),
	SoundSource.MASTER
) {
	var shouldPlay: Boolean = false

	init {
		this.volume = 0.5f
	}

	override fun tick(player: LocalPlayer) {
		if (this.shouldPlay) {
			if (this.isPaused()) this.togglePause()
			if (this.volume < 0.5f) this.volume += 0.1f
		} else {
			if (this.volume > 0f) this.volume -= 0.1f
			else if (this.volume == 0f && !this.isPaused()) this.togglePause()
		}
	}

	override fun isLooping(): Boolean = true
}