package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.model.block.Orientable
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockModel
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import kotlin.reflect.jvm.javaField


/**
 * An annotation-based block model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see bread.mod.breadmod.datagen.model.block
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartBlockModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartBlockModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
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
                object : BlockStateProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    @Suppress("ReplaceNotNullAssertionWithElvisReturn")
                    override fun registerStatesAndModels() = getBlockModelMap().forEach { (register, data) ->
                        val actual = when (val rx = register.get()) {
                            is Block -> rx
                            is BlockItem -> rx.block
                            else -> throw IllegalArgumentException(
                                String.format(
                                    "%s must be of type %s or %s.",
                                    data.second.name, Block::class.qualifiedName, BlockItem::class.qualifiedName
                                )
                            )
                        }

                        val location = modLoc("${ModelProvider.BLOCK_FOLDER}/${register.id.path}")

                        val model = when (data.first) {
                            is DataGenerateCubeAllBlockModel, is DataGenerateCubeAllBlockAndItemModel -> cubeAll(actual)

                            is DataGenerateSingleTextureBlockModel, is DataGenerateSingleTextureBlockAndItemModel ->
                                models().singleTexture(location.path, location, location)

                            is DataGenerateWithExistingParentBlockModel, is DataGenerateWithExistingParentBlockAndItemModel ->
                                models().withExistingParent(location.path, location)

                            else -> throw UnsupportedOperationException(data.first.toString())
                        }

                        val orientable = data.second.javaField!!.getAnnotation(Orientable::class.java)
                        if (orientable != null) {
                            when (orientable.type) {
                                Orientable.Type.HORIZONTAL -> horizontalBlock(actual, model)
                                Orientable.Type.ALL_AXIS -> directionalBlock(actual, model)
                            }
                        } else simpleBlock(actual, model)

                        if (data.first.annotationClass.qualifiedName!!.endsWith("AndItemModel")) simpleBlockItem(actual, model)
                    }
                }
            )
        }
    }
}
