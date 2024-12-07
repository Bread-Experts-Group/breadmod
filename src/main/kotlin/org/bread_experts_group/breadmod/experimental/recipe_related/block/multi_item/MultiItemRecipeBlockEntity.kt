package org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_item

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiItemTestRecipe
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
            if (activeRecipe.canFitResults(listOf(items[3]))) {
                val recipeTime = activeRecipe.rTime ?: 0
                progress++
                if (progress >= recipeTime) {
                    finalizeRecipe(activeRecipe, level)
                    resetRecipe()
                }
            }
        }, {
            val inputList = listOf(items[0], items[1], items[2])
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.MultiItem(
                    inputList,
                    buildList { inputList.forEach { add(it.count) } },
                    3
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                maxProgress = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)

//                try {
//                    LOGGER.info("recipe id: ${present.id.path}")
//                    LOGGER.info("items: ${recipe.rItemInputs}")
//                    LOGGER.info("outputs: ${recipe.rItemOutputs}")
//                } catch (e: Exception) {
//                    LOGGER.error(e)
//                }
            }
        })
    }

    override fun finalizeRecipe(recipe: MultiItemTestRecipe, level: Level) {
        val inputList = listOf(items[0], items[1], items[2])
        val assemble = recipe.assembleItems(
            BMRecipeInputs.MultiItem(
                inputList.filter { !it.isEmpty },
                buildList { inputList.filter { it.count != 0 }.forEach { add(it.count) } },
                3
            )
        )
        if (itemSlots[3].isEmpty) itemSlots[3] =
            assemble[0].copyWithCount(recipe.rItemOutputs[0].count) else itemSlots[3].grow(recipe.rItemOutputs[0].count)
        recipe.consumeInputs(inputList)
    }

    override fun getWidth(): Int = 3
    override fun getHeight(): Int = 1

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        MultiItemRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("multi item recipe test block")
}