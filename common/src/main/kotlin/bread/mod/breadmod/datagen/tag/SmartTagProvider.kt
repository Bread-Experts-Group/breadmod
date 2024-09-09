package bread.mod.breadmod.datagen.tag

import bread.mod.breadmod.datagen.DataProviderScanner
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
                val supplier = data.first
                if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException("${property.name} must be of type ${RegistrySupplier::class.qualifiedName}.")
                put(supplier, Pair(data.second, property))
            }
        }
    }
}