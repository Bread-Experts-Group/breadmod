package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.SoundDefinitionsProvider
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object ModSounds {
    val REGISTRY: DeferredRegister<SoundEvent> = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreadMod.ID)

    val TEST_SOUND by REGISTRY.registerObject("test_sound") { SoundEvent(ResourceLocation(BreadMod.ID, "test_sound")) }

    class Generator(
        generator: DataGenerator,
        fileHelper: ExistingFileHelper,
    ) : SoundDefinitionsProvider(generator, BreadMod.ID, fileHelper) {
        override fun registerSounds() {
            add(
                ModSounds.TEST_SOUND,
                definition()
                    .subtitle("gaynerd")
                    .with(
                        sound(ResourceLocation(BreadMod.ID, "test_sound"))
                            .stream()
                    )
            )
        }
    }
}

// i have an amazing idea