package bread.mod.breadmod.registry.sound

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent

object ModSounds {
    internal val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ModMainCommon.MOD_ID, Registries.SOUND_EVENT)

    val TEST_SOUND: RegistrySupplier<SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: RegistrySupplier<SoundEvent> = registerSoundEvents("happy_block_fuse")
    val ULTRAMARINE: RegistrySupplier<SoundEvent> = registerSoundEvents("ultramarine")
    val POW: RegistrySupplier<SoundEvent> = registerSoundEvents("pow")
    val SCREAM: RegistrySupplier<SoundEvent> = registerSoundEvents("scream")
    val MINIGUN: RegistrySupplier<SoundEvent> = registerSoundEvents("minigun")
//    val TOOL_GUN: RegistrySupplier<SoundEvent> = registerSoundEvents(TOOL_GUN_DEF)
    val HELL_NAW: RegistrySupplier<SoundEvent> = registerSoundEvents("hell_naw")
    val WAR_TIMER: RegistrySupplier<SoundEvent> = registerSoundEvents("war_timer")
    val WAR_TIMER_UP: RegistrySupplier<SoundEvent> = registerSoundEvents("war_timer_up")

    private fun registerSoundEvents(name: String): RegistrySupplier<SoundEvent> {
        return SOUND_REGISTRY.register(name) {
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }
}