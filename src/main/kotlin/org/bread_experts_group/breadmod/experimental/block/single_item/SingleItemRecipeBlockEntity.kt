package org.bread_experts_group.breadmod.experimental.block.single_item

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class SingleItemRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractTestItemRecipeBlockEntity<BMRecipeInputs.SingleItem, SingleItemTestRecipe>(
    pos,
    state,
    ModBlockEntityTypes.SINGLE_ITEM_TEST.get(),
    ModRecipeTypes.SINGLE_ITEM.get(),
    2
) {
    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!activeRecipe.inputStillValid(items)) resetRecipe()
            val recipeTime = activeRecipe.rTime ?: 0
            progress++
            if (progress >= recipeTime) {
                finalizeRecipe(activeRecipe, level)
                resetRecipe()
            }
        }, {
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.SingleItem(
                    items[0],
                    items[0].count,
                    1
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                val recipeTime = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)
                maxProgress = recipeTime
            }
        })
    }

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        SingleItemRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("SingleItemRecipe")

    override fun finalizeRecipe(recipe: SingleItemTestRecipe, level: Level) {
        val assemble = recipe.assemble(BMRecipeInputs.SingleItem(items[0], items[0].count, 1), level.registryAccess())
        if (itemSlots[1].isEmpty) itemSlots[1] =
            assemble.copyWithCount(recipe.rItemOutput.count) else itemSlots[1].grow(recipe.rItemOutput.count)
        recipe.consumeInput(items)
    }
}