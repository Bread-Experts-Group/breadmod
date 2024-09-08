package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.sound.DataGenerateSound
import bread.mod.breadmod.datagen.sound.SmartSoundProvider
import net.neoforged.neoforge.common.data.SoundDefinition
import net.neoforged.neoforge.common.data.SoundDefinition.SoundType
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class SmartSoundProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartSoundProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: GatherDataEvent) {
        forEvent.generator.addProvider(true, object : SoundDefinitionsProvider(
            forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
            override fun registerSounds() = getSoundMap().forEach { (event, annotations) ->
                val definition = SoundDefinition.definition()
                    .subtitle(event.location.toLanguageKey("sound"))
                annotations.forEach {
                    definition.with(
                        sound(
                            modID + ":" + it.sound,
                            if (it.type == DataGenerateSound.Type.FILE) SoundType.SOUND else SoundType.EVENT
                        ).volume(it.volume)
                            .pitch(it.pitch)
                            .weight(it.weight)
                            .stream(it.stream)
                            .attenuationDistance(it.attenuation_distance)
                            .preload(it.preload)
                    )
                }

                add(event, definition)
            }
        })
    }
}