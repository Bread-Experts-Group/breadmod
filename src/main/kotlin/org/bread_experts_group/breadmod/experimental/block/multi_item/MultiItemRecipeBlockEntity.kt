package org.bread_experts_group.breadmod.experimental.block.multi_item

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.BreadMod.Companion.LOGGER
import org.bread_experts_group.breadmod.experimental.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class MultiItemRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : AbstractTestItemRecipeBlockEntity<BMRecipeInputs.MultiItem, MultiItemTestRecipe>(
    pos,
    state,
    ModBlockEntityTypes.MULTI_ITEM_TEST.get(),
    ModRecipeTypes.MULTI_ITEM.get(),
    4
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
                BMRecipeInputs.MultiItem(
                    items,
                    buildList { items.forEach { add(it.count) } },
                    3
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                currentRecipe = Optional.of(recipe)

                try {
                    LOGGER.info("recipe id: ${present.id.path}")
                    LOGGER.info("items: ${recipe.rItemInputs}")
                    LOGGER.info("outputs: ${recipe.rItemOutputs}")
                } catch (e: Exception) {
                    LOGGER.error(e)
                }
            }
        })
    }

    override fun finalizeRecipe(recipe: MultiItemTestRecipe, level: Level) {
        val assemble = recipe.assembleItems(
            BMRecipeInputs.MultiItem(
                items.filter { !it.isEmpty },
                buildList { items.filter { it.count != 0 }.forEach { add(it.count) } },
                3
            )
        )
        if (itemSlots[3].isEmpty) itemSlots[3] =
            assemble[0].copyWithCount(recipe.rItemOutputs[0].count) else itemSlots[3].grow(recipe.rItemOutputs[0].count)
        recipe.consumeInputs(items)
    }

    override fun getWidth(): Int = 3
    override fun getHeight(): Int = 1

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        MultiItemRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("multi item recipe test block")
}