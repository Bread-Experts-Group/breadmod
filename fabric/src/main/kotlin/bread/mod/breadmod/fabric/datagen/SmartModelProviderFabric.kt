package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.model.block.DataGenerateButtonBlock
import bread.mod.breadmod.datagen.model.block.DataGenerateLayerBlock
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import bread.mod.breadmod.datagen.model.block.orientable.DataGenerateOrientableBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockModel
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import dev.architectury.registry.registries.RegistrySupplier
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.*
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import kotlin.reflect.KProperty1


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

        // todo update for new annotations
        forEvent.addProvider { dataOutput, _ ->
            object : FabricModelProvider(dataOutput) {
                override fun generateBlockStateModels(p0: BlockModelGenerators) =
                    getBlockModelMap().forEach { (register, data) ->
                        val actual = register.get() as BlockItem
                        when (data.first) {
                            is DataGenerateCubeAllBlockModel, is DataGenerateCubeAllBlockAndItemModel -> {
                                val mapping = TextureMapping().put(
                                    TextureSlot.ALL,
                                    ModelLocationUtils.getModelLocation(actual.block)
                                )
                                p0.blockStateOutput.accept(
                                    BlockModelGenerators.createSimpleBlock(
                                        actual.block,
                                        ModelTemplates.CUBE_ALL.create(actual.block, mapping, p0.modelOutput)
                                    )
                                )
                            }

                            // TODO: .OBJ support for fabric
                            is DataGenerateWithExistingParentBlockModel, is DataGenerateWithExistingParentBlockAndItemModel,
                            is DataGenerateSingleTextureBlockModel, is DataGenerateSingleTextureBlockAndItemModel -> {
                                p0.blockStateOutput.accept(
                                    BlockModelGenerators.createSimpleBlock(
                                        actual.block,
                                        ModelLocationUtils.getModelLocation(actual.block)
                                    )
                                )
                            }

                            is DataGenerateButtonBlock -> {
                                p0.blockStateOutput.accept(
                                    BlockModelGenerators.createButton(
                                        actual.block,
                                        ModelLocationUtils.getModelLocation(actual.block),
                                        ModelLocationUtils.getModelLocation(actual.block, "_pressed")
                                    )
                                )
                            }

                            // todo do this later
                            is DataGenerateLayerBlock -> null

                            // todo do this later
                            is DataGenerateOrientableBlockAndItemModel -> null

                            else -> throw UnsupportedOperationException(data.first.annotationClass.qualifiedName)
                        }
                    }

                override fun generateItemModels(p0: ItemModelGenerators) =
                    mutableMapOf<RegistrySupplier<*>, Pair<Annotation, KProperty1<*, *>>>().also {
                        it.putAll(getBlockModelMap())
                        it.putAll(associateItemModelProvider.expose())
                    }.forEach { (register, data) ->
                        @Suppress("UNUSED_EXPRESSION")
                        when (val annotation = data.first) {
                            is DataGenerateCubeAllBlockAndItemModel,
                            is DataGenerateWithExistingParentBlockAndItemModel,
                            is DataGenerateSingleTextureBlockAndItemModel -> {
                                val actual = register.get() as BlockItem
                                p0.output.accept(
                                    ModelLocationUtils.getModelLocation(actual),
                                    DelegatedModel(ModelLocationUtils.getModelLocation(actual.block))
                                )
                            }

                            is DataGenerateItemModel -> {
                                val actual = register.get() as Item
                                when (annotation.type) {
                                    DataGenerateItemModel.Type.DOUBLE_LAYERED -> ModelTemplates.TWO_LAYERED_ITEM.create(
                                        ModelLocationUtils.getModelLocation(actual),
                                        TextureMapping.layered(
                                            TextureMapping.getItemTexture(actual),
                                            TextureMapping.getItemTexture(actual).withSuffix("_overlay")
                                        ),
                                        p0.output
                                    )

                                    DataGenerateItemModel.Type.HANDHELD -> p0.generateFlatItem(
                                        actual,
                                        ModelTemplates.FLAT_HANDHELD_ITEM
                                    )

                                    else -> p0.generateFlatItem(actual, ModelTemplates.FLAT_ITEM)
                                }
                            }

                            is DataGenerateCubeAllBlockModel, is DataGenerateWithExistingParentBlockModel,
                            is DataGenerateSingleTextureBlockModel, is DataGenerateOrientableBlockAndItemModel,
                            is DataGenerateButtonBlock, is DataGenerateLayerBlock -> null

                            else -> throw UnsupportedOperationException(data.first.annotationClass.qualifiedName)
                        }
                    }
            }
        }
    }
}
