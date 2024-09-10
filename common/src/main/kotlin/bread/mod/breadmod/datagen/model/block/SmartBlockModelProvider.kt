package bread.mod.breadmod.datagen.model.block

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockModel
import bread.mod.breadmod.util.ensureRegistrySupplier
import dev.architectury.registry.registries.RegistrySupplier
import kotlin.reflect.KProperty1

/**
 * Abstract class for [SmartBlockModelProvider]s.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartBlockModelProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartBlockModelProvider].
     * Reads off all properties tagged with annotations in [bread.mod.breadmod.datagen.model.block].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getBlockModelMap(): Map<RegistrySupplier<*>, Pair<Annotation, KProperty1<*, *>>> = buildMap {
        listOf(
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateCubeAllBlockModel>(),
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateCubeAllBlockAndItemModel>(),

            scanner.getObjectPropertiesAnnotatedWith<DataGenerateWithExistingParentBlockModel>(),
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateWithExistingParentBlockAndItemModel>()
        ).forEach {
            it.forEach { (property, data) ->
                put(data.first.ensureRegistrySupplier(property), Pair(data.second.first(), property))
            }
        }
    }
}