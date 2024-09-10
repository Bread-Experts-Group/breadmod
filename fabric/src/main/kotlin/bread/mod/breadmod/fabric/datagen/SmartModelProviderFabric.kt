package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockModel
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block


/**
 * An annotation-based model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see bread.mod.breadmod.datagen.model
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartModelProviderFabric(
    modID: String, private val forClassLoader: ClassLoader, private val forPackage: Package
) : SmartBlockModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates model definition files according to [bread.mod.breadmod.datagen.model] annotation use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {
        val associateItemModelProvider =
            object : SmartItemModelProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
                override fun generate(forEvent: FabricDataGenerator.Pack) = throw UnsupportedOperationException("!")
                fun expose() = getItemModelMap()
            }

        forEvent.addProvider { dataOutput, _ ->
            object : FabricModelProvider(dataOutput) {
                override fun generateBlockStateModels(p0: BlockModelGenerators) =
                    getBlockModelMap().forEach { (value, annotation) ->
                        when (annotation) {
                            is DataGenerateCubeAllBlockModel, is DataGenerateCubeAllBlockAndItemModel -> {
                                val mapping = TextureMapping().put(
                                    TextureSlot.ALL,
                                    ModelLocationUtils.getModelLocation(value)
                                )
                                p0.blockStateOutput.accept(
                                    BlockModelGenerators.createSimpleBlock(
                                        value,
                                        ModelTemplates.CUBE_ALL.create(value, mapping, p0.modelOutput)
                                    )
                                )
                            }

                            else -> throw UnsupportedOperationException(annotation::class.simpleName)
                        }
                    }

                override fun generateItemModels(p0: ItemModelGenerators) =
                    mutableMapOf<ItemLike, Annotation>().also {
                        it.putAll(getBlockModelMap())
                        it.putAll(associateItemModelProvider.expose())
                    }.forEach { (value, annotation) ->
                        when (annotation) {
                            is DataGenerateCubeAllBlockModel,
                            is DataGenerateCubeAllBlockAndItemModel -> p0.output.accept(
                                ModelLocationUtils.getModelLocation(value.asItem()),
                                DelegatedModel(ModelLocationUtils.getModelLocation(value as Block))
                            )

                            else -> throw UnsupportedOperationException(annotation::class.simpleName)
                        }
                    }
            }
        }
    }
}
