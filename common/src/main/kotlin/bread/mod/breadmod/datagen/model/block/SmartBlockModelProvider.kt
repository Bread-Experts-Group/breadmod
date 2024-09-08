package bread.mod.breadmod.datagen.model.block

import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block

/**
 * Abstract class for [SmartBlockModelProvider]s, reading and assorting locales to description IDs and translations.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartBlockModelProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartBlockModelProvider].
     * Reads off all properties tagged with [DataGenerateBlockModel], assorting them into
     * locale -> description ID, translation groups.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getBlockModelMap(): Map<Block, Annotation> = buildMap {
        fun Any?.getBlock() = if (this is BlockItem) this.block else this as Block

        listOf(
            *scanner.getObjectPropertiesAnnotatedWith<DataGenerateBlockModel>().toTypedArray(),
            *scanner.getObjectPropertiesAnnotatedWith<DataGenerateBlockAndItemModel>().toTypedArray()
        ).forEach { (value, annotations) -> this[value.getBlock()] = annotations[0] }
    }
}