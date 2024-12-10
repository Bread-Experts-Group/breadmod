package org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.BreadMod.Companion.LOGGER
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class MultiFluidRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractTestRecipeBlockEntity<BMRecipeInputs.MultiFluid, MultiFluidTestRecipe>(
    pos,
    state,
    ModBlockEntityTypes.MULTI_FLUID_TEST.get(),
    ModRecipeTypes.MULTI_FLUID.get()
) {
    val tank: CustomFluidTank by lazy {
        object : CustomFluidTank(10000, 4) {
            override fun onContentsChanged() {
                syncToClients()
            }
        }
    }

//    val sidedTest: SidedFluidTank by lazy {
//        SidedFluidTank(
//            arrayListOf(
//                SidedFluidTank.CustomHandler(10000) { syncToClients() },
//                SidedFluidTank.CustomHandler(
//                    10000,
//                    { it.`is`(ModFluids.BREAD_LIQUID.source.get()) }) { syncToClients() },
//                SidedFluidTank.CustomHandler(5000) { syncToClients() },
//                SidedFluidTank.CustomHandler(5000) { syncToClients() }
//            )
//        )
//    }

    private fun getFluid(tank: Int): FluidStack = this.tank.getFluidInTank(tank)

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            val inputList = listOf(getFluid(0), getFluid(1))
            if (!activeRecipe.inputsStillValid(inputList)) resetRecipe()
            if (activeRecipe.canFitResults(listOf(getFluid(2), getFluid(3)), tank.capacity)) {
                val recipeTime = activeRecipe.rTime ?: 0
                progress++
                if (progress >= recipeTime) {
                    finalizeRecipe(activeRecipe, level)
                    resetRecipe()
                }
            }
        }, {
            val inputList = listOf(getFluid(0), getFluid(1))
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.MultiFluid(
                    inputList,
                    buildList { inputList.forEach { add(it.amount) } },
                    0
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                maxProgress = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)
            }
        })
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        MultiFluidRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("MultiFluidTestRecipe")

    override fun finalizeRecipe(recipe: MultiFluidTestRecipe, level: Level) {
        val inputList = listOf(getFluid(0), getFluid(1))
        val assemble = recipe.assembleFluids(
            BMRecipeInputs.MultiFluid(
                inputList.filter { !it.isEmpty },
                buildList { inputList.filter { it.amount != 0 }.forEach { add(it.amount) } },
                0
            )
        )
        // index 0 for multi-item/fluid recipes should always exist. If it doesn't, then something seriously went wrong...
        if (getFluid(2).isEmpty) {
            tank.setFluidInTank(2, assemble[0].copyWithAmount(recipe.rFluidOutputs[0].amount))
        } else getFluid(2).amount += recipe.rFluidOutputs[0].amount
        try {
            if (getFluid(3).isEmpty) {
                tank.setFluidInTank(3, assemble[1].copyWithAmount(recipe.rFluidOutputs[1].amount))
            } else getFluid(3).amount += recipe.rFluidOutputs[1].amount
        } catch (e: Exception) {
            LOGGER.error(e)
        }
        recipe.consumeInputs(inputList)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("fluid", CompoundTag().also { tank.writeToNBT(registries, it) })
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        tank.readFromNBT(registries, tag.getCompound("fluid"))
    }
}