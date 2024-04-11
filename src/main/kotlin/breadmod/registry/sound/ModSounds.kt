package breadmod.registry.sound

import breadmod.BreadMod
import breadmod.BreadMod.modLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSounds {
    val deferredRegister: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreadMod.ID)

    val TEST_SOUND: RegistryObject<SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: RegistryObject<SoundEvent> = registerSoundEvents("happy_block_fuse")

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent> {
        return deferredRegister.register(
            name
        ) {
            SoundEvent.createVariableRangeEvent(
                modLocation(name)
            )
        }
    }
}
