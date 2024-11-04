package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.tag.BlockTags.MINEABLE_WITH_KNIFE
import java.util.concurrent.CompletableFuture

class ModBlockTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : BlockTagsProvider(output, lookupProvider, Breadmod.ID, existingFileHelper) {
    private fun IntrinsicTagAppender<Block>.add(vararg toAdd: DeferredItem<BlockItem>) =
        this.also { toAdd.forEach { this.add(it.get().block) } }

    override fun addTags(provider: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(ModBlocks.BREAD_BLOCK, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK)
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.REINFORCED_BREAD_BLOCK)
            .add(ModBlocks.BAUXITE_ORE)
            .add(ModBlocks.MONITOR)
        tag(BlockTags.BEACON_BASE_BLOCKS)
            .add(ModBlocks.REINFORCED_BREAD_BLOCK)
        tag(BlockTags.STONE_ORE_REPLACEABLES)
            .add(ModBlocks.BREAD_BLOCK)
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(ModBlocks.FLOUR_BLOCK, ModBlocks.FLOUR_LAYER_BLOCK)
        tag(
            BlockTags.create(
                ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/low_density_charcoal")
            )
        ).add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK)
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/charcoal")))
            .add(ModBlocks.CHARCOAL_BLOCK)
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/bread")))
            .add(ModBlocks.BREAD_BLOCK)
        tag(MINEABLE_WITH_KNIFE)
            .add(ModBlocks.BREAD_BLOCK)
    }
}