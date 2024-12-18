package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTank
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class FluidEnergyBlockEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractTestItemRecipeBlockEntity<FluidEnergyRecipe.FluidEnergyInput, FluidEnergyRecipeTest>(
    pos, state,
    ModBlockEntityTypes.FLUID_ENERGY.get(),
    ModRecipeTypes.FLUID_ENERGY_TEST.get(),
    8
) {
    inner class SyncedHandler(capacity: Int) : SidedFluidTank.CustomHandler(capacity) {
        override fun onContentsChanged() {
            level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
        }
    }

    // todo needs logic to disallow input fluids
    val tank = SidedFluidTank(
        listOf(10000, 10000, 10000, 10000).map { SyncedHandler(it) }
    )

    private fun getFluid(tank: Int): FluidStack = this.tank.getFluidInTank(tank)

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            val fluidInputs = listOf(getFluid(0), getFluid(1))
            if (!activeRecipe.inputStillValid(items, fluidInputs)) resetRecipe()
            if (activeRecipe.canFitResults(
                    listOf(items[4], items[5], items[6], items[7]),
                    tank,
                    10000
                )
            ) {
                val recipeTime = activeRecipe.rTime ?: 0
                progress++
                if (progress >= recipeTime) {
                    finalizeRecipe(activeRecipe, level)
                    resetRecipe()
                }
            }
        }, {
            val fluidInputs = listOf(getFluid(0), getFluid(1))
            val itemInputs = listOf(items[0], items[1], items[2], items[3])
            val check = recipeDial.getRecipeFor(
                FluidEnergyRecipe.FluidEnergyInput(
                    items,
                    buildList { itemInputs.forEach { add(it.count) } },
                    fluidInputs,
                    buildList { fluidInputs.forEach { add(it.amount) } },
                    2
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                maxProgress = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)
            }
        })
    }

    override fun finalizeRecipe(recipe: FluidEnergyRecipeTest, level: Level) {
        val inputItems = listOf(items[0], items[1], items[2], items[3])
        val inputFluids = listOf(getFluid(0), getFluid(1))
        val assemble = recipe.assembleOutputs(
            FluidEnergyRecipe.FluidEnergyInput(
                inputItems,
                buildList { inputItems.filter { it.count != 0 }.forEach { add(it.count) } },
                inputFluids,
                buildList { inputFluids.filter { it.amount != 0 }.forEach { add(it.amount) } },
                4
            )
        )
        // todo needs rewriting to actually set the output slots with the item outputs, fluid part seems to be fine?
        val outputSlots = mutableListOf(getItem(4), getItem(5), getItem(6), getItem(7))
        if (assemble.first.isNotEmpty()) repeat(assemble.first.size) { index ->
            if (outputSlots[index].isEmpty) outputSlots[index] =
                assemble.first[index].copyWithCount(recipe.rItemOutputs[index].count) else
                outputSlots[index].grow(recipe.rItemOutputs[index].count)
        }
        if (assemble.second.isNotEmpty()) repeat(assemble.second.size) { index ->
            if (tank.tanks[index].isEmpty()) tank.tanks[index].fluid =
                assemble.second[index].copyWithAmount(recipe.rFluidOutputs[index].amount) else
                tank.tanks[index].fluid.grow(recipe.rFluidOutputs[index].amount)
        }
        recipe.consumeInputs(inputItems, inputFluids)
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        FluidEnergyMenu(containerId, playerInventory, this)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tank.writeToNBT(registries, tag)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        tank.readFromNBT(registries, tag)
    }

    override fun getDisplayName(): Component = Component.literal("FluidEnergyRecipe")

    override fun getWidth(): Int = 2
    override fun getHeight(): Int = 2
}