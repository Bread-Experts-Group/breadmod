package breadmodadvanced.datagen

import breadmodadvanced.ModMainAdv
import breadmodadvanced.registry.block.ModBlocksAdv
import net.minecraft.data.PackOutput
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

class ModBlockStateProviderAdv(
    output: PackOutput,
    exFileHelper: ExistingFileHelper
) : BlockStateProvider(output, ModMainAdv.ID, exFileHelper) {
    private fun getLocForBlock(name: String) = modLoc("${ModelProvider.BLOCK_FOLDER}/$name")

    override fun registerStatesAndModels() {
        getLocForBlock("diesel_generator").let {
            val abs = it.toString()
            horizontalBlock(ModBlocksAdv.DIESEL_GENERATOR.get().block) { _ ->
                models().singleTexture(abs, it, it).renderType("cutout")
            }
            simpleBlockItem(
                ModBlocksAdv.DIESEL_GENERATOR.get().block,
                models().getBuilder(abs)
            )
        }
    }
}