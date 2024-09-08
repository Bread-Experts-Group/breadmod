package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.DataGenerateCustomModel
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockModel
import bread.mod.breadmod.datagen.model.block.ModelType
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based block model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see DataGenerateBlockModel
 * @see DataGenerateBlockAndItemModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartBlockModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartBlockModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates block model definition files according to [DataGenerateBlockModel] and [DataGenerateBlockAndItemModel]
     * use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            forEvent.generator.addProvider(
                true,
                object : BlockStateProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    override fun registerStatesAndModels() = getBlockModelMap().forEach { (block, annotation) ->
                        fun addBlockItemWithSelfParent(block: Block) {
                            val parent = modLoc("${ModelProvider.BLOCK_FOLDER}/${block.descriptionId.substringAfterLast('.')}")
                            horizontalBlock(block) { models().withExistingParent(parent.toString(), parent) }
                            simpleBlockItem(block, models().getBuilder(parent.toString()))
                        }

                        when (annotation) {
                            is DataGenerateBlockModel -> simpleBlock(block)
                            is DataGenerateBlockAndItemModel -> simpleBlockWithItem(block, cubeAll(block))
                            // todo this needs to be replicated to fabric as well (unfortunately)
                            // todo separate into a single texture or multiple texture, but selectable model type (ex: horizontal, directional, orientable)
                            is DataGenerateCustomModel -> if (!annotation.existingParent) {
                                val parent = modLoc("${ModelProvider.BLOCK_FOLDER}/${block.descriptionId.substringAfterLast('.')}")
                                val parentName = parent.path.substringAfterLast('/')
                                when (annotation.type) {
                                    ModelType.HORIZONTAL_FACING -> {
                                        horizontalBlock(block) {
                                            return@horizontalBlock models().singleTexture(
                                                parent.toString(),
                                                modLoc("${ModelProvider.BLOCK_FOLDER}/$parentName"),
                                                modLoc("${ModelProvider.BLOCK_FOLDER}/$parentName")
                                            )
                                        }
                                        simpleBlockItem(block, models().getBuilder(parent.toString()))
                                    }
                                    ModelType.SIMPLE -> simpleBlockWithItem(block, cubeAll(block))
                                    ModelType.ORIENTABLE -> {
                                        horizontalBlock(block) {
                                            // todo figuring out side textures for blocks with same textures on all sides except front
                                            return@horizontalBlock models().orientable(
                                                parent.toString(),
                                                modLoc("${ModelProvider.BLOCK_FOLDER}/${parentName}_side"),
                                                modLoc("${ModelProvider.BLOCK_FOLDER}/${parentName}"),
                                                modLoc("${ModelProvider.BLOCK_FOLDER}/${parentName}_side")
                                            )
                                        }
                                        simpleBlockItem(block, models().getBuilder(parent.toString()))
                                    }
                                }
                            } else {
                                addBlockItemWithSelfParent(block)
                            }
                            else -> throw UnsupportedOperationException(annotation.toString())
                        }
                    }
                }
            )
        }
    }
}
