package breadmod.registry.block

import breadmod.BreadMod
import breadmod.block.*
//import breadmod.item.util.BlockStateStack
import breadmod.registry.item.ModItems
import breadmod.registry.item.RegisterSpecialCreativeTab
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
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
import net.minecraftforge.common.extensions.IForgeItem
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlocks {
    val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)
    fun getLocation(block: Block) = ForgeRegistries.BLOCKS.getKey(block)

    private fun registerBlockItem(id: String, block: () -> Block, properties: Item.Properties): RegistryObject<BlockItem> =
        deferredRegister.register(id, block).let { ModItems.deferredRegister.register(id) { BlockItem(it.get(), properties) } }
    private fun registerBlockItem(id: String, block: () -> Block, item: (block: Block) -> BlockItem): RegistryObject<BlockItem> =
        deferredRegister.register(id, block).let { ModItems.deferredRegister.register(id) { item(it.get()) } }

    val BREAD_BLOCK = registerBlockItem(
        "bread_block",
        { BreadBlock() },
        Item.Properties().also {
            val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null)!!
            it.food(
                FoodProperties.Builder()
                    .nutrition(breadFoodStats.nutrition * 9)
                    .saturationMod(breadFoodStats.saturationModifier * 9)
                    .build()
            )
        }
    )

    val REINFORCED_BREAD_BLOCK = registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(1f).sound(SoundType.NETHERITE_BLOCK)) },
        Item.Properties().fireResistant()
    )

    val MONITOR = registerBlockItem(
        "monitor",
        { MonitorBlock() },
        Item.Properties()
    )
    
    val KEYBOARD = registerBlockItem(
        "keyboard",
        { KeyboardBlock() },
        Item.Properties().stacksTo(1)
    )

    val CHARCOAL_BLOCK = registerBlockItem(
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 9
        } }
    )
    val LOW_DENSITY_CHARCOAL_BLOCK = registerBlockItem(
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 4
        } }
    )

    val DOUGH_MACHINE_BLOCK = registerBlockItem(
        "dough_machine",
        { DoughMachineBlock() },
        Item.Properties()
    )

    val HEATING_ELEMENT_BLOCK = registerBlockItem(
        "heating_element",
        { HeatingElementBlock() },
        Item.Properties()
    )

    val HAPPY_BLOCK = registerBlockItem(
        "happy_block",
        { HappyBlock() },
        Item.Properties().stacksTo(1)
    )

    val FLOUR_BLOCK = registerBlockItem(
        "flour_block",
        { object : FallingBlock(Properties.of().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.SNOW)) {
            override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean = true
            override fun getFlammability(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 100
            override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 150
        } },
        Item.Properties()
    )

    val FLOUR_LAYER_BLOCK = registerBlockItem(
        "flour_layer",
        { FlourLayeredBlock() },
        Item.Properties()
    )

    val BAUXITE_ORE = registerBlockItem(
        "bauxite_ore",
        { OreBlock() },
        { block -> object : BlockItem(block, Properties()), RegisterSpecialCreativeTab {
            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ) {
                //pOutput.accept(BlockStateStack(block.defaultBlockState().setValue(OreBlock.ORE_TYPE, OreBlock.Companion.OreTypes.STONE)))
                //pOutput.accept(BlockStateStack(block.defaultBlockState().setValue(OreBlock.ORE_TYPE, OreBlock.Companion.OreTypes.BREAD)))
            }
        } }
    )

    class ModBlockLoot : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {
        override fun getKnownBlocks(): Iterable<Block> {
            return Iterable<Block> {
                deferredRegister.entries
                    .stream()
                    .flatMap<Block> { obj: RegistryObject<Block?> -> obj.stream() }
                    .iterator()
            }
        }

        override fun generate() {
            dropSelf(BREAD_BLOCK.get().block)
            dropSelf(REINFORCED_BREAD_BLOCK.get().block)
            dropSelf(CHARCOAL_BLOCK.get().block)
            dropSelf(LOW_DENSITY_CHARCOAL_BLOCK.get().block)
            dropSelf(HAPPY_BLOCK.get().block)
            dropSelf(HEATING_ELEMENT_BLOCK.get().block)
            dropSelf(BAUXITE_ORE.get().block)
            dropSelf(MONITOR.get().block)
            dropSelf(DOUGH_MACHINE_BLOCK.get().block)
            dropSelf(KEYBOARD.get().block)

            add(
                FLOUR_BLOCK.get().block,
                createSingleItemTableWithSilkTouch(
                    FLOUR_BLOCK.get().block,
                    ModItems.FLOUR.get(), ConstantValue.exactly(4F)
                )
            )
            // TODO: All stolen from snow loot table. Remake in the future?
            add(FLOUR_LAYER_BLOCK.get().block, LootTable.lootTable().withPool(
                LootPool.lootPool().`when`(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                    .add(
                        AlternativesEntry.alternatives(AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
                            LootItem.lootTableItem(ModItems.FLOUR.get()).`when`(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block).setProperties(
                                    StatePropertiesPredicate.Builder.properties().hasProperty(
                                        SnowLayerBlock.LAYERS, pValue
                                    )
                                )
                            ).apply(SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2)))
                        }.`when`(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.possibleValues) { pValue: Int ->
                            (if (pValue == 8)
                                LootItem.lootTableItem(FLOUR_BLOCK.get().block)
                            else LootItem.lootTableItem(ModItems.FLOUR.get()).apply(
                                SetItemCountFunction.setCount(ConstantValue.exactly(pValue.toFloat() / 2))
                            ).`when`(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(FLOUR_LAYER_BLOCK.get().block).setProperties(
                                    StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, pValue)
                                )
                            )) as LootPoolEntryContainer.Builder<*>
                        })
                    )
            ))
        }
    }
}