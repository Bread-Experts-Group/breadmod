package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.block.DataGenerateBlockModel
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator


/**
 * An annotation-based block model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see DataGenerateBlockModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartBlockModelProviderFabric(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartBlockModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates block model definition files according to [DataGenerateBlockModel] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {

    }
}
