package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based block model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see bread.mod.breadmod.datagen.model.block
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartItemModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartItemModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates block model definition files according to annotations in [bread.mod.breadmod.datagen.model.block]
     * use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            forEvent.generator.addProvider(
                true,
                object : ItemModelProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    override fun registerModels() = getItemModelMap().forEach { (register, _) ->
                        basicItem(register.get())
                    }
                }
            )
        }
    }
}
