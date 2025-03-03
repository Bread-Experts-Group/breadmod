package org.bread_experts_group.breadmod.datagen

import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.FLOUR_BLOCK
import org.bread_experts_group.breadmod.registry.block.ModBlocks.FLOUR_LAYER_BLOCK
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.concurrent.CompletableFuture

class ModBlockLootProvider(
	lookupProvider: CompletableFuture<HolderLookup.Provider>
) : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags(), lookupProvider.get()) {
	override fun getKnownBlocks(): MutableIterable<Block> = object : MutableIterable<Block> {
		override fun iterator(): MutableIterator<Block> {
			return ModBlocks.BLOCK_REGISTRY.entries
				.stream()
				.flatMap { obj -> obj.asOptional().stream() }
				.iterator()
		}
	}

	override fun generate() {
		this.dropSelf(ModBlocks.BREAD_BLOCK.asBlock())
		this.dropSelf(ModBlocks.REINFORCED_BREAD_BLOCK.asBlock())
		this.dropSelf(ModBlocks.MONITOR.asBlock())
		this.dropSelf(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.asBlock())
		this.dropSelf(ModBlocks.HAPPY_BLOCK.asBlock())
		this.dropSelf(ModBlocks.CHARCOAL_BLOCK.asBlock())
		this.dropSelf(ModBlocks.KEYBOARD.asBlock())
		this.dropSelf(ModBlocks.HELL_NAW_BUTTON.asBlock())
		this.dropSelf(ModBlocks.WAR_TERMINAL.asBlock())
		this.dropSelf(ModBlocks.RANDOM_SOUND_BLOCK.asBlock())
		this.dropSelf(ModBlocks.SOUND_BLOCK.asBlock())
		this.dropSelf(ModBlocks.WHEAT_CRUSHER.asBlock())
		this.dropSelf(ModBlocks.DOUGH_MACHINE.asBlock())
		this.dropSelf(ModBlocks.MULTI_ITEM_TEST.asBlock())
		this.dropSelf(ModBlocks.SINGLE_ITEM_TEST.asBlock())
		this.dropSelf(ModBlocks.SINGLE_FLUID_TEST.asBlock())
		this.dropSelf(ModBlocks.MULTI_FLUID_TEST.asBlock())
		this.dropSelf(ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock())
		this.dropSelf(ModBlocks.NIKO_BLOCK.asBlock())
		this.dropSelf(ModBlocks.OMANEKO_BLOCK.asBlock())
		this.dropSelf(ModBlocks.RICARD_BLOCK.asBlock())
		this.dropSelf(ModBlocks.UNFUNNYLAD_BLOCK.asBlock())
		this.dropSelf(ModBlocks.BREAD_FENCE.asBlock())
		this.dropSelf(ModBlocks.BREAD_DOOR.asBlock())
		this.dropSelf(ModBlocks.FLUID_ENERGY.asBlock())
		this.dropSelf(ModBlocks.TOASTER.asBlock())
		this.dropSelf(ModBlocks.MICROWAVE.asBlock())
		this.dropSelf(ModBlocks.ENERGY_STORAGE.asBlock())
		this.add(ModBlocks.ITEM_IN_WORLD_BLOCK.asBlock(), noDrop())
		this.add(ModBlocks.COLORED_EMISSIVE_LIGHT_RED.asBlock(), noDrop())
		this.add(ModBlocks.COLORED_EMISSIVE_LIGHT_BLUE.asBlock(), noDrop())
		this.add(ModBlocks.COLORED_EMISSIVE_LIGHT_GREEN.asBlock(), noDrop())
		this.add(ModBlocks.JADE_FLUID_TANK.asBlock(), noDrop())

		this.add(
			FLOUR_BLOCK.asBlock(),
			this.createSingleItemTableWithSilkTouch(
				FLOUR_BLOCK.asBlock(),
				ModItems.FLOUR.get(), ConstantValue.exactly(4f)
			)
		)

		this.add(
			FLOUR_LAYER_BLOCK.get().block, LootTable.lootTable().withPool(
				LootPool.lootPool()
					.`when`(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
					.add(
						AlternativesEntry.alternatives(
							AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
								LootItem.lootTableItem(ModItems.FLOUR.get()).`when`(
									LootItemBlockStatePropertyCondition
										.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block)
										.setProperties(
											StatePropertiesPredicate.Builder.properties().hasProperty(
												SnowLayerBlock.LAYERS, pValue
											)
										)
								).apply(SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2)))
							}.`when`(this.hasSilkTouch()),
							AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
								(if (pValue == 8)
									LootItem.lootTableItem(FLOUR_BLOCK.get().block)
								else LootItem.lootTableItem(ModItems.FLOUR.get()).apply(
									SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2))
								).`when`(
									LootItemBlockStatePropertyCondition
										.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block)
										.setProperties(
											StatePropertiesPredicate.Builder.properties()
												.hasProperty(SnowLayerBlock.LAYERS, pValue)
										)
								)) as LootPoolEntryContainer.Builder<*>
							})
					)
			)
		)
	}

	companion object {
		val dropNone: MutableList<Block> = mutableListOf()
		fun constructLootProvider(
			blockLootProvider: BlockLootSubProvider,
			output: PackOutput,
			registries: CompletableFuture<HolderLookup.Provider>
		): LootTableProvider =
			LootTableProvider(
				output,
				setOf(),
				listOf(SubProviderEntry({ blockLootProvider }, LootContextParamSets.BLOCK)),
				registries
			)
	}
}