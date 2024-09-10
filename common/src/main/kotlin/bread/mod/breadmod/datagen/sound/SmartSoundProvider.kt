package bread.mod.breadmod.datagen.sound

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.util.ensureRegistrySupplierAndValue
import net.minecraft.sounds.SoundEvent

abstract class SmartSoundProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getSoundMap(): Map<SoundEvent, Array<DataGenerateSound>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateSound>().forEach { (property, data) ->
            this[data.first.ensureRegistrySupplierAndValue<SoundEvent>(property).get()] = data.second
        }
    }
}