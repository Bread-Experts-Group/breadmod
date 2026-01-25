package org.bread_experts_group.breadmod.tool_gun.sound

import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundSource
import net.neoforged.fml.earlydisplay.RenderElement.clamp
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
			if (this.volume < 0.5f) {
				this.volume = clamp(this.volume + 0.05f, 0f, 0.5f)
				this.setChannelVolume(this.volume)
			}
			if (this.volume > 0f) this.unpause()
		} else {
			if (this.volume > 0f) {
				this.volume = clamp(this.volume - 0.05f, 0f, 0.5f)
				this.setChannelVolume(this.volume)
			}
			if (this.volume == 0f) this.pause()
		}
	}

	override fun isLooping(): Boolean = true
}