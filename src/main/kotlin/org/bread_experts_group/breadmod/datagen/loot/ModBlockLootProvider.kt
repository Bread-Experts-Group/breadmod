package org.bread_experts_group.breadmod.datagen.loot

import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.datagen.getBlock
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.util.block
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import java.util.concurrent.CompletableFuture

class ModBlockLootProvider(
	lookupProvider: CompletableFuture<HolderLookup.Provider>
) : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags(), lookupProvider.get()) {
	private val registryScanner: LibraryScanner = Registry::class.java.`package`.getScanner()

	override fun getKnownBlocks(): Iterable<Block> = ModBlocks.blockIterator()
		.asSequence()
		.map { it.get() }
		.asIterable()

	override fun generate() {
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateLootDropSelf>().forEach { (_, data) ->
			this.dropSelf(data.getBlock("Block loot generation (drop self)"))
		}
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateLootDropNothing>().forEach { (_, data) ->
			this.dropOther(data.getBlock("Block loot generation (drop nothing)"), Blocks.AIR)
		}
		val breadDoor = ModBlocks.BREAD_DOOR.block
		this.add(breadDoor, this.createDoorTable(breadDoor))
//		val doubleOrNothing = ModBlocks.DOUBLE_OR_NOTHING.block
//		this.add(
//			doubleOrNothing,
//			this.createSinglePropConditionTable(
//				doubleOrNothing,
//				DoubleOrNothingBlock.Companion.TRIPLE_HALF,
//				ModBlockStateProperties.TripleBlockHalf.LOWER
//			)
//		)
		this.add(
			ModBlocks.FLOUR_BLOCK.block,
			this.createSingleItemTableWithSilkTouch(
				ModBlocks.FLOUR_BLOCK.block,
				ModItems.FLOUR.get(), ConstantValue.exactly(4f)
			)
		)
		this.add(
			ModBlocks.ENERGY_STORAGE.block,
			LootTable.lootTable()
				.withPool(
					this.applyExplosionCondition(
						ModBlocks.ENERGY_STORAGE.block,
						LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1f))
							.add(
								LootItem.lootTableItem(ModBlocks.ENERGY_STORAGE.asItem())
									.apply(
										CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
											.include(ModDataComponents.ENERGY.get())
											.include(ModDataComponents.ENERGY_CAPACITY.get())
									)
							)
					)
				)
		)
		this.add(
			ModBlocks.FLOUR_LAYER_BLOCK.get().block, LootTable.lootTable().withPool(
				LootPool.lootPool()
					.`when`(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
					.add(
						AlternativesEntry.alternatives(
							AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
								LootItem.lootTableItem(ModItems.FLOUR.get()).`when`(
									LootItemBlockStatePropertyCondition
										.hasBlockStateProperties(ModBlocks.FLOUR_LAYER_BLOCK.get().block)
										.setProperties(
											StatePropertiesPredicate.Builder.properties().hasProperty(
												SnowLayerBlock.LAYERS, pValue
											)
										)
								).apply(SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2)))
							}.`when`(this.hasSilkTouch()),
							AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
								(if (pValue == 8)
									LootItem.lootTableItem(ModBlocks.FLOUR_BLOCK.get().block)
								else LootItem.lootTableItem(ModItems.FLOUR.get()).apply(
									SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2))
								).`when`(
									LootItemBlockStatePropertyCondition
										.hasBlockStateProperties(ModBlocks.FLOUR_LAYER_BLOCK.get().block)
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

	fun construct(output: PackOutput, registries: CompletableFuture<Provider>): LootTableProvider =
		LootTableProvider(
			output,
			setOf(),
			listOf(LootTableProvider.SubProviderEntry({ this }, LootContextParamSets.BLOCK)),
			registries
		)
}