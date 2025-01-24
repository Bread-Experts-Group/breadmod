package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.references.Blocks
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import java.util.concurrent.CompletableFuture

class ModBlockTags(
	output: PackOutput,
	lookupProvider: CompletableFuture<HolderLookup.Provider>,
	existingFileHelper: ExistingFileHelper,
) : BlockTagsProvider(output, lookupProvider, BreadMod.ID, existingFileHelper) {
	private fun IntrinsicTagAppender<Block>.add(vararg toAdd: DeferredItem<BlockItem>) =
		this.also { toAdd.forEach { this.add(it.get().block) } }

	override fun addTags(provider: HolderLookup.Provider) {
		this.tag(BlockTags.MINEABLE_WITH_HOE)
			.add(ModBlocks.BREAD_BLOCK, ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK)
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(ModBlocks.REINFORCED_BREAD_BLOCK)
			.add(ModBlocks.MONITOR)
		this.tag(BlockTags.BEACON_BASE_BLOCKS)
			.add(ModBlocks.REINFORCED_BREAD_BLOCK)
		this.tag(BlockTags.STONE_ORE_REPLACEABLES)
			.add(ModBlocks.BREAD_BLOCK)
		this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
			.add(ModBlocks.FLOUR_BLOCK, ModBlocks.FLOUR_LAYER_BLOCK)
		this.tag(
			BlockTags.create(
				ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/low_density_charcoal")
			)
		).add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK)
		this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/charcoal")))
			.add(ModBlocks.CHARCOAL_BLOCK)
		this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/bread")))
			.add(ModBlocks.BREAD_BLOCK)
		this.tag(Companion.MINEABLE_WITH_KNIFE)
			.add(ModBlocks.BREAD_BLOCK)
			.add(Blocks.PUMPKIN)
		this.tag(BlockTags.FENCES)
			.add(ModBlocks.BREAD_FENCE)
	}

	companion object {
		@DataGenerateLanguage("en_us", prefix = "tag.block.")
		val MINEABLE_WITH_KNIFE: TagKey<Block> =
			TagKey.create(Registries.BLOCK, modLocation("mineable/knife"))

		@DataGenerateLanguage("en_us", prefix = "tag.block.")
		val INCORRECT_FOR_BREAD_TOOL: TagKey<Block> =
			TagKey.create(Registries.BLOCK, modLocation("incorrect_for_bread_tool"))

		@DataGenerateLanguage("en_us", prefix = "tag.block.")
		val INCORRECT_FOR_REINFORCED_BREAD_TOOL: TagKey<Block> =
			TagKey.create(Registries.BLOCK, modLocation("incorrect_for_reinforced_bread_tool"))
	}
}