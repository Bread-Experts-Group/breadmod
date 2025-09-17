package org.bread_experts_group.breadmod.client.sound

import com.mojang.blaze3d.audio.Channel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.sounds.AudioStream
import net.minecraft.client.sounds.SoundBufferLibrary
import net.minecraft.client.sounds.SoundManager
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.valueproviders.ConstantFloat
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.stream.BaseAudioStream
import org.bread_experts_group.breadmod.client.sound.stream.ImageData
import org.bread_experts_group.breadmod.client.sound.stream.MP3AudioStream
import org.bread_experts_group.breadmod.client.sound.stream.RIFFAudioStream
import org.bread_experts_group.breadmod.registry.Registry.playingSounds
import java.net.URI
import java.util.concurrent.CompletableFuture

class StereoSoundInstance(
	val uri: URI,
	pos: Vec3
) : BreadModTickingSoundInstance(pos, 100.0, SoundEvents.EMPTY, BLOCKS) {
	companion object {
		fun destroy(isClientSide: Boolean, pos: BlockPos): Boolean {
			if (!isClientSide) return false
			val instance = playingSounds[pos.center] as? StereoSoundInstance ?: return false
			instance.stop()
			if (instance.stream.image != ImageData.EMPTY)
				localClient.textureManager.release(instance.stream.image.location)
			instance.logger.info("Removing sound instance from ${instance.originPos}")
			instance.remove()
			return true
		}
	}

	val stream: BaseAudioStream
	val fileName: Component = Component.literal(this.uri.path.substringAfterLast('/'))
	val logger: Logger = LogManager.getLogger("StereoSoundInstance")

	override fun onTogglePause(channel: Channel, isPausing: Boolean) {
		this.stream.togglePaused()
	}

	init {
		val extension = this.uri.path.substringAfter('.').lowercase()
		this.stream = when (extension) {
			"mp3" -> MP3AudioStream(this.uri)
			"wav" -> RIFFAudioStream(this.uri)
			else -> throw IllegalStateException("Unsupported file format: $extension")
		}
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

	override fun tick(player: LocalPlayer) {
		super.tick(player)
		this.setVolume(this.volume)
	}
}