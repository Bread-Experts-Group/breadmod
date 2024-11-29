package org.bread_experts_group.breadmod.registry

import com.mojang.authlib.GameProfile
import org.bread_experts_group.breadmod.client.sound.MachSoundInstance
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.renderMachTrail
import org.bread_experts_group.breadmod.util.rgMinecraft

data class MachTrailData(var playerProfile: GameProfile) {
    val player = rgMinecraft.level?.getPlayerByUUID(playerProfile.id)!!
    val machOneSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_ONE.get(), 1..20, player)
    val machTwoSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_TWO.get(), 21..40, player)
    val machThreeSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_THREE.get(), 40..70, player)
    val machFourSound: MachSoundInstance = MachSoundInstance(ModSounds.MACH_FOUR.get(), 70..Int.MAX_VALUE, player)
    var sprintTimer: Int = 0
    var shouldTick: Boolean = true

    fun tick() {
        val soundManager = rgMinecraft.soundManager
        if (player.isSprinting && shouldTick) {
            machOneSound.timer = sprintTimer
            machTwoSound.timer = sprintTimer
            machThreeSound.timer = sprintTimer
            machFourSound.timer = sprintTimer

            when (sprintTimer) {
                1 -> soundManager.play(machOneSound)
                20 -> soundManager.play(machTwoSound)
                41 -> soundManager.play(machThreeSound)
                70 -> {
                    machFourSound.shouldLoop = true
                    soundManager.play(machFourSound)
                }
            }
            sprintTimer++
            if (sprintTimer >= 20) {
                renderMachTrail(playerProfile)
            }
        } else if (!player.isSprinting || !shouldTick) {
            machFourSound.shouldLoop = false
            sprintTimer = 0
            soundManager.stop(machOneSound)
            soundManager.stop(machTwoSound)
            soundManager.stop(machThreeSound)
            soundManager.stop(machFourSound)
        }
    }
}