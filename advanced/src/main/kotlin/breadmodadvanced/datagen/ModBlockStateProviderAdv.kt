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
    override fun registerStatesAndModels() {
        horizontalBlock(ModBlocksAdv.DIESEL_GENERATOR.get().block) {
            val name = "breadmodadv:block/diesel_generator"
            val model = models().singleTexture(
                name,
                modLoc("${ModelProvider.BLOCK_FOLDER}/diesel_generator"),
                modLoc("${ModelProvider.BLOCK_FOLDER}/diesel_generator")
            )

            return@horizontalBlock model
        }
        simpleBlockItem(
            ModBlocksAdv.DIESEL_GENERATOR.get().block,
            models().getBuilder("breadmodadv:block/diesel_generator")
        )
    }
}