package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.sound.BreadModTickingSoundInstance
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class LidarSound(playerPos: Vec3) : BreadModTickingSoundInstance(
	playerPos,
	10.0,
	ModSounds.LIDAR_SCAN.get(),
	SoundSource.AMBIENT
) {
	override fun tick(player: LocalPlayer) {
		this.setPos(player.position())
		this.volume = 0.5f
		this.setChannelVolume()
		this.setChannelPitch(1f + (LidarHandler.currentDeviation / 20f))
	}

	override fun isLooping(): Boolean = true
}