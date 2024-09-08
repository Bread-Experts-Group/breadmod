package bread.mod.breadmod.datagen.model.item

import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.world.item.Item

/**
 * Abstract class for [SmartItemModelProvider]s, reading and assorting locales to description IDs and translations.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartItemModelProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartItemModelProvider].
     * Reads off all properties tagged with [DataGenerateItemModel], assorting them into
     * locale -> description ID, translation groups.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getItemModelMap(): Map<Item, DataGenerateItemModel> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateItemModel>().forEach { (value, annotations) ->
            this[value.get() as Item] = annotations[0]
        }
    }
}