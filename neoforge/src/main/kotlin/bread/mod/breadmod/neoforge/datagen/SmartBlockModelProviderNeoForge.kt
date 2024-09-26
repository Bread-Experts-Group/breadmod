package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.model.block.DataGenerateButtonBlock
import bread.mod.breadmod.datagen.model.block.DataGenerateLayerBlock
import bread.mod.breadmod.datagen.model.block.Orientable
import bread.mod.breadmod.datagen.model.block.SmartBlockModelProvider
import bread.mod.breadmod.datagen.model.block.orientable.DataGenerateOrientableBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.orientable.DataGenerateOrientableBlockModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.singleTexture.DataGenerateSingleTextureBlockModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockModel
import net.minecraft.core.Direction
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import kotlin.reflect.jvm.javaField


// todo important, support blockbench models to only generate items and *not* models
//  (they'll cause a parent loop on client run and not load at all)
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

                        if (data.first is DataGenerateButtonBlock) {
                            val button = actual as ButtonBlock
                            val buttonModel =
                                ModelFile.ExistingModelFile(
                                    modLoc("${ModelProvider.BLOCK_FOLDER}/${register.id.path}"),
                                    forEvent.existingFileHelper
                                )
                            val buttonPressedModel =
                                ModelFile.ExistingModelFile(
                                    modLoc("${ModelProvider.BLOCK_FOLDER}/${register.id.path}_pressed"),
                                    forEvent.existingFileHelper
                                )

                            getVariantBuilder(button).forAllStates { state: BlockState ->
                                val facing = state.getValue(ButtonBlock.FACING)
                                val face = state.getValue(ButtonBlock.FACE)
                                val powered = state.getValue(ButtonBlock.POWERED)
                                ConfiguredModel.builder()
                                    .modelFile(if (powered) buttonPressedModel else buttonModel)
                                    .rotationX(if (face == AttachFace.FLOOR) 0 else if (face == AttachFace.WALL) 90 else 180)
                                    .rotationY(
                                        (if (face == AttachFace.CEILING) facing else facing.opposite).toYRot().toInt()
                                    )
                                    .build()
                            }
                            simpleBlockItem(actual, models().getBuilder(location.toString()))
                        } else if (data.first is DataGenerateLayerBlock) {
                            getVariantBuilder(actual).forAllStates { state ->
                                val layer = state.getValue(BlockStateProperties.LAYERS)
                                ConfiguredModel.builder()
                                    .modelFile(
                                        models().getBuilder("${location}_${layer}")
                                            .parent(
                                                models().withExistingParent(
                                                    location.toString(),
                                                    mcLoc("${ModelProvider.BLOCK_FOLDER}/thin_block")
                                                )
                                            )
                                            .texture("texture", location)
                                            .texture("particle", location)
                                            .element()
                                            .from(0F, 0F, 0F)
                                            .to(16F, 2F * layer, 16F)
                                            .allFaces { d, u ->
                                                u.uvs(0F, if (d.axis.isVertical) 0F else 16F - (2 * layer), 16F, 16F)
                                                u.texture("#texture")
                                                if (d != Direction.UP) u.cullface(d)
                                            }
                                            .end()
                                    )
                                    .build()
                            }
                            simpleBlockItem(actual, models().getBuilder("${location}_1"))
                        } else {
                            val model = when (data.first) {
                                is DataGenerateCubeAllBlockModel, is DataGenerateCubeAllBlockAndItemModel ->
                                    cubeAll(actual)

                                is DataGenerateSingleTextureBlockModel, is DataGenerateSingleTextureBlockAndItemModel ->
                                    models().singleTexture(location.path, location, location)

                                is DataGenerateWithExistingParentBlockModel, is DataGenerateWithExistingParentBlockAndItemModel ->
                                    ModelFile.ExistingModelFile(location, forEvent.existingFileHelper)

                                is DataGenerateOrientableBlockModel ->
                                    models().orientable(
                                        location.path,
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockModel).side),
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockModel).front),
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockModel).top)
                                    )

                                is DataGenerateOrientableBlockAndItemModel ->
                                    models().orientable(
                                        location.path,
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockAndItemModel).side),
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockAndItemModel).front),
                                        modLoc(location.path + (data.first as DataGenerateOrientableBlockAndItemModel).top)
                                    )

                                else -> throw UnsupportedOperationException(data.first.toString())
                            }

                            val orientable = data.second.javaField!!.getAnnotation(Orientable::class.java)
                            if (orientable != null) {
                                when (orientable.type) {
                                    Orientable.Type.HORIZONTAL -> horizontalBlock(actual, model)
                                    Orientable.Type.ALL_AXIS -> directionalBlock(actual, model)
                                }
                            } else simpleBlock(actual, model)

                            if (data.first.annotationClass.qualifiedName!!.endsWith("AndItemModel")) {
                                simpleBlockItem(actual, model)
                            }
                        }
                    }
                }
            )
        }
    }
}
