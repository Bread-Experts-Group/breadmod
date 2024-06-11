package breadmod.datagen

import breadmod.registry.block.ModBlocks
import breadmod.block.specialItem.OreBlock
import breadmod.registry.fluid.ModFluids
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

@Suppress("SpellCheckingInspection")
class ModBlockStateProvider(
    output: PackOutput,
    modID: String,
    exFileHelper: ExistingFileHelper,
) : BlockStateProvider(output, modID, exFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BREAD_BLOCK.get().block)
        blockWithItem(ModBlocks.REINFORCED_BREAD_BLOCK.get().block)
        blockWithItem(ModBlocks.CHARCOAL_BLOCK.get().block)
        blockWithItem(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get().block)
        blockWithItem(ModBlocks.HAPPY_BLOCK.get().block)
        blockWithItem(ModBlocks.FLOUR_BLOCK.get().block)

        horizontalBlock(ModBlocks.COAL_GENERATOR.get().block) {state ->
            val machineOn = if(state.getValue(BlockStateProperties.LIT)) "_on" else ""
            val name = "breadmod:block/coal_generator$machineOn"

            val model = models().singleTexture(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/coal_generator$machineOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/coal_generator$machineOn")
            )

            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.COAL_GENERATOR.get().block,
            models().getBuilder("breadmod:block/coal_generator")
        )

        doorBlockWithRenderType(ModBlocks.BREAD_DOOR.get().block as DoorBlock,
            modLoc("${ModelProvider.BLOCK_FOLDER}/bread_door_bottom"),
            modLoc("${ModelProvider.BLOCK_FOLDER}/bread_door_top"),
            "minecraft:cutout"
        )

        fenceBlockWithRenderType(ModBlocks.BREAD_FENCE.get().block as FenceBlock,
            modLoc("${ModelProvider.BLOCK_FOLDER}/bread_block"),
            "minecraft:cutout"
        )

        horizontalBlock(ModBlocks.DOUGH_MACHINE_BLOCK.get().block) { state ->
            val machineOn = if(state.getValue(BlockStateProperties.LIT)) "_on" else ""
            val name = "breadmod:block/dough_machine$machineOn"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_side" ),
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_front$machineOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_top")
            )
            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.DOUGH_MACHINE_BLOCK.get().block,
            models().getBuilder("breadmod:block/dough_machine")
        )
        horizontalBlock(ModBlocks.WHEAT_CRUSHER_BLOCK.get().block) { state ->
            val machineOn = if(state.getValue(BlockStateProperties.LIT)) "_on" else ""
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
            ModBlocks.WHEAT_CRUSHER_BLOCK.get().block,
            models().getBuilder("breadmod:block/wheat_crusher")
        )

        // Farmer Multiblock
        blockWithItem(ModBlocks.FARMER_BASE_BLOCK.get().block)
        horizontalBlock(ModBlocks.FARMER_CONTROLLER.get().block) { state ->
            val machineOn = if(state.getValue(BlockStateProperties.TRIGGERED)) "_on" else ""
            val name = "breadmod:block/farmer_controller$machineOn"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_base_block"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_controller$machineOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_base_block"),
            )
            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.FARMER_CONTROLLER.get().block,
            models().getBuilder("breadmod:block/farmer_controller")
        )

        directionalBlock(ModBlocks.FARMER_INPUT_BLOCK.get().block) { _ ->
            val name = "breadmod:block/farmer_input_block"
            return@directionalBlock models().cubeTop(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_base_block"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_input_block")
            )
        }
        simpleBlockItem(ModBlocks.FARMER_INPUT_BLOCK.get().block,
            models().getBuilder("breadmod:block/farmer_input_block"))

        directionalBlock(ModBlocks.FARMER_OUTPUT_BLOCK.get().block) { _ ->
            val name = "breadmod:block/farmer_output_block"
            return@directionalBlock models().cubeTop(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_base_block"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_output_block")
            )
        }
        simpleBlockItem(ModBlocks.FARMER_OUTPUT_BLOCK.get().block,
            models().getBuilder("breadmod:block/farmer_output_block"))

        directionalBlock(ModBlocks.GENERIC_POWER_INTERFACE.get().block) { _ ->
            val name = "breadmod:block/farmer_power_block"
            return@directionalBlock models().cubeTop(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_base_block"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/farmer_power_block")
            )
        }
        simpleBlockItem(ModBlocks.GENERIC_POWER_INTERFACE.get().block,
            models().getBuilder("breadmod:block/farmer_power_block"))
        //// // // // TODO!
        directionalBlock(ModBlocks.HEATING_ELEMENT_BLOCK.get().block) { _ ->
            return@directionalBlock models().cubeColumn(
                "breadmod:block/heating_element",
                modLoc("${ModelProvider.BLOCK_FOLDER}/heating_element_side" ),
                modLoc("${ModelProvider.BLOCK_FOLDER}/heating_element_cap"),
            ).element()
                .from(0F, 0F, 0F)
                .to(16F, 16F, 16F)
                .allFaces { d, u ->
                    u.uvs(0F, 0F, 16F, 16F)
                    u.texture(if(d.axis.isVertical) "#end" else "#side")
                    u.tintindex(0)
                }
                .end()
        }
        simpleBlockItem(
            ModBlocks.HEATING_ELEMENT_BLOCK.get().block,
            models().getBuilder("breadmod:block/heating_element")
        )
        //// // // // TODO!
        getVariantBuilder(ModBlocks.FLOUR_LAYER_BLOCK.get().block).forAllStates { state ->
            val layer = state.getValue(BlockStateProperties.LAYERS)
            ConfiguredModel.builder()
                .modelFile(
                    models().getBuilder("breadmod:block/flour_layer_${layer}")
                        .parent(models().withExistingParent(
                            ModBlocks.getLocation(ModBlocks.FLOUR_LAYER_BLOCK.get().block)!!.path,
                            mcLoc("${ModelProvider.BLOCK_FOLDER}/thin_block")
                        ))
                        .texture("texture", modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
                        .texture("particle", modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
                        .element()
                        .from(0F, 0F, 0F)
                        .to(16F, 2F * layer, 16F)
                        .allFaces { d, u ->
                            u.uvs(0F, if(d.axis.isVertical) 0F else 16F - (2 * layer), 16F, 16F)
                            u.texture("#texture")
                            if(d != Direction.UP) u.cullface(d)
                        }
                        .end()
                )
                .build()
        }

        simpleBlockItem(
            ModBlocks.FLOUR_LAYER_BLOCK.get().block,
            models().getBuilder("breadmod:block/flour_layer_1")
        )

        fun getOreModels(state: BlockState): Array<out ConfiguredModel> {
            val path = "${ModBlocks.getLocation(state.block)!!.path}_${state.getValue(OreBlock.ORE_TYPE).serializedName}"

            return models().cubeAll(
                path,
                modLoc("${ModelProvider.BLOCK_FOLDER}/$path")
            ).let {
                itemModels().getBuilder(path).parent(it)
                ConfiguredModel.builder().modelFile(it).build()
            }
        }

        getVariantBuilder(ModBlocks.BAUXITE_ORE.get().block).forAllStates { state -> getOreModels(state) }

        // // //
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
        // // //
        horizontalBlock(ModBlocks.KEYBOARD.get().block, models().getBuilder("breadmod:block/keyboard"))
        simpleBlockItem(ModBlocks.KEYBOARD.get().block, models().getBuilder("breadmod:block/keyboard"))
        // // //
        blockTexture(ModFluids.BREAD_LIQUID.block.get())
    }

    private fun blockWithItem(blockRegistryObject: Block) {
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))
    }
}