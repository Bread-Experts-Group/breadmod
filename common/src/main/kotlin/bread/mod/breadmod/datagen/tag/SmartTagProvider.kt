package bread.mod.breadmod.datagen.tag

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.util.ensureRegistrySupplier
import dev.architectury.registry.registries.RegistrySupplier
import kotlin.reflect.KProperty1

/**
 * Abstract class for [SmartTagProvider]s.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartTagProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartTagProvider].
     * Reads off all properties tagged with annotations in [bread.mod.breadmod.datagen.tag].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getTagMap(): Map<RegistrySupplier<*>, Pair<Array<DataGenerateTag>, KProperty1<*, *>>> = buildMap {
        listOf(scanner.getObjectPropertiesAnnotatedWith<DataGenerateTag>()).forEach {
            it.forEach { (property, data) ->
                put(data.first.ensureRegistrySupplier(property), Pair(data.second, property))
            }
        }
    }
}