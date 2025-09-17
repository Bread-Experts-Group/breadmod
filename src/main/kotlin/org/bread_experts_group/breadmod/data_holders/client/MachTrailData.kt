package org.bread_experts_group.breadmod.data_holders.client

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.EquipmentSlot.HEAD
import org.bread_experts_group.breadmod.client.render.buffer.MachTrailBufferTask
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.MachSoundInstance
import org.bread_experts_group.breadmod.data_holders.common.MachSpeedData
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.logDebugInfo

data class MachTrailData(val targetPlayer: LocalPlayer) {
	private val sounds: List<MachSoundInstance> = listOf(
		MachSoundInstance(ModSounds.MACH_ONE.get(), this.targetPlayer),
		MachSoundInstance(ModSounds.MACH_TWO.get(), this.targetPlayer),
		MachSoundInstance(ModSounds.MACH_THREE.get(), this.targetPlayer),
		MachSoundInstance(ModSounds.MACH_FOUR.get(), this.targetPlayer)
	)

	fun tick() {
		val stack = this.targetPlayer.getItemBySlot(HEAD)
		if (!stack.`is`(ModItems.CHEF_HAT.get())) return
		val data = MachSpeedData.get(stack)
		logDebugInfo("MachTrailData", data.machStage)

		if (this.targetPlayer.isSprinting) {
			this.setSounds(data.sprintTimer)
			if (data.machStage > 1) MachTrailBufferTask.create(this.targetPlayer)
		} else if (!this.targetPlayer.isSprinting) repeat(4, this::killSound)
	}

	private fun killSound(id: Int) {
		val sound = this.sounds[id]
		sound.kill()
	}

	private fun playSound(id: Int) {
		val sound = this.sounds[id]
		sound.stopped = false
		sound.play()
	}

	fun killAllSounds(): Unit = repeat(4) { this.sounds[it].kill() }

	private fun setSounds(timer: Int) {
		when (timer) {
			1 -> this.playSound(0)
			2 -> if (!localClient.soundManager.isActive(this.sounds[0])) this.playSound(0)
			20 -> {
				this.killSound(0)
				this.playSound(1)
			}
			41 -> {
				this.killSound(1)
				this.playSound(2)
			}
			70 -> {
				this.killSound(2)
				this.playSound(3)
			}
		}
	}
}