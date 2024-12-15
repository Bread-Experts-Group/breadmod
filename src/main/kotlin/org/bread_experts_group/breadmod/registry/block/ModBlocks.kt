package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.api.ILightColored
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_fluid.MultiFluidRecipeBlock
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_item.MultiItemRecipeBlock
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid.SingleFluidRecipeBlock
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid_item.SingleFluidItemRecipeBlock
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_item.SingleItemRecipeBlock
import org.bread_experts_group.breadmod.registry.block.actual.*
import org.bread_experts_group.breadmod.registry.block.actual.machine.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.WheatCrusherBlock
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockSetTypes
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems.ITEM_REGISTRY
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.awt.Color
import java.util.function.Supplier

object ModBlocks {
    val BLOCK_REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(BreadMod.ID)

    fun getLocation(block: Block): ResourceLocation = BuiltInRegistries.BLOCK.getKey(block)

    /**
     * Convenience function for directly getting a block from a [DeferredItem]
     */
    fun DeferredItem<BlockItem>.asBlock(): Block = this.get().block

    @DataGenerateLanguage("en_us")
    val BREAD_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() }, Properties().also {
            val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null)
                ?: throw IllegalArgumentException("Bread has no food properties?")
            it.food(
                FoodProperties.Builder()
                    .nutrition(breadFoodStats.nutrition * 9)
                    .saturationModifier(breadFoodStats.saturation * 9)
                    .build()
            )
        }
    )

    @DataGenerateLanguage("en_us")
    val REINFORCED_BREAD_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).strength(25f, 1200f)) },
        Properties().fireResistant()
    )

    @DataGenerateLanguage("en_us")
    val MONITOR: DeferredItem<BlockItem> =
        BLOCK_REGISTRY.registerBlockItem("monitor", { MonitorBlock() }, Properties())

    @DataGenerateLanguage("en_us", "Low-Density Charcoal Block")
    val LOW_DENSITY_CHARCOAL_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)) },
        { block ->
            object : BlockItem(block, Properties()) {
                override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 4
            }
        }
    )

    @DataGenerateLanguage("en_us")
    @DataGenerateLanguage("en_us", "Prolongs the inevitable.", "tooltip")
    val WAR_TERMINAL: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "war_terminal", { WarTerminalBlock() }, Properties()
    )

    @DataGenerateLanguage("en_us")
    @DataGenerateLanguage("en_us", "Uses the power of a die to make random noises.", "tooltip")
    val RANDOM_SOUND_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "random_sound_block", { RandomSoundBlock() }, Properties()
    )

    @DataGenerateLanguage("en_us")
    val SOUND_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "sound_block", { SoundBlock() }, Properties()
    )

    @DataGenerateLanguage("en_us")
    val CHARCOAL_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK)) },
        { block ->
            object : BlockItem(block, Properties()) {
                override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 9
            }
        }
    )

    @DataGenerateLanguage("en_us")
    val WHEAT_CRUSHER: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "wheat_crusher",
        { WheatCrusherBlock() },
        Properties()
    )

    @DataGenerateLanguage("en_us")
    val DOUGH_MACHINE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "dough_machine",
        { DoughMachineBlock() },
        Properties()
    )

    // todo port
    @DataGenerateLanguage("en_us")
    val BAUXITE_ORE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bauxite_ore", { Block(BlockBehaviour.Properties.of()) }, Properties()
    )

    @DataGenerateLanguage("en_us")
    val FLOUR_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_block",
        { FlourBlock() },
        Properties()
    )

    @DataGenerateLanguage("en_us", "Flour")
    val FLOUR_LAYER_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_layer", { FlourLayeredBlock() }, Properties()
    )

    @DataGenerateLanguage("en_us")
    val HAPPY_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "happy_block", { HappyBlock() }, Properties()
    )

    @DataGenerateLanguage("en_us")
    val KEYBOARD: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "keyboard",
        { KeyboardBlock() },
        Properties().stacksTo(1)
    )

    @DataGenerateLanguage("en_us")
    val HELL_NAW_BUTTON: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "hell_naw_button",
        { HellNawButtonBlock() },
        Properties()
    )

    @DataGenerateLanguage("en_us", "NIKO TENSHOT")
    val NIKO_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "niko_block",
        { CharacterModelBlock() },
        Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateLanguage("en_us", "OMANEKO")
    val OMANEKO_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "omaneko_block",
        { CharacterModelBlock() },
        Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateLanguage("en_us", "Ricard")
    val RICARD_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "ricard_block",
        { CharacterModelBlock() },
        Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateLanguage("en_us", "Unfunnylad")
    val UNFUNNYLAD_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "unfunnylad_block",
        { CharacterModelBlock() },
        Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateLanguage("en_us")
    val BREAD_FENCE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_fence",
        {
            object : FenceBlock(
                Properties.of()
                    .forceSolidOn()
                    .sound(SoundType.GRASS)
                    .strength(1.0F)
            ) {
                override fun getFireSpreadSpeed(
                    state: BlockState,
                    level: BlockGetter,
                    pos: BlockPos,
                    direction: Direction
                ): Int = 100

                override fun isFlammable(
                    state: BlockState,
                    level: BlockGetter,
                    pos: BlockPos,
                    direction: Direction
                ): Boolean = true
            }
        },
        Properties()
    )

    @DataGenerateLanguage("en_us")
    val BREAD_DOOR = BLOCK_REGISTRY.registerBlockItem(
        "bread_door",
        {
            object : DoorBlock(
                ModBlockSetTypes.BREAD,
                Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0F)
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()
            ) { //todo create item texture
                override fun isFlammable(
                    state: BlockState,
                    level: BlockGetter,
                    pos: BlockPos,
                    direction: Direction
                ): Boolean = true
            }
        },
        Properties()
    )

    private fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Properties
    ): DeferredItem<BlockItem> = this.register(id, block).let { supplier ->
        ITEM_REGISTRY.register(id) { -> BlockItem(supplier.get(), properties) }
    }

    private fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        item: (block: Block) -> BlockItem
    ): DeferredItem<BlockItem> = this.register(id, block).let { supplier ->
        ITEM_REGISTRY.register(id) { -> item(supplier.get()) }
    }

    private fun itemWithCreativeTab(
        block: Block,
        properties: Properties,
        creativeTabs: List<Supplier<CreativeModeTab>>
    ): BlockItem = run {
        object : BlockItem(block, properties), IRegisterSpecialCreativeTab {
            override val creativeModeTabs: List<Supplier<CreativeModeTab>> = creativeTabs
        }
    }

    // EXPERIMENTAL PAST THIS POINT

    @DataGenerateLanguage("en_us", "EXPERIMENTAL RECIPE TEST BLOCK (MULTI)")
    val MULTI_ITEM_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "multi_item_recipe",
        { MultiItemRecipeBlock() },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL RECIPE TEST BLOCK (MULTI, FLUID)")
    val MULTI_FLUID_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "multi_fluid_recipe",
        { MultiFluidRecipeBlock() },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL RECIPE TEST BLOCK (SINGLE)")
    val SINGLE_ITEM_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "single_item_recipe",
        { SingleItemRecipeBlock() },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL MACHINE TEST BLOCK (SINGLE, FLUID)")
    val SINGLE_FLUID_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "single_fluid_recipe",
        { SingleFluidRecipeBlock() },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL MACHINE TEST BLOCK(SINGLE, FLUID/ITEM")
    val SINGLE_FLUID_ITEM_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "single_fluid_item_recipe",
        { SingleFluidItemRecipeBlock() },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL COLORED EMISSIVE LIGHT, RED")
    val COLORED_EMISSIVE_LIGHT_RED: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "colored_emissive_light_red",
        {
            object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
                override fun getLightColor(level: BlockAndTintGetter, blockState: BlockState, position: BlockPos): Int =
                    Color.RED.rgb
            }
        },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL COLORED EMISSIVE LIGHT, GREEN")
    val COLORED_EMISSIVE_LIGHT_GREEN: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "colored_emissive_light_green",
        {
            object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
                override fun getLightColor(level: BlockAndTintGetter, blockState: BlockState, position: BlockPos): Int =
                    Color.GREEN.rgb
            }
        },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )

    @DataGenerateLanguage("en_us", "EXPERIMENTAL COLORED EMISSIVE LIGHT, BLUE")
    val COLORED_EMISSIVE_LIGHT_BLUE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "colored_emissive_light_blue",
        {
            object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
                override fun getLightColor(level: BlockAndTintGetter, blockState: BlockState, position: BlockPos): Int =
                    Color.BLUE.rgb
            }
        },
        { block -> itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
    )
}
