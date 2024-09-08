package bread.mod.breadmod.datagen.sound

import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.sounds.SoundEvent

abstract class SmartSoundProvider<T> (
    modID: String, forClassLoader: ClassLoader, forPackage: Package
): DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getSoundMap(): Map<SoundEvent, Array<DataGenerateSound>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateSound>().forEach { (value, annotations) ->
            this[value.get() as SoundEvent] = annotations
        }
    }
}