package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource.BLOCKS
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import java.lang.Math.clamp
import java.util.concurrent.CompletableFuture

class StereoSoundInstance(
	sound: SoundEvent,
	private val pos: BlockPos,
	private val falloffDistance: Double
) : AbstractTickableSoundInstance(
	sound,
	BLOCKS,
	SoundInstance.createUnseededRandom()
) {
	override fun tick() {
		val playerPos = (localClient.player ?: return).position()
		val (x, y, z) = this.pos.center
		this.x = x
		this.y = y
		this.z = z
		val normalized = clamp(
			this.falloffDistance / clamp(this.pos.center.distanceTo(playerPos), 0.0, this.falloffDistance) - 1.0,
			0.0,
			1.0
		)
		this.volume = normalized.toFloat()
	}

	override fun isRelative(): Boolean = true
	override fun getAttenuation(): Attenuation = Attenuation.LINEAR

	override fun getStream(
		soundBuffers: SoundBufferLibrary,
		sound: Sound,
		looping: Boolean
	): CompletableFuture<AudioStream> {
		return super.getStream(soundBuffers, sound, looping)
	}
}