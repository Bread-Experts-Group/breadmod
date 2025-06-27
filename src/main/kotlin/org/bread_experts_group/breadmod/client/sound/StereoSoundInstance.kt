package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.sounds.SoundSource.BLOCKS
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.coder.format.riff.RIFFInputStream
import java.util.concurrent.CompletableFuture

class StereoSoundInstance : AbstractSoundInstance(
	ModSounds.GOING_UP.id,
	BLOCKS,
	SoundInstance.createUnseededRandom()
) {
	init {
		val player = localClient.player!!
		this.x = player.x
		this.y = player.y
		this.z = player.z
	}
//	override fun tick() {
//		val playerPos = (localClient.player ?: return).position()
//		val (x, y, z) = this.pos.center
//		this.x = x
//		this.y = y
//		this.z = z
//		val normalized = clamp(
//			this.falloffDistance / clamp(this.pos.center.distanceTo(playerPos), 0.0, this.falloffDistance) - 1.0,
//			0.0,
//			1.0
//		)
//		this.volume = normalized.toFloat()
//	}

//	override fun isRelative(): Boolean = true
//	override fun getAttenuation(): Attenuation = Attenuation.LINEAR

	override fun getStream(
		soundBuffers: SoundBufferLibrary,
		sound: Sound,
		looping: Boolean
	): CompletableFuture<AudioStream> {
		return CompletableFuture.completedFuture(
			RIFFAudioStream(RIFFInputStream(this::class.java.getResourceAsStream("/chicken.wav")!!))
		)
	}
}