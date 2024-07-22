package breadmod.registry.block

import breadmod.ModMain
import breadmod.block.*
import breadmod.block.machine.*
import breadmod.block.machine.multiblock.farmer.FarmerControllerBlock
import breadmod.block.machine.multiblock.farmer.FarmerInputBlock
import breadmod.block.machine.multiblock.farmer.FarmerOutputBlock
import breadmod.block.machine.multiblock.generic.PowerInterfaceBlock
import breadmod.block.storage.EnergyStorageBlock
import breadmod.block.specialItem.OreBlock
import breadmod.block.specialItem.UseBlockStateNBT
import breadmod.item.renderer.CreativeGeneratorItemRenderer
import breadmod.registry.item.ModItems
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.menu.ModCreativeTabs
import breadmod.util.registerBlockItem
import com.google.common.collect.ImmutableMap
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
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
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.common.extensions.IForgeItem
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer

object ModBlocks {
    internal val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.ID)
    fun getLocation(block: Block) = ForgeRegistries.BLOCKS.getKey(block)

    val BREAD_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "bread_block",
        { BreadBlock() },
        Item.Properties().also {
            val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null) ?: throw IllegalStateException("Bread has no food properties???")
            it.food(
                FoodProperties.Builder()
                    .nutrition(breadFoodStats.nutrition * 9)
                    .saturationMod(breadFoodStats.saturationModifier * 9)
                    .build()
            )
        }
    )

    val REINFORCED_BREAD_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(25.0f, 1200.0f)) },
        Item.Properties().fireResistant()
    )

    val MONITOR = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "monitor",
        { MonitorBlock() },
        Item.Properties()
    )
    
    val KEYBOARD = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "keyboard",
        { KeyboardBlock() },
        Item.Properties().stacksTo(1)
    )

    val CHARCOAL_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 9
        } }
    )
    val LOW_DENSITY_CHARCOAL_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 4
        } }
    )

    val DOUGH_MACHINE_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "dough_machine",
        { DoughMachineBlock() },
        Item.Properties()
    )

    val WHEAT_CRUSHER_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "wheat_crusher",
        { WheatCrusherBlock() },
        Item.Properties()
    )

    val GENERATOR = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "generator",
        { GeneratorBlock() },
        Item.Properties()
    )

    val CREATIVE_GENERATOR = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "creative_generator",
        { CreativeGeneratorBlock() },
        { block -> object : BlockItem(block, Properties()) {
            override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions{
                override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = CreativeGeneratorItemRenderer()
            })
        }}
    )

    // the silly
    val NIKO_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "niko_block",
        { object : Block(Properties.of().noOcclusion()) {
            override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
                pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
            }

            override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState = defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection)

            @Deprecated("Deprecated in Java", ReplaceWith(
                "super.getShape(pState, pLevel, pPos, pContext)",
                "net.minecraft.world.level.block.Block"
            ))
            override fun getShape(
                pState: BlockState,
                pLevel: BlockGetter,
                pPos: BlockPos,
                pContext: CollisionContext
            ): VoxelShape = when(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction.NORTH, Direction.SOUTH -> box(0.0, 0.0, 3.0, 16.0, 20.0, 13.0)
                else -> box(3.0, 0.0, 0.0, 13.0, 20.0, 16.0)
            }
        }},
        Item.Properties().rarity(Rarity.EPIC)
    )

    // the sillyier
    val OMANEKO_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "omaneko_block",
        { object : Block(Properties.of().noOcclusion()) {
            override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
                pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
            }

            override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState = defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection)

            @Deprecated("Deprecated in Java", ReplaceWith(
                "super.getShape(pState, pLevel, pPos, pContext)",
                "net.minecraft.world.level.block.Block"
            ))
            override fun getShape(
                pState: BlockState,
                pLevel: BlockGetter,
                pPos: BlockPos,
                pContext: CollisionContext
            ): VoxelShape = when(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction.NORTH, Direction.SOUTH -> box(0.0, 0.0, 3.0, 16.0, 20.0, 13.0)
                else -> box(3.0, 0.0, 0.0, 13.0, 20.0, 16.0)
            }
        }},
        Item.Properties().rarity(Rarity.EPIC)
    )

    // Storage Blocks
    val ENERGY_STORAGE_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "energy_storage",
        { EnergyStorageBlock() },
        Item.Properties()
    )

    // Farmer Multiblock
    val FARMER_CONTROLLER = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "farmer_controller",
        { FarmerControllerBlock() },
        Item.Properties()
    )
    val FARMER_BASE_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "farmer_base_block",
        { Block(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)) },
        Item.Properties()
    )
    val FARMER_INPUT_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "farmer_input_block",
        { FarmerInputBlock() },
        Item.Properties()
    )
    val FARMER_OUTPUT_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "farmer_output_block",
        { FarmerOutputBlock() },
        Item.Properties()
    )
    val GENERIC_POWER_INTERFACE = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "generic_power_interface",
        { PowerInterfaceBlock() },
        Item.Properties()
    )

    ////
    val HEATING_ELEMENT_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "heating_element",
        { HeatingElementBlock() },
        Item.Properties()
    )

    val HAPPY_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "happy_block",
        { HappyBlock() },
        Item.Properties().stacksTo(1)
    )

    val HELL_NAW = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "hell_naw",
        { HellNawButtonBlock() },
        Item.Properties()
    )

    val FLOUR_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "flour_block",
        { object : FallingBlock(Properties.of().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.SNOW)) {
            override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean = true
            override fun getFlammability(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 100
            override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 150
        } },
        Item.Properties()
    )

    val FLOUR_LAYER_BLOCK = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "flour_layer",
        { FlourLayeredBlock() },
        Item.Properties()
    )

    val BREAD_FENCE = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "bread_fence",
        { object : FenceBlock(Properties.of()
            .forceSolidOn()
            .sound(SoundType.GRASS)
            .strength(1.0F)
        ) {
            override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 100
            override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean = true
        } },
        Item.Properties()
    )

    val BREAD_DOOR = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "bread_door",
        { object : DoorBlock(Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(1.0F)
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion(),
            ModBlockSetTypes.BREAD) { //todo create item texture
            override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean = true
        } },
        Item.Properties()
    )

    val TOASTER = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "toaster",
        { ToasterBlock() },
        Item.Properties()
    )

    val BAUXITE_ORE = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "bauxite_ore",
        { OreBlock() },
        { block -> object : BlockItem(block, Properties()), IRegisterSpecialCreativeTab {
            override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.MAIN_TAB)

            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ): Boolean {
                OreBlock.Companion.OreTypes.entries.forEach { type ->
                    pOutput.accept(ItemStack(block).also {
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        UseBlockStateNBT.saveState(it.orCreateTag, BlockState(block, ImmutableMap.copyOf(mapOf(OreBlock.ORE_TYPE to type)), null))
                    })
                }
                return false
            }
        } }
    )

    internal class ModBlockLoot : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {
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
            dropSelf(BREAD_FENCE.get().block)
            dropSelf(WHEAT_CRUSHER_BLOCK.get().block)
            dropSelf(GENERATOR.get().block)
            dropSelf(FARMER_CONTROLLER.get().block)
            dropSelf(FARMER_BASE_BLOCK.get().block)
            dropSelf(FARMER_INPUT_BLOCK.get().block)
            dropSelf(FARMER_OUTPUT_BLOCK.get().block)
            dropSelf(GENERIC_POWER_INTERFACE.get().block)
            dropSelf(HELL_NAW.get().block)
            dropSelf(CREATIVE_GENERATOR.get().block)
            dropSelf(TOASTER.get().block)
            dropSelf(NIKO_BLOCK.get().block)
            dropSelf(OMANEKO_BLOCK.get().block)
            dropSelf(ENERGY_STORAGE_BLOCK.get().block)
            add(BREAD_DOOR.get().block, createDoorTable(BREAD_DOOR.get().block))
            // NOTICE: The below uses what I'd consider a hack (see: ModFluids.kt), but it works.
            dropNone.forEach { add(it, noDrop()) }

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

        companion object {
            val dropNone = mutableListOf<Block>()
        }
    }
}