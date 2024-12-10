package org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid_item

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class SingleFluidItemRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractTestItemRecipeBlockEntity<BMRecipeInputs.SingleFluidItem, SingleFluidItemRecipe>(
    pos,
    state,
    ModBlockEntityTypes.SINGLE_FLUID_ITEM_TEST.get(),
    ModRecipeTypes.SINGLE_FLUID_ITEM.get(),
    2
) {
    val tank: CustomFluidTank by lazy {
        object : CustomFluidTank(10000, 2) {
            override fun onContentsChanged() {
                syncToClients()
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("fluid", CompoundTag().also { tank.writeToNBT(registries, it) })
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        tank.readFromNBT(registries, tag.getCompound("fluid"))
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!activeRecipe.inputStillValid(items[0], tank.getFluid(0))) resetRecipe()
            val recipeTime = activeRecipe.rTime ?: 0
            progress++
            if (progress >= recipeTime) {
                finalizeRecipe(activeRecipe, level)
                resetRecipe()
            }
        }, {
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.SingleFluidItem(
                    items[0],
                    items[0].count,
                    tank.getFluid(0),
                    tank.getFluid(0).amount, 1
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                val recipeTime = recipe.rTime ?: 0
                if (!recipe.canFitResults(tank, 1, items, 1)) return@ifPresent
                currentRecipe = Optional.of(recipe)
                maxProgress = recipeTime
            }
        })
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        SingleFluidItemRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("SingleFluidItemRecipe")

    override fun getWidth(): Int = 1

    override fun getHeight(): Int = 1

    override fun finalizeRecipe(recipe: SingleFluidItemRecipe, level: Level) {
        val assemble = recipe.assembleOutputs(
            BMRecipeInputs.SingleFluidItem(
                items[0],
                items[0].count,
                tank.getFluid(0),
                tank.getFluid(0).amount, 1
            )
        )
        if (itemSlots[1].isEmpty) itemSlots[1] =
            assemble.second.copyWithCount(recipe.rItemOutput.count) else itemSlots[1].grow(recipe.rItemOutput.count)
        if (tank.getFluid(1).isEmpty) tank.setFluidInTank(
            1,
            assemble.first.copyWithAmount(recipe.rFluidOutput.amount)
        ) else
            tank.getFluid(1).grow(recipe.rFluidOutput.amount)
        recipe.consumeInputs(tank, 0, items, 0)
    }
}