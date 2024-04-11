package breadmod.datagen

import breadmod.BreadMod
import breadmod.BreadMod.modLocation
import breadmod.registry.sound.ModSounds
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.SoundDefinitionsProvider

class ModSoundDefinitionsProvider(
    output: PackOutput,
    id: String,
    helper: ExistingFileHelper
) : SoundDefinitionsProvider(output, id, helper) {
    override fun registerSounds() { // Adds each entry to sounds.json
        this.add(
            ModSounds.TEST_SOUND, definition()
                .subtitle("sound.$BreadMod.ID.test_sound")
                .with(sound(modLocation("test_sound")).volume(1f).stream())
        )
        this.add(
            ModSounds.HAPPY_BLOCK_FUSE, definition()
                .subtitle("sound.$BreadMod.ID.happy_block_fuse")
                .with(sound(modLocation("happy_block_fuse")).volume(1f))
        )
    }
}