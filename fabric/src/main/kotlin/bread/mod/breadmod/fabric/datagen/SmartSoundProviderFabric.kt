package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.sound.SoundProvider
import bread.mod.breadmod.fabric.FabricSoundProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class SmartSoundProviderFabric(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SoundProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: FabricDataGenerator.Pack) {
        getSoundMap().forEach { (sound, data) ->
            forEvent.addProvider { dataOutput, future ->
                object : FabricSoundProvider(dataOutput, future) {
                    override fun registerSounds() {
                        add(
                            data.name,
                            sound,
                            data.volume,
                            data.pitch,
                            data.stream
                        )
                    }
                }
            }
        }
    }
}