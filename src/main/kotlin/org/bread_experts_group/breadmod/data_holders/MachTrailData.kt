package org.bread_experts_group.breadmod.data_holders

import com.mojang.authlib.GameProfile
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadmod.client.sound.MachSoundInstance
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.client.render.buffer.render.MachTrailBufferTask
import org.bread_experts_group.breadmod.client.render.localClient

data class MachTrailData(var playerProfile: GameProfile) {
	val player: Player = localClient.level?.getPlayerByUUID(this.playerProfile.id)!!
	private val machOneSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_ONE.get(), 1 .. 20, this.player)
	private val machTwoSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_TWO.get(), 21 .. 40, this.player)
	private val machThreeSound: MachSoundInstance =
		MachSoundInstance(ModSounds.MACH_THREE.get(), 40 .. 70, this.player)
	val machFourSound: MachSoundInstance =
		MachSoundInstance(ModSounds.MACH_FOUR.get(), 70 .. Int.MAX_VALUE, this.player)
	private var sprintTimer: Int = 0
	private var shouldTick: Boolean = true
	fun tick() {
		val soundManager = localClient.soundManager
		if (this.player.isSprinting && this.shouldTick) {
			this.machOneSound.timer = this.sprintTimer
			this.machTwoSound.timer = this.sprintTimer
			this.machThreeSound.timer = this.sprintTimer
			this.machFourSound.timer = this.sprintTimer

			when (this.sprintTimer) {
				1  -> soundManager.play(this.machOneSound)
				20 -> soundManager.play(this.machTwoSound)
				41 -> soundManager.play(this.machThreeSound)
				70 -> {
					this.machFourSound.shouldLoop = true
					soundManager.play(this.machFourSound)
				}
			}
			this.sprintTimer++
			if (this.sprintTimer >= 20) {
				MachTrailBufferTask.create(this.playerProfile)
			}
		} else if (!this.player.isSprinting || !this.shouldTick) {
			this.machFourSound.shouldLoop = false
			this.sprintTimer = 0
			soundManager.stop(this.machOneSound)
			soundManager.stop(this.machTwoSound)
			soundManager.stop(this.machThreeSound)
			soundManager.stop(this.machFourSound)
		}
	}
}