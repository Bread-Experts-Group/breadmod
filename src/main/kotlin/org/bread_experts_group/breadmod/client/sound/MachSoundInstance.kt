package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player

class MachSoundInstance(
	soundEvent: SoundEvent,
	player: Player
) : BreadModTickingSoundInstance(player, 50.0, soundEvent, SoundSource.AMBIENT) {
	override fun tick(player: LocalPlayer) {
		super.tick(player)
		if (!player.isRemoved) {
			this.x = player.x
			this.y = player.y
			this.z = player.z
		} else this.kill()
	}

	override fun isLooping(): Boolean = true
}