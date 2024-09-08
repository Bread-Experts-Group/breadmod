package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockModel
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.world.level.ItemLike


/**
 * An annotation-based model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see DataGenerateBlockModel
 * @see DataGenerateBlockAndItemModel
 * @see DataGenerateItemModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartModelProviderFabric(
    modID: String, private val forClassLoader: ClassLoader, private val forPackage: Package
) : SmartBlockModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates model definition files according to [DataGenerateBlockModel], [DataGenerateBlockAndItemModel], and
     * [DataGenerateItemModel] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {
        val associateItemModelProvider = object : SmartItemModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
            override fun generate(forEvent: FabricDataGenerator.Pack) = throw UnsupportedOperationException("!")
            fun expose() = getItemModelMap()
        }

        forEvent.addProvider { dataOutput, _ ->
            object : FabricModelProvider(dataOutput) {
                override fun generateBlockStateModels(p0: BlockModelGenerators) = getBlockModelMap().forEach { (value, annotation) ->
                    when (annotation) {
                        is DataGenerateBlockModel, is DataGenerateBlockAndItemModel -> p0.createGenericCube(value)
                        else -> throw UnsupportedOperationException(annotation::class.simpleName)
                    }
                }

                override fun generateItemModels(p0: ItemModelGenerators) =
                    mutableMapOf<ItemLike, Annotation>().also {
                        it.putAll(getBlockModelMap())
                        it.putAll(associateItemModelProvider.expose())
                    }.forEach { (value, annotation) ->
                        when (annotation) {
                            is DataGenerateBlockModel,
                            is DataGenerateBlockAndItemModel,
                            is DataGenerateItemModel -> p0.generateFlatItem(
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
