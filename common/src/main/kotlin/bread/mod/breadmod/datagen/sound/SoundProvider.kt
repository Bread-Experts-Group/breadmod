package bread.mod.breadmod.datagen.sound

import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.sounds.SoundEvent

abstract class SoundProvider<T> (
    modID: String, forClassLoader: ClassLoader, forPackage: Package
): DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getSoundMap(): Map<SoundEvent, DataGenerateSound> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateSound>().forEach { (value, annotations) ->
/*            if (value is SoundEvent) {
                println("Got sound event: $value")
            } else {
                println("$value is not SoundEvent")
            }*/
            this[value as SoundEvent] = annotations[0]
        }
    }
}