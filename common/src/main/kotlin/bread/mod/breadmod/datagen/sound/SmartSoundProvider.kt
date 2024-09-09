package bread.mod.breadmod.datagen.sound

import bread.mod.breadmod.datagen.DataProviderScanner
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.sounds.SoundEvent

abstract class SmartSoundProvider<T> (
    modID: String, forClassLoader: ClassLoader, forPackage: Package
): DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getSoundMap(): Map<SoundEvent, Array<DataGenerateSound>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateSound>().forEach { (property, data) ->
            val supplier = data.first
            if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException("${property.name} must be of type ${RegistrySupplier::class.qualifiedName}.")
            this[supplier.get() as SoundEvent] = data.second
        }
    }
}