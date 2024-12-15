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
        dropSelf(ModBlocks.BREAD_BLOCK.asBlock())
        dropSelf(ModBlocks.REINFORCED_BREAD_BLOCK.asBlock())
        dropSelf(ModBlocks.MONITOR.asBlock())
        dropSelf(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.asBlock())
        dropSelf(ModBlocks.BAUXITE_ORE.asBlock())
        dropSelf(ModBlocks.HAPPY_BLOCK.asBlock())
        dropSelf(ModBlocks.CHARCOAL_BLOCK.asBlock())
        dropSelf(ModBlocks.KEYBOARD.asBlock())
        dropSelf(ModBlocks.HELL_NAW_BUTTON.asBlock())
        dropSelf(ModBlocks.WAR_TERMINAL.asBlock())
        dropSelf(ModBlocks.RANDOM_SOUND_BLOCK.asBlock())
        dropSelf(ModBlocks.SOUND_BLOCK.asBlock())
        dropSelf(ModBlocks.WHEAT_CRUSHER.asBlock())
        dropSelf(ModBlocks.DOUGH_MACHINE.asBlock())
        dropSelf(ModBlocks.MULTI_ITEM_TEST.asBlock())
        dropSelf(ModBlocks.SINGLE_ITEM_TEST.asBlock())
        dropSelf(ModBlocks.SINGLE_FLUID_TEST.asBlock())
        dropSelf(ModBlocks.MULTI_FLUID_TEST.asBlock())
        dropSelf(ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock())
        dropSelf(ModBlocks.NIKO_BLOCK.asBlock())
        dropSelf(ModBlocks.OMANEKO_BLOCK.asBlock())
        dropSelf(ModBlocks.RICARD_BLOCK.asBlock())
        dropSelf(ModBlocks.UNFUNNYLAD_BLOCK.asBlock())
        dropSelf(ModBlocks.BREAD_FENCE.asBlock())
        dropSelf(ModBlocks.BREAD_DOOR.asBlock())
        add(ModBlocks.COLORED_EMISSIVE_LIGHT_RED.asBlock(), noDrop())
        add(ModBlocks.COLORED_EMISSIVE_LIGHT_BLUE.asBlock(), noDrop())
        add(ModBlocks.COLORED_EMISSIVE_LIGHT_GREEN.asBlock(), noDrop())

        add(
            FLOUR_BLOCK.asBlock(),
            createSingleItemTableWithSilkTouch(
                FLOUR_BLOCK.asBlock(),
                ModItems.FLOUR.get(), ConstantValue.exactly(4f)
            )
        )

        add(
            FLOUR_LAYER_BLOCK.get().block, LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .`when`(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                    .add(
                        AlternativesEntry.alternatives(
                            AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
                                LootItem.lootTableItem(ModItems.FLOUR.get()).`when`(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block)
                                        .setProperties(
                                            StatePropertiesPredicate.Builder.properties().hasProperty(
                                                SnowLayerBlock.LAYERS, pValue
                                            )
                                        )
                                ).apply(SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2)))
                            }.`when`(hasSilkTouch()),
                            AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
                                (if (pValue == 8)
                                    LootItem.lootTableItem(FLOUR_BLOCK.get().block)
                                else LootItem.lootTableItem(ModItems.FLOUR.get()).apply(
                                    SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2))
                                ).`when`(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block)
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