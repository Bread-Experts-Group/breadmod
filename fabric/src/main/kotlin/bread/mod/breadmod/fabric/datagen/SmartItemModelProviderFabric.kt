package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator


/**
 * An annotation-based item model provider.
 *
 * @property modID The mod ID to save item model definitions for.
 *
 * @see DataGenerateItemModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartItemModelProviderFabric(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartItemModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates item model definition files according to [DataGenerateItemModel] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {

    }
}
