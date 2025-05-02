package org.bread_experts_group.breadmod.data_holders.client

import net.minecraft.world.entity.EquipmentSlot.HEAD
import net.minecraft.world.entity.player.Player
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.buffer.MachTrailBufferTask
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.MachSoundInstance
import org.bread_experts_group.breadmod.data_holders.common.MachSpeedData
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.sound.ModSounds

data class MachTrailData(val targetPlayer: Player) {
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
		LogManager.getLogger(data.machStage)

		if (this.targetPlayer.isSprinting) {
			this.setSounds(data.sprintTimer)
			if (data.machStage > 1) MachTrailBufferTask.create(this.targetPlayer)
		} else if (!this.targetPlayer.isSprinting) repeat(4, this::killSound)
	}

	private fun killSound(id: Int) {
		val soundManager = localClient.soundManager
		val sound = this.sounds[id]
		sound.kill()
		soundManager.stop(sound)
	}

	private fun playSound(id: Int) {
		val soundManager = localClient.soundManager
		val sound = this.sounds[id]
		sound.stopped = false
		soundManager.play(sound)
	}

	fun killAllSounds(): Unit = repeat(4) { this.sounds[it].kill() }

	private fun setSounds(timer: Int) {
		when (timer) {
			1  -> this.playSound(0)
			2  -> if (!localClient.soundManager.isActive(this.sounds[0])) this.playSound(0)
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