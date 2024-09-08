package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based item model provider.
 *
 * @property modID The mod ID to save item model definitions for.
 *
 * @see DataGenerateItemModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartItemModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartItemModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates item model definition files according to [DataGenerateItemModel] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            forEvent.generator.addProvider(
                true,
                object : ItemModelProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    override fun registerModels() = getItemModelMap().forEach { (item, _) ->
                        basicItem(item)
                    }
                }
            )
        }
    }
}
