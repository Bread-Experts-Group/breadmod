package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy_recipe.FluidEnergyInput
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.AbstractTickingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe

class DoughMachineBlockEntity(
	pos : BlockPos, state : BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, DoughMachineRecipe, DoughMachineBlockEntity>(
	ModBlockEntityTypes.DOUGH_MACHINE.get(),
	pos,
	state,
	ModRecipeTypes.DOUGH_MACHINE.get(),
	3,
	100000,
	listOf(
		Triple(10000, true, false),
		Triple(10000, false, true),
	)
), MenuProvider {
	val horizontal : Direction = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
	override fun commonTick(
		clientLevel : Level,
		pos : BlockPos,
		state : BlockState,
		entity : AbstractTickingBlockEntity<*>
	) {
		super.commonTick(clientLevel, pos, state, entity)
	}

	override fun finalizeRecipe(recipe : DoughMachineRecipe, level : Level) {
		TODO("Not yet implemented")
	}

	override fun createMenu(containerId : Int, playerInventory : Inventory, player : Player) : AbstractContainerMenu =
		DoughMachineMenu(containerId, playerInventory, this)

	override fun getDisplayName() : Component = modTranslatable("block", "dough_machine")
}