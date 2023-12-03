package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.SoundDefinition.Sound
import net.minecraftforge.common.data.SoundDefinitionsProvider
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject


typealias RegisteredSoundEvent = RegistryObject<SoundEvent>
object ModSounds {
    val REGISTRY: DeferredRegister<SoundEvent> = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreadMod.ID)
    private fun DeferredRegister<SoundEvent>.register(name: String): RegisteredSoundEvent = this.register(name) { SoundEvent(BreadMod.resource(name)) }

    val TEST_SOUND: RegisteredSoundEvent = REGISTRY.register("test_sound")

    class Generator(
        generator: DataGenerator,
        fileHelper: ExistingFileHelper,
    ) : SoundDefinitionsProvider(generator, BreadMod.ID, fileHelper) {
        private fun registerSound(soundEvent: RegisteredSoundEvent, subtitle: String?, builder: (Sound) -> Unit) =
            add(soundEvent, definition().also { definition ->
                if(subtitle != null) { definition.subtitle(subtitle) }
                sound(soundEvent.id).also { builder(it); definition.with(it) }
            })

        override fun registerSounds() {
            registerSound(TEST_SOUND, "test sound") { it.stream() }

        }
    }
}