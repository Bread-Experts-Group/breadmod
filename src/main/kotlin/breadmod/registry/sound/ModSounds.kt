package breadmod.registry.sound

import breadmod.ModMain
import breadmod.ModMain.modLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSounds {
    val deferredRegister: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.ID)

    val TEST_SOUND: RegistryObject<SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: RegistryObject<SoundEvent> = registerSoundEvents("happy_block_fuse")
    val ULTRAMARINE: RegistryObject<SoundEvent> = registerSoundEvents("ultramarine")
    val POW: RegistryObject<SoundEvent> = registerSoundEvents("pow")
    val SCREAM: RegistryObject<SoundEvent> = registerSoundEvents("scream")
    val MINIGUN: RegistryObject<SoundEvent> = registerSoundEvents("minigun")
    val TOOL_GUN: RegistryObject<SoundEvent> = registerSoundEvents("tool_gun")

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent> {
        return deferredRegister.register(name) {
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }
}
