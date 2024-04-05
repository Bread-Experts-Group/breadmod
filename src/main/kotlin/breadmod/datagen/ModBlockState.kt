package breadmod.datagen

import breadmod.block.registry.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Block
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

        horizontalBlock(ModBlocks.BREAD_FURNACE_BLOCK.get().block) { state ->
            val furnaceOn = if(state.getValue(AbstractFurnaceBlock.LIT)) "_on" else ""
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
    }

    private fun blockWithItem(blockRegistryObject: Block) {
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))
    }
}