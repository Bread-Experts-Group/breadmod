package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockModel
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates


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
        forEvent.addProvider { dataOutput, _ ->
            object : FabricModelProvider(dataOutput) {
                override fun generateBlockStateModels(p0: BlockModelGenerators) = getModelMap().forEach { (value, annotation) ->
                    when (annotation) {
                        is DataGenerateBlockModel, is DataGenerateBlockAndItemModel -> p0.createGenericCube(value)
                        else -> throw UnsupportedOperationException(annotation::class.simpleName)
                    }
                }

                override fun generateItemModels(p0: ItemModelGenerators) = getModelMap().forEach { (value, annotation) ->
                    when (annotation) {
                        is DataGenerateBlockModel, is DataGenerateBlockAndItemModel -> p0.generateFlatItem(
                            value.asItem(),
                            ModelTemplates.FLAT_ITEM
                        )
                        else -> throw UnsupportedOperationException(annotation::class.simpleName)
                    }
                }
            }
        }
    }
}
