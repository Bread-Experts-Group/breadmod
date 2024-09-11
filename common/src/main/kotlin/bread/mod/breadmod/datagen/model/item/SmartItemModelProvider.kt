package bread.mod.breadmod.datagen.model.item

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.util.ensureRegistrySupplierAndValue
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.world.item.Item
import kotlin.reflect.KProperty1

/**
 * Abstract class for [SmartItemModelProvider]s.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartItemModelProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartItemModelProvider].
     * Reads off all properties tagged with annotations in [bread.mod.breadmod.datagen.model.item].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getItemModelMap(): Map<RegistrySupplier<Item>, Pair<Annotation, KProperty1<*, *>>> = buildMap {
        listOf(
            // todo need handheld and layered items
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateItemModel>(),
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateHandheldItemModel>()
        ).forEach {
            it.forEach { (property, data) ->
                put(data.first.ensureRegistrySupplierAndValue<Item>(property), Pair(data.second.first(), property))
            }
        }
    }
}