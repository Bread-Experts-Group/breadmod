package bread.mod.breadmod.registry.sound

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.language.DataGenerateTooltipLang
import bread.mod.breadmod.datagen.sound.DataGenerateSound
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent

object ModSounds {
    internal val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ModMainCommon.MOD_ID, Registries.SOUND_EVENT)

    @DataGenerateSound("test_sound", stream = true)
    @DataGenerateLanguage("en_us", "Test")
    @DataGenerateTooltipLang("en_us", "ClascyJitto - Secret Hoppin'")
    val TEST_SOUND: RegistrySupplier<SoundEvent> = registerSoundEvents("test_sound")

    @DataGenerateSound("happy_block_fuse")
    @DataGenerateLanguage("en_us", "HAPPY HAPPY HAPPY")
    val HAPPY_BLOCK_FUSE: RegistrySupplier<SoundEvent> = registerSoundEvents("happy_block_fuse")

    @DataGenerateSound("ultramarine", stream = true)
    @DataGenerateLanguage("en_us", "Ultramarine plays")
    val ULTRAMARINE: RegistrySupplier<SoundEvent> = registerSoundEvents("ultramarine")

    @DataGenerateSound("pow")
    @DataGenerateLanguage("en_us", "Pow!")
    val POW: RegistrySupplier<SoundEvent> = registerSoundEvents("pow")

    @DataGenerateSound("scream")
    @DataGenerateLanguage("en_us", "AAAAAAAAAAAAAAA-")
    val SCREAM: RegistrySupplier<SoundEvent> = registerSoundEvents("scream")

    @DataGenerateSound("minigun")
    @DataGenerateLanguage("en_us", "Minigun fires")
    val MINIGUN: RegistrySupplier<SoundEvent> = registerSoundEvents("minigun")

//    val TOOL_GUN: RegistrySupplier<SoundEvent> = registerSoundEvents(TOOL_GUN_DEF)

    @DataGenerateSound("hell_naw")
    @DataGenerateLanguage("en_us", "HELL NAW!")
    val HELL_NAW: RegistrySupplier<SoundEvent> = registerSoundEvents("hell_naw")

    @DataGenerateSound("war_timer")
    @DataGenerateLanguage("en_us", "War Timer counts down")
    val WAR_TIMER: RegistrySupplier<SoundEvent> = registerSoundEvents("war_timer")

    @DataGenerateSound("war_timer_up")
    @DataGenerateLanguage("en_us", "War Timer increases")
    val WAR_TIMER_UP: RegistrySupplier<SoundEvent> = registerSoundEvents("war_timer_up")

    @DataGenerateSound("mach_one")
    val MACH_ONE: RegistrySupplier<SoundEvent> = registerSoundEvents("mach_one")

    @DataGenerateSound("mach_two")
    val MACH_TWO: RegistrySupplier<SoundEvent> = registerSoundEvents("mach_two")

    @DataGenerateSound("mach_three")
    val MACH_THREE: RegistrySupplier<SoundEvent> = registerSoundEvents("mach_three")

    @DataGenerateSound("mach_four")
    val MACH_FOUR: RegistrySupplier<SoundEvent> = registerSoundEvents("mach_four")

    private fun registerSoundEvents(name: String): RegistrySupplier<SoundEvent> {
        return SOUND_REGISTRY.register(name) {
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }
}