package breadmod.datagen

import breadmod.registry.block.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

class ModBlockState(
    output: PackOutput,
    modID: String,
    exFileHelper: ExistingFileHelper
) : BlockStateProvider(output, modID, exFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BREAD_BLOCK.get().block)
        blockWithItem(ModBlocks.REINFORCED_BREAD_BLOCK.get().block)
        blockWithItem(ModBlocks.CHARCOAL_BLOCK.get().block)
        blockWithItem(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get().block)
        blockWithItem(ModBlocks.HAPPY_BLOCK.get().block)
        blockWithItem(ModBlocks.FLOUR_BLOCK.get().block)
        // TODO: Figure out how to generate snow layer like blockstates and models - Refer to snow.json under minecraft/assets/blockstates for clues

        horizontalBlock(ModBlocks.BREAD_FURNACE_BLOCK.get().block) { state ->
            val furnaceOn = if(state.getValue(BlockStateProperties.LIT)) "_on" else ""
            val name = "breadmod:block/bread_furnace$furnaceOn"

            val model = models().orientable(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/bread_furnace_side" ),
                modLoc("${ModelProvider.BLOCK_FOLDER}/bread_furnace_front$furnaceOn"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/bread_furnace_top$furnaceOn")
            )

            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocks.BREAD_FURNACE_BLOCK.get().block,
            models().getBuilder("breadmod:block/bread_furnace")
        )
        //// // // // TODO!
        directionalBlock(ModBlocks.HEATING_ELEMENT_BLOCK.get().block) { state ->
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

    }

    private fun blockWithItem(blockRegistryObject: Block) {
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))
    }
}