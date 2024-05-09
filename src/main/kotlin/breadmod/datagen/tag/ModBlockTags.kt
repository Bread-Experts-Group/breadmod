package breadmod.datagen.tag

import breadmod.ModMain
import breadmod.registry.block.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject
import java.util.concurrent.CompletableFuture

class ModBlockTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : BlockTagsProvider(output, lookupProvider, ModMain.ID, existingFileHelper) {
    private fun IntrinsicTagAppender<Block>.add(vararg toAdd: RegistryObject<BlockItem>) =
        this.also { toAdd.forEach { this.add(it.get().block) } }

    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(ModBlocks.BREAD_BLOCK, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK)
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.CHARCOAL_BLOCK,
                ModBlocks.REINFORCED_BREAD_BLOCK,
                ModBlocks.HEATING_ELEMENT_BLOCK,
                ModBlocks.BAUXITE_ORE,
                ModBlocks.MONITOR)
        tag(BlockTags.STONE_ORE_REPLACEABLES)
            .add(ModBlocks.BREAD_BLOCK)
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(ModBlocks.FLOUR_BLOCK, ModBlocks.FLOUR_LAYER_BLOCK)
        tag(BlockTags.BEACON_BASE_BLOCKS)
            .add(ModBlocks.REINFORCED_BREAD_BLOCK)
        tag(BlockTags.create(ResourceLocation("forge", "storage_blocks")))
            .add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK,
                ModBlocks.CHARCOAL_BLOCK,
                ModBlocks.BREAD_BLOCK)
    }
}