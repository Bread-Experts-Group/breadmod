package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import breadmod.block.ModBlocks.BREAD_BLOCK
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper

class BlockStates(
    generator: DataGenerator,
    fileHelper: ExistingFileHelper,
) : BlockStateProvider(generator, BreadMod.ID, fileHelper) {
    private fun simpleBlockAndItem(block: Block) = cubeAll(block).let {
        simpleBlock(block, it)
        simpleBlockItem(block, it)
    }

    override fun registerStatesAndModels() {
        simpleBlockAndItem(BREAD_BLOCK)
    }

}