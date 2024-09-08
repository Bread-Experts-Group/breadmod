package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockModel
import bread.mod.breadmod.datagen.model.block.DataGenerateCustomModel
import bread.mod.breadmod.datagen.model.block.ModelType
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
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
                        when (annotation) {
                            is DataGenerateBlockModel -> simpleBlock(block)
                            is DataGenerateBlockAndItemModel -> simpleBlockWithItem(block, cubeAll(block))
                            is DataGenerateCustomModel -> {
                                // todo this is really bad and cursed...
                                val correctedPath = modLocation(block.name.string.replace("block.breadmod.", "block/"))
                                val id = block.name.string.replace("block.breadmod.", "")
                                if (annotation.type == ModelType.HORIZONTAL) {
                                    horizontalBlock(block) {
                                        return@horizontalBlock models().singleTexture(
                                            correctedPath.toString(),
                                            modLoc("${ModelProvider.BLOCK_FOLDER}/$id"),
                                            modLoc("${ModelProvider.BLOCK_FOLDER}/$id")
                                        )
                                    }
                                    simpleBlockItem(
                                        block,
                                        models().getBuilder(correctedPath.toString())
                                    )
                                }
                            }
                            else -> throw UnsupportedOperationException(annotation.toString())
                        }
                    }
                }
            )
        }
    }
}
