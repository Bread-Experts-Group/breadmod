package breadmod.registry.sound

import breadmod.ModMain
import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSounds {
    internal val deferredRegister: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.ID)

    val TEST_SOUND: RegistryObject<SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: RegistryObject<SoundEvent> = registerSoundEvents("happy_block_fuse")
    val ULTRAMARINE: RegistryObject<SoundEvent> = registerSoundEvents("ultramarine")
    val POW: RegistryObject<SoundEvent> = registerSoundEvents("pow")
    val SCREAM: RegistryObject<SoundEvent> = registerSoundEvents("scream")
    val MINIGUN: RegistryObject<SoundEvent> = registerSoundEvents("minigun")
    val TOOL_GUN: RegistryObject<SoundEvent> = registerSoundEvents(TOOL_GUN_DEF)
    val HELL_NAW: RegistryObject<SoundEvent> = registerSoundEvents("hell_naw")
    val WAR_TIMER: RegistryObject<SoundEvent> = registerSoundEvents("war_timer")
    val WAR_TIMER_UP: RegistryObject<SoundEvent> = registerSoundEvents("war_timer_up")

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent> {
        return deferredRegister.register(name) {
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }
}
