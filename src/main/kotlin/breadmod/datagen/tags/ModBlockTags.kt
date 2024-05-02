package breadmod.datagen.tags

import breadmod.BreadMod
import breadmod.registry.block.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : BlockTagsProvider(output, lookupProvider, BreadMod.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(ModBlocks.BREAD_BLOCK.get().block)
            .add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get().block)
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.CHARCOAL_BLOCK.get().block)
            .add(ModBlocks.REINFORCED_BREAD_BLOCK.get().block)
            .add(ModBlocks.HEATING_ELEMENT_BLOCK.get().block)
            .add(ModBlocks.BAUXITE_ORE.get().block)
            .add(ModBlocks.MONITOR.get().block)
        tag(BlockTags.STONE_ORE_REPLACEABLES)
            .add(ModBlocks.BREAD_BLOCK.get().block)
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(ModBlocks.FLOUR_BLOCK.get().block)
            .add(ModBlocks.FLOUR_LAYER_BLOCK.get().block)
        tag(BlockTags.BEACON_BASE_BLOCKS)
            .add(ModBlocks.REINFORCED_BREAD_BLOCK.get().block)
        tag(BlockTags.create(ResourceLocation("forge", "storage_blocks")))
            .add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get().block)
            .add(ModBlocks.CHARCOAL_BLOCK.get().block)
            .add(ModBlocks.BREAD_BLOCK.get().block)
    }
}