package bread.mod.breadmod.client.sound

import bread.mod.breadmod.registry.item.ModItems
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EquipmentSlot

class MachSoundInstance(
    soundEvent: SoundEvent,
    val player: LocalPlayer
) : AbstractTickableSoundInstance(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom()) {
    var allowedToLoop: Boolean = true

    init {
        looping = true
        volume = 1f
        delay = 0
    }

    override fun tick() {

        if (!player.isRemoved && player.getItemBySlot(EquipmentSlot.HEAD)
                .`is`(ModItems.CHEF_HAT.get()) && player.isSprinting && allowedToLoop
        ) {
            x = player.x
            y = player.y
            z = player.z
        } else stop()
    }

    override fun isLooping(): Boolean = looping && allowedToLoop
}