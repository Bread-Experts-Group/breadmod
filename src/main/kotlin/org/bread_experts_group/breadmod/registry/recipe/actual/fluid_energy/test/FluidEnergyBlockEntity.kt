package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.AbstractTickingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemBearingBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler
import java.util.Optional

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
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, false, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, false, true)
		)
	)

	override fun finalizeRecipe(recipe: FluidEnergyRecipeTest, level: Level): Boolean {
		recipe.consumeItems(this.getItemsInRange(0 .. 3)).forEachIndexed(this::setItem)
		recipe.setItemsOverflow(this.getItemsInRange(4 .. 7)) { slot, item, count ->
			this.setOrGrowItem(slot + 4, item, count)
		}
		recipe.consumeFluids(this.getFluidsInRange(0 .. 1)).forEachIndexed(this::setFluid)
		recipe.setFluidsOverflow(this.getFluidsInRange(2 .. 3), 10000) { tank, fluid, amount ->
			this.setOrGrowFluid(tank + 2, fluid, amount)
		}
		return true
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		FluidEnergyMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("Fluid Energy Recipe")
	override fun commonTick(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	) {
		if (this.itemHandler.isEmpty && this.fluidHandler.isEmpty) return
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			val fluidInputs = this.getFluidsInRange(0 .. 1)
			val itemInputs = this.getItemsInRange(0 .. 3)

			if (!activeRecipe.inputsStillValid(itemInputs, fluidInputs)) this.resetRecipe(level)
			if (
				activeRecipe.canFitItemsOverflow(this.getItemsInRange(4 .. 7)) &&
				activeRecipe.canFitFluidsOverflow(this.getFluidsInRange(2 .. 3), 10000)
			) {
				val recipeTime = activeRecipe.rTime ?: 0
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, level)
					this.resetRecipe(level)
				} else this.progress++
			} else this.resetRecipe(level)
		}, {
			val fluidInputs = this.getFluidsInRange(0 .. 1)
			val itemInputs = this.getItemsInRange(0 .. 3)
			val check = this.recipeDial.getRecipeFor(
				FluidEnergyInput(
					itemInputs,
					buildList { itemInputs.forEach { this.add(it.count) } },
					fluidInputs,
					buildList { fluidInputs.forEach { this.add(it.amount) } }
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				this.maxProgress = recipe.rTime ?: 0
				this.currentRecipe = Optional.of(recipe)
			}
		})
	}
}