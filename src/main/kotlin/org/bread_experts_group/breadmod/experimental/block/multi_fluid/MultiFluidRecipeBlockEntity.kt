package org.bread_experts_group.breadmod.experimental.block.multi_fluid

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.util.CustomFluidTank

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

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        TODO("Not yet implemented")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Component {
        TODO("Not yet implemented")
    }

    override fun finalizeRecipe(recipe: MultiFluidTestRecipe, level: Level) {
        TODO("Not yet implemented")
    }
}