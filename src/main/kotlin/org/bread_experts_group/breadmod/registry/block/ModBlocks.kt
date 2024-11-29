package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.experimental.multi_item.MultiItemRecipeBlock
import org.bread_experts_group.breadmod.registry.block.actual.*
import org.bread_experts_group.breadmod.registry.block.actual.machine.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.WheatCrusherBlock
import org.bread_experts_group.breadmod.registry.item.ModItems.ITEM_REGISTRY

object ModBlocks {
    val BLOCK_REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(BreadMod.ID)

    fun getLocation(block: Block): ResourceLocation = BuiltInRegistries.BLOCK.getKey(block)

    /**
     * Convenience function for directly getting a block from a [DeferredItem]
     */
    fun DeferredItem<BlockItem>.asBlock(): Block = this.get().block

    @DataGenerateLanguage("en_us")
    val BREAD_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() }, Item.Properties().also {
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
        Item.Properties().fireResistant()
    )

    @DataGenerateLanguage("en_us")
    val MONITOR: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem("monitor", { MonitorBlock() }, Item.Properties())

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
        "war_terminal", { WarTerminalBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    @DataGenerateLanguage("en_us", "Uses the power of a die to make random noises.", "tooltip")
    val RANDOM_SOUND_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "random_sound_block", { RandomSoundBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    val SOUND_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "sound_block", { SoundBlock() }, Item.Properties()
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
        Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    val DOUGH_MACHINE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "dough_machine",
        { DoughMachineBlock() },
        Item.Properties()
    )

    // todo port
    @DataGenerateLanguage("en_us")
    val BAUXITE_ORE: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bauxite_ore", { Block(BlockBehaviour.Properties.of()) }, Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    val FLOUR_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_block",
        { FlourBlock() },
        Item.Properties()
    )

    @DataGenerateLanguage("en_us", "Flour")
    val FLOUR_LAYER_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_layer", { FlourLayeredBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    val HAPPY_BLOCK: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "happy_block", { HappyBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us")
    val KEYBOARD: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "keyboard",
        { KeyboardBlock() },
        Item.Properties().stacksTo(1)
    )

    @DataGenerateLanguage("en_us")
    val HELL_NAW_BUTTON: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "hell_naw_button",
        { HellNawButtonBlock() },
        Item.Properties()
    )

    private fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
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

    // EXPERIMENTAL PAST THIS POINT

    @DataGenerateLanguage("en_us", "EXPERIMENTAL MACHINE TEST BLOCK")
    val MULTI_ITEM_TEST: DeferredItem<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "multi_item_recipe",
        { MultiItemRecipeBlock() },
        Item.Properties()
    )
}
