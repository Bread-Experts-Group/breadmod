package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.client.sounds.SoundManager
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.valueproviders.ConstantFloat
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.playingSounds
import org.bread_experts_group.breadmod.client.sound.stream.BaseAudioStream
import org.bread_experts_group.breadmod.client.sound.stream.ImageData
import org.bread_experts_group.breadmod.client.sound.stream.MP3AudioStream
import org.bread_experts_group.breadmod.client.sound.stream.RIFFAudioStream
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import java.net.URL
import java.util.concurrent.CompletableFuture

class StereoSoundInstance(
	val url: URL,
	private val pos: BlockPos,
	private val falloffDistance: Double = 50.0
) : AbstractSoundInstance(
	SoundEvents.EMPTY,
	BLOCKS,
	SoundInstance.createUnseededRandom()
) {
	companion object {
		fun destroy(isClientSide: Boolean, pos: BlockPos): Boolean {
			if (!isClientSide) return false
			val instance = playingSounds[pos] ?: return false
			localClient.soundManager.stop(instance)
			if (instance.stream.image != ImageData.EMPTY)
				localClient.textureManager.release(instance.stream.image.location)
			instance.logger.info("Removing sound instance from ${instance.pos}")
			playingSounds.remove(pos)
			return true
		}
	}

	val stream: BaseAudioStream
	val fileName: Component = Component.literal(this.url.path.substringAfterLast('/'))
	val logger: Logger = LogManager.getLogger("StereoSoundInstance")

	fun togglePause() {
		val soundEngine = localClient.soundManager.soundEngine
		val handle = soundEngine.instanceToChannel[this] ?: return
		handle.execute { channel ->
			if (channel.state == 4114) {
				this.stream.togglePaused()
				channel.pause()
			} else if (channel.state == 4115) {
				this.stream.togglePaused()
				channel.unpause()
			}
		}
	}

	init {
		val extension = this.url.file.substringAfter('.').lowercase()
		val (x, y, z) = this.pos.center

		this.stream = when (extension) {
			"mp3" -> MP3AudioStream(this.url)
			"wav" -> RIFFAudioStream(this.url)
			else  -> throw IllegalStateException("Unsupported file format: $extension")
		}

		this.x = x
		this.y = y
		this.z = z
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

	override fun getStream(
		soundBuffers: SoundBufferLibrary,
		sound: Sound,
		looping: Boolean
	): CompletableFuture<AudioStream> {
		return CompletableFuture.completedFuture(this.stream)
	}

	//	override fun tick() {
//		this.volume = 1f
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
}