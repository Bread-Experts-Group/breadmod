package bread.mod.breadmod.datagen.model.item

import bread.mod.breadmod.datagen.DataProviderScanner
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
            scanner.getObjectPropertiesAnnotatedWith<DataGenerateItemModel>()
        ).forEach {
            it.forEach { (property, data) ->
                val supplier = data.first
                if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException("${property.name} must be of type ${RegistrySupplier::class.qualifiedName}.")
                val rx = supplier.get()
                if (rx !is Item) throw IllegalArgumentException("${supplier.id} must supply type ${Item::class.qualifiedName}.")

                @Suppress("UNCHECKED_CAST")
                put(supplier as RegistrySupplier<Item>, Pair(data.second.first(), property))
            }
        }
    }
}