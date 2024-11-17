package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock

class ModBlockStateProvider(
    packOutput: PackOutput,
    private val existingFileHelper: ExistingFileHelper
) : BlockStateProvider(packOutput, BreadMod.ID, existingFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BREAD_BLOCK.asBlock())
        blockWithItem(ModBlocks.REINFORCED_BREAD_BLOCK.asBlock())
        blockWithItem(ModBlocks.FLOUR_BLOCK.asBlock())
        blockWithItem(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.asBlock())
        blockWithItem(ModBlocks.HAPPY_BLOCK.asBlock())
        blockWithItem(ModBlocks.CHARCOAL_BLOCK.asBlock())
        blockWithItem(ModBlocks.RANDOM_SOUND_BLOCK.asBlock())

        directionalBlock(ModBlocks.MONITOR.get().block) {
            val name = "breadmod:block/monitor"

            val model = models().cube(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_face"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side")
            )

            return@directionalBlock model
        }
        simpleBlockItem(
            ModBlocks.MONITOR.get().block,
            models().getBuilder("breadmod:block/monitor")
        )

        horizontalBlock(ModBlocks.SOUND_BLOCK.asBlock()) {
            val name = "breadmod:block/sound_block"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block_side"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block_side")
            )

            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.SOUND_BLOCK.asBlock(),
            models().getBuilder("breadmod:block/sound_block")
        )

        getVariantBuilder(ModBlocks.FLOUR_LAYER_BLOCK.get().block).forAllStates { state ->
            val layer = state.getValue(BlockStateProperties.LAYERS)
            ConfiguredModel.builder()
                .modelFile(
                    models().getBuilder("breadmod:block/flour_layer_${layer}")
                        .parent(
                            models().withExistingParent(
                                ModBlocks.getLocation(ModBlocks.FLOUR_LAYER_BLOCK.get().block).path,
                                mcLoc("${ModelProvider.BLOCK_FOLDER}/thin_block")
                            )
                        )
                        .texture("texture", modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
                        .texture("particle", modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
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

        horizontalBlock(ModBlocks.WHEAT_CRUSHER.asBlock()) { state ->
            val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
            val name = "breadmod:block/wheat_crusher$machineOn"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/wheat_crusher_side"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/wheat_crusher_front$machineOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/wheat_crusher_top")
            )
            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.WHEAT_CRUSHER.asBlock(),
            models().getBuilder("breadmod:block/wheat_crusher")
        )

        horizontalBlock(ModBlocks.DOUGH_MACHINE.asBlock()) { state ->
            val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
            val name = "breadmod:block/dough_machine$machineOn"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_side"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_front$machineOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_top")
            )
            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.DOUGH_MACHINE.asBlock(),
            models().getBuilder("breadmod:block/dough_machine")
        )

        simpleBlockItem(
            ModBlocks.FLOUR_LAYER_BLOCK.get().block,
            models().getBuilder("breadmod:block/flour_layer_1")
        )

        horizontalBlock(ModBlocks.KEYBOARD.asBlock(), blockBenchModel("keyboard"))
        simpleBlockItem(ModBlocks.KEYBOARD.asBlock(), blockBenchModel("keyboard"))

        horizontalBlock(ModBlocks.WAR_TERMINAL.asBlock(), blockBenchModel("war_terminal"))
        simpleBlockItem(ModBlocks.WAR_TERMINAL.asBlock(), blockBenchModel("war_terminal"))

        // Hell Naw button
        getVariantBuilder(ModBlocks.HELL_NAW_BUTTON.asBlock() as ButtonBlock).forAllStates { state: BlockState ->
            val facing = state.getValue(ButtonBlock.FACING)
            val face = state.getValue(ButtonBlock.FACE)
            val powered = state.getValue(ButtonBlock.POWERED)
            ConfiguredModel.builder()
                .modelFile(if (powered) blockBenchModel("hell_naw_button_pressed") else blockBenchModel("hell_naw_button"))
                .rotationX(if (face == AttachFace.FLOOR) 0 else if (face == AttachFace.WALL) 90 else 180)
                .rotationY((if (face == AttachFace.CEILING) facing else facing.opposite).toYRot().toInt())
                .build()
        }

        simpleBlockItem(
            ModBlocks.HELL_NAW_BUTTON.get().block,
            models().getBuilder("breadmod:block/hell_naw_button")
        )
    }

    private fun addBlockItemWithSelfParent(block: Block) {
        val parent = modLoc("${ModelProvider.BLOCK_FOLDER}/${block.descriptionId.substringAfterLast('.')}")
        horizontalBlock(block) { models().withExistingParent(parent.toString(), parent) }
        simpleBlockItem(block, models().getBuilder(parent.toString()))
    }

    private fun blockWithItem(blockRegistryObject: Block) {
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))
    }

    private fun blockBenchModel(model: String): ModelFile.ExistingModelFile =
        ModelFile.ExistingModelFile(modLoc("${ModelProvider.BLOCK_FOLDER}/$model"), existingFileHelper)
}