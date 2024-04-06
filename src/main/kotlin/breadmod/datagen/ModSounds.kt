package breadmod.datagen

import breadmod.BreadMod
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.SoundDefinitionsProvider
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSounds {
    val REGISTRY: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreadMod.ID)

    val TEST_SOUND: RegistryObject<SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: RegistryObject<SoundEvent> = registerSoundEvents("happy_block_fuse")

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent> {
        return REGISTRY.register(
            name
        ) {
            SoundEvent.createVariableRangeEvent(
                ResourceLocation(BreadMod.ID, name)
            )
        }
    }

    internal open class ModSoundsDatagen(
        output: PackOutput?,
        id: String?,
        helper: ExistingFileHelper?
    ) :
        SoundDefinitionsProvider(output!!, id.toString(), helper!!) {
        override fun registerSounds() { // Adds each entry to sounds.json
            this.add(
                TEST_SOUND, definition().subtitle(("sound." + BreadMod.ID) + ".test_sound")
                    .with(sound(ResourceLocation(BreadMod.ID, "test_sound")).volume(1f).stream())
            )
            this.add(
                HAPPY_BLOCK_FUSE, definition().subtitle("sound." + BreadMod.ID + ".happy_block_fuse")
                    .with(sound(ResourceLocation(BreadMod.ID, "happy_block_fuse")).volume(1f))
            )
        }
    }
}
