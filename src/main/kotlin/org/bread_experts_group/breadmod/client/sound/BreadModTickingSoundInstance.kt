package org.bread_experts_group.breadmod.client.sound

import com.mojang.blaze3d.audio.Channel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.ChannelAccess
import net.minecraft.client.sounds.SoundManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.Registry
import java.lang.Math.clamp

abstract class BreadModTickingSoundInstance(
	val originPos: Vec3,
	var falloffDistance: Double,
	sound: ResourceLocation,
	soundSource: SoundSource
) : AbstractSoundInstance(sound, soundSource, SoundInstance.createUnseededRandom()) {
	constructor(pos: Vec3, falloffDistance: Double, sound: SoundEvent, soundSource: SoundSource) : this(
		pos,
		falloffDistance,
		sound.location,
		soundSource
	)

	constructor(player: Player, falloffDistance: Double, sound: SoundEvent, soundSource: SoundSource) : this(
		player.position(),
		falloffDistance,
		sound,
		soundSource
	)

	var stopped: Boolean = false
	var killed: Boolean = false
	private val soundManager: SoundManager = localClient.soundManager

	init {
		this.x = this.originPos.x
		this.y = this.originPos.y
		this.z = this.originPos.z
		this.delay = 0
		this.looping = false
	}

	fun kill() {
		this.volume = 0f
		this.killed = true
		this.stop()
		this.remove()
	}

	fun play() {
		this.soundManager.play(this)
		Registry.tickingSoundInstances[this.originPos] = this
	}

	fun isActive(): Boolean = Registry.tickingSoundInstances[this.originPos] != null

	fun stop(stopTicking: Boolean = false) {
		this.stopped = true
		this.looping = false
		this.soundManager.stop(this)
		if (stopTicking) this.remove()
	}

	fun remove() {
		Registry.tickingSoundInstances.remove(this.originPos)
	}

	fun isPaused(): Boolean {
		val channel = this.getChannelHandle() ?: return false
		var paused = false
		channel.execute { channel ->
			if (channel.state == 4114) paused = true
			else if (channel.state == 4115) paused = false
		}
		return paused
	}

	fun togglePause() {
		if (this.isPaused()) this.unpause() else this.pause()
	}

	fun pause() {
		val handle = this.getChannelHandle() ?: return
		handle.execute { channel ->
			this.onTogglePause(channel, true)
			channel.pause()
		}
	}

	fun unpause() {
		val handle = this.getChannelHandle() ?: return
		handle.execute { channel ->
			this.onTogglePause(channel, false)
			channel.unpause()
		}
	}

	fun setPos(): Unit = this.setPos(this.x, this.y, this.z)

	fun setPos(x: Double, y: Double, z: Double): Unit = this.setPos(Vec3(x, y, z))

	fun setPos(vec: Vec3) {
		val handle = this.getChannelHandle() ?: return
		handle.execute { channel ->
			channel.setSelfPosition(vec)
		}
	}

	fun setChannelVolume(): Unit = this.setChannelVolume(this.volume)

	fun setChannelVolume(newVolume: Float) {
		val handle = this.getChannelHandle() ?: return
		handle.execute { channel ->
			channel.setVolume(newVolume)
		}
	}

	fun updateVolumeFromPlayerPosition(player: LocalPlayer) {
		val playerPos = player.position()
		val normalized = clamp(
			this.falloffDistance / clamp(this.originPos.distanceTo(playerPos), 0.0, this.falloffDistance) - 1.0,
			0.0,
			1.0
		)
		this.volume = normalized.toFloat()
	}

	private fun getChannelHandle(): ChannelAccess.ChannelHandle? =
		localClient.soundManager.soundEngine.instanceToChannel[this]

	open fun onTogglePause(channel: Channel, isPausing: Boolean) {}

	/**
	 * @param player The local client player.
	 */
	open fun tick(player: LocalPlayer) {
		if (this.killed) {
			this.kill()
			return
		}
		if (this.stopped) this.stop()
		this.updateVolumeFromPlayerPosition(player)
		this.setChannelVolume(0.5f)
	}
}