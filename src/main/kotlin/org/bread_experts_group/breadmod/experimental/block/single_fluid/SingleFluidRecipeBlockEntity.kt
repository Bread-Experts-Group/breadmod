package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.util.CustomFluidTank
import java.util.*

class SingleFluidRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractTestRecipeBlockEntity<BMRecipeInputs.SingleFluid, SingleFluidTestRecipe>(
    pos,
    state,
    ModBlockEntityTypes.SINGLE_FLUID_TEST.get(),
    ModRecipeTypes.SINGLE_FLUID.get()
) {
    // todo needs a custom FluidTank impl to allow setting specific tanks
    val tank: CustomFluidTank by lazy {
        object : CustomFluidTank(10000, 2) {
            override fun onContentsChanged() {
                syncToClients()
            }
        }
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!activeRecipe.inputStillValid(tank.getFluidInTank(0))) resetRecipe()
            if (activeRecipe.canFitResults(tank, 1)) {
                val recipeTime = activeRecipe.rTime ?: 0
                progress++
                if (progress >= recipeTime) {
                    finalizeRecipe(activeRecipe, level)
                    resetRecipe()
                }
            }
        }, {
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.SingleFluid(
                    tank.getFluidInTank(0),
                    tank.getFluidInTank(0).amount,
                    0
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                if (!recipe.canFitResults(tank, 1)) return@ifPresent
                currentRecipe = Optional.of(recipe)
            }
        })
    }

    override fun finalizeRecipe(recipe: SingleFluidTestRecipe, level: Level) {
        val assemble = recipe.assembleFluid(
            BMRecipeInputs.SingleFluid(
                tank.getFluidInTank(0),
                tank.getFluidInTank(0).amount,
                0
            )
        )
        if (tank.getFluidInTank(1).isEmpty) tank.setFluidInTank(
            1,
            assemble.copyWithAmount(recipe.rFluidOutput.amount)
        ) else tank.getFluidInTank(1).amount += recipe.rFluidOutput.amount
        recipe.consumeInput(tank, 0)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("fluid", CompoundTag().also { tank.writeToNBT(registries, it) })
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        tank.readFromNBT(registries, tag.getCompound("fluid"))
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        SingleFluidRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("SingleFluidRecipe")

}