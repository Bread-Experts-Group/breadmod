package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.client.sounds.SoundManager
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.valueproviders.ConstantFloat
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.coder.format.riff.RIFFInputStream
import java.io.InputStream
import java.lang.Math.clamp
import java.util.concurrent.CompletableFuture

class StereoSoundInstance(
	private val inputStream: InputStream,
	private val pos: BlockPos,
	private val falloffDistance: Double
) : AbstractTickableSoundInstance(
	SoundEvents.EMPTY,
	BLOCKS,
	SoundInstance.createUnseededRandom()
) {
	init {
		val player = localClient.player!!
		this.x = player.x
		this.y = player.y
		this.z = player.z
	}

	override fun getSound(): Sound = Sound(
		SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION,
		ConstantFloat.of(1f),
		ConstantFloat.of(1f),
		1,
		Sound.Type.FILE,
		true,
		false,
		this.falloffDistance.toInt()
	)

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

	override fun getStream(
		soundBuffers: SoundBufferLibrary,
		sound: Sound,
		looping: Boolean
	): CompletableFuture<AudioStream> {
		return CompletableFuture.completedFuture(RIFFAudioStream(RIFFInputStream(this.inputStream)))
	}
}