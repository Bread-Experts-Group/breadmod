package breadmod.datagen

import breadmod.block.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper

class ModBlockState(output: PackOutput?, modid: String?, exFileHelper: ExistingFileHelper?) :
    BlockStateProvider(output, modid, exFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BREAD_BLOCK)
    }

    private fun blockWithItem(blockRegistryObject: Block) {
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))
    }
}