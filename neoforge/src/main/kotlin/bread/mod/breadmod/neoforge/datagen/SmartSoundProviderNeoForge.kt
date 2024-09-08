package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.sound.SoundProvider
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class SmartSoundProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SoundProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: GatherDataEvent) {
        getSoundMap().forEach { (sound, data) ->
            val soundProvider = object : SoundDefinitionsProvider(
                forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                override fun registerSounds() {
                    add(sound, definition()
                        .subtitle("sound.${ModMainCommon.MOD_ID}.${data.name}")
                        .with(sound(sound.location)
                            .volume(data.volume)
                            .pitch(data.pitch)
                            .stream(data.stream)
                        )
                    )
                }
            }

            forEvent.generator.addProvider(true, soundProvider)
        }
    }
}