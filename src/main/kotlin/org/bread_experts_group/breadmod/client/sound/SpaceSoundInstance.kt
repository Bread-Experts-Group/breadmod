package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class SpaceSoundInstance(
	private val entity: LivingEntity
) : BreadModTickingSoundInstance(entity.position(), 100.0, ModSounds.THE_MOON.get(), SoundSource.AMBIENT) {
	override fun tick(player: LocalPlayer) {
		super.tick(player)
		if (!this.entity.isDeadOrDying) {
			this.setPos(this.entity.position())
			this.setChannelVolume(this.volume)
		} else this.kill()
	}
}