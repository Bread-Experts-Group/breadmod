package bread.mod.breadmod.util

import bread.mod.breadmod.client.sound.MachSoundInstance
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.renderMachTrail
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.authlib.GameProfile

data class MachTrailData(var playerProfile: GameProfile) {
    val player = rgMinecraft.level?.getPlayerByUUID(playerProfile.id)!!
    val machOneSound = MachSoundInstance(ModSounds.MACH_ONE.get(), 1..20, player)
    val machTwoSound = MachSoundInstance(ModSounds.MACH_TWO.get(), 21..40, player)
    val machThreeSound = MachSoundInstance(ModSounds.MACH_THREE.get(), 40..70, player)
    val machFourSound = MachSoundInstance(ModSounds.MACH_FOUR.get(), 70..Int.MAX_VALUE, player)
    var sprintTimer = 0
    var shouldTick = true

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