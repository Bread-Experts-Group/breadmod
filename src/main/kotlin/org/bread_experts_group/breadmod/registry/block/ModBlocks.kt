package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeType
import org.bread_experts_group.breadmod.BreadMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.registry.block.actual.FlourLayeredBlock
import org.bread_experts_group.breadmod.registry.block.actual.HappyBlock
import org.bread_experts_group.breadmod.registry.block.actual.*
import org.bread_experts_group.breadmod.registry.block.actual.experimental.MultiItemRecipeBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.machine.WheatCrusherBlock
import org.bread_experts_group.breadmod.registry.item.ModItems.ITEM_REGISTRY

object ModBlocks {
    val BLOCK_REGISTRY = DeferredRegister.createBlocks(BreadMod.ID)

    fun getLocation(block: Block) = BuiltInRegistries.BLOCK.getKey(block)

    /**
     * Convenience function for directly getting a block from a [DeferredItem]
     */
    fun DeferredItem<BlockItem>.asBlock(): Block = this.get().block

    val BREAD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
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

    val REINFORCED_BREAD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).strength(25f, 1200f)) },
        Item.Properties().fireResistant()
    )

    val MONITOR = BLOCK_REGISTRY.registerBlockItem("monitor", { MonitorBlock() }, Item.Properties())

    val LOW_DENSITY_CHARCOAL_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)) },
        { block ->
            object : BlockItem(block, Properties()) {
                override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 4
            }
        }
    )

    val WAR_TERMINAL = BLOCK_REGISTRY.registerBlockItem(
        "war_terminal", { WarTerminalBlock() }, Item.Properties()
    )

    val RANDOM_SOUND_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "random_sound_block", { RandomSoundBlock() }, Item.Properties()
    )

    val SOUND_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "sound_block", { SoundBlock() }, Item.Properties()
    )

    val CHARCOAL_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK)) },
        { block ->
            object : BlockItem(block, Properties()) {
                override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 9
            }
        }
    )

    val WHEAT_CRUSHER = BLOCK_REGISTRY.registerBlockItem(
        "wheat_crusher",
        { WheatCrusherBlock() },
        Item.Properties()
    )

    val DOUGH_MACHINE = BLOCK_REGISTRY.registerBlockItem(
        "dough_machine",
        { DoughMachineBlock() },
        Item.Properties()
    )

    // todo port
    val BAUXITE_ORE = BLOCK_REGISTRY.registerBlockItem(
        "bauxite_ore", { Block(BlockBehaviour.Properties.of()) }, Item.Properties()
    )

    val FLOUR_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "flour_block",
        { FlourBlock() },
        Item.Properties()
    )

    val FLOUR_LAYER_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "flour_layer", { FlourLayeredBlock() }, Item.Properties()
    )

    val HAPPY_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "happy_block", { HappyBlock() }, Item.Properties()
    )

    val KEYBOARD = BLOCK_REGISTRY.registerBlockItem(
        "keyboard",
        { KeyboardBlock() },
        Item.Properties().stacksTo(1)
    )

    val HELL_NAW_BUTTON = BLOCK_REGISTRY.registerBlockItem(
        "hell_naw_button",
        { HellNawButtonBlock() },
        Item.Properties()
    )

    val MULTI_ITEM_TEST = BLOCK_REGISTRY.registerBlockItem(
        "multi_item_recipe",
        { MultiItemRecipeBlock() },
        Item.Properties()
    )

    fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
    ): DeferredItem<BlockItem> = this.register(id, block).let { supplier ->
        ITEM_REGISTRY.register(id) { -> BlockItem(supplier.get(), properties) }
    }

    fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        item: (block: Block) -> BlockItem
    ): DeferredItem<BlockItem> = this.register(id, block).let { supplier ->
        ITEM_REGISTRY.register(id) { -> item(supplier.get()) }
    }
}
