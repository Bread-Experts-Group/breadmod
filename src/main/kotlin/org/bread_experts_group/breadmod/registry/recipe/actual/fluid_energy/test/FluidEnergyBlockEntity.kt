package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemBearingBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

class FluidEnergyBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, FluidEnergyRecipeTest, FluidEnergyBlockEntity>(
	ModBlockEntityTypes.FLUID_ENERGY.get(),
	pos,
	state,
	ModRecipeTypes.FLUID_ENERGY_TEST.get()
), MenuProvider, ItemBearingBlockEntity, FluidBearingBlockEntity {
	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(8)
	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
		mutableListOf(
			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = false, allowOut = true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = false, allowOut = true)
		)
	)

	override fun finalizeRecipe(recipe: FluidEnergyRecipeTest, level: Level): Boolean {
		recipe.consumeItemsAndSet(this.getItemsInRange(0 .. 3), this::setItem)
		recipe.setItemsOverflow(4 .. 7, this::getItemsInRange, this::setOrGrowItem)

		recipe.consumeFluidsAndSet(this.getFluidsInRange(0 .. 1), this::setFluid)
		recipe.setFluidsOverflow(2 .. 3, this::getFluidsInRange, this::setOrGrowFluid, 10000)
		return true
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		FluidEnergyMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("Fluid Energy Recipe")

	override fun runCurrentRecipe(
		recipe: FluidEnergyRecipeTest,
		level: Level
	) {
		val fluidInputs = this.getFluidsInRange(0 .. 1)
		val itemInputs = this.getItemsInRange(0 .. 3)

		if (!recipe.inputsStillValid(itemInputs, fluidInputs)) this.resetRecipe(level)
		val recipeTime = recipe.getTime()
		if (this.progress >= recipeTime) this.finalizeAndReset(recipe, level) else this.progress++
	}

	override fun runMissingRecipe(level: Level) {
		val fluidInputs = this.getFluidsInRange(0 .. 1)
		val itemInputs = this.getItemsInRange(0 .. 3)
		val check = this.getOptionalRecipe(FluidEnergyInput(itemInputs, fluidInputs), level)

		check.ifPresent { present ->
			val recipe = present.value
			if (
				recipe.canFitItemsOverflow(this.getItemsInRange(4 .. 7)) &&
				recipe.canFitFluidsOverflow(this.getFluidsInRange(2 .. 3), 10000)
			) {
				this.setRecipe(recipe)
				this.maxProgress = recipe.getTime()
			}
		}
	}

	override fun checkIsEmpty(level: Level): Boolean =
		this.getItemsInRange(0 .. 3).all(ItemStack::isEmpty) &&
				this.getFluidsInRange(0 .. 1).all(FluidStack::isEmpty)
}