package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.loot.DataGenerateLoot
import bread.mod.breadmod.datagen.loot.SmartBlockLootProvider
import bread.mod.breadmod.datagen.loot.constructLootProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.resources.ResourceLocation
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
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.neoforged.neoforge.data.event.GatherDataEvent
import kotlin.collections.emptySet
import kotlin.collections.forEach

class SmartBlockLootProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartBlockLootProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: GatherDataEvent) {
        forEvent.generator.addProvider(
            true,
            constructLootProvider(
                BlockLootProvider(forEvent.lookupProvider.get()),
                forEvent.generator.packOutput,
                forEvent.lookupProvider
            )
        )
    }

    inner class BlockLootProvider(
        lookupProvider: HolderLookup.Provider
    ) : BlockLootSubProvider(
        emptySet<Item>(),
        FeatureFlags.DEFAULT_FLAGS,
        lookupProvider
    ) {
        override fun generate() {
            getBlockLootMap().forEach { (block, data) ->
                data.forEach {
                    val optionalItem = BuiltInRegistries.ITEM[ResourceLocation.parse(it.optionalItem)]
                    val optionalBlock = BuiltInRegistries.BLOCK[ResourceLocation.parse(it.optionalBlock)]
                    when (it.dropType) {
                        DataGenerateLoot.Type.SELF -> dropSelf(block.block)
                        DataGenerateLoot.Type.OTHER -> TODO("Not yet implemented")
                        DataGenerateLoot.Type.DOOR -> createDoorTable(block.block)
                        DataGenerateLoot.Type.SNOW_LAYER -> add(
                            block.block, LootTable.lootTable().withPool(
                                LootPool.lootPool()
                                    .`when`(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                                    .add(
                                        AlternativesEntry.alternatives(
                                            AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { value: Int ->
                                                LootItem.lootTableItem(block).`when`(
                                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(
                                                        block.block
                                                    )
                                                        .setProperties(
                                                            StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(
                                                                    SnowLayerBlock.LAYERS, value
                                                                )
                                                        )
                                                )
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(value.toFloat() / 2)))
                                            }.`when`(hasSilkTouch()),
                                            AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { value: Int ->
                                                (if (value == 8) {
                                                    LootItem.lootTableItem(optionalBlock)
                                                } else LootItem.lootTableItem(optionalItem).apply(
                                                    SetItemCountFunction.setCount(ConstantValue.exactly(value.toFloat() / 2))
                                                ).`when`(
                                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(
                                                        block.block
                                                    )
                                                        .setProperties(
                                                            StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(SnowLayerBlock.LAYERS, value)
                                                        )
                                                )) as LootPoolEntryContainer.Builder<*>
                                            }
                                        )
                                    )
                            )
                        )
                    }
                }
            }
        }

        override fun getKnownBlocks(): Iterable<Block> = BuiltInRegistries.BLOCK.stream()
            .filter { it.builtInRegistryHolder().key?.location()?.namespace == ModMainCommon.MOD_ID }
            .map { it.builtInRegistryHolder().value() }
            .toList()

    }
}