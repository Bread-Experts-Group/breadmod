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
		val inputItems = listOf(this.getItem(0), this.getItem(1), this.getItem(2), this.getItem(3))
		val inputFluids = listOf(this.getFluid(0), this.getFluid(1))
		val outputSlots = listOf(this.getItem(4), this.getItem(5), this.getItem(6), this.getItem(7))
		val (items, fluids) = recipe.assembleOutputs(
			FluidEnergyInput(
				inputItems,
				buildList { inputItems.filter { !it.isEmpty }.forEach { this.add(it.count) } },
				inputFluids,
				buildList { inputFluids.filter { !it.isEmpty }.forEach { this.add(it.amount) } }
			)
		)
		items.forEach { outputItem ->
			for (i in outputSlots.indices) {
				if (
					(outputSlots[i].`is`(outputItem.item) || outputSlots[i].isEmpty) &&
					(outputSlots[i].count + outputItem.count <= outputItem.maxStackSize)
				) {
					this.setOrGrow(i + 4, outputItem, outputItem.count)
					recipe.consumeItemsAndSet(inputItems, this::setItem)
					break
				}
			}
		}
		// todo fluids come later
//		fluids.forEach { outputFluid ->
//			val match =
//		}
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
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			val fluidInputs = listOf(this.getFluid(0), this.getFluid(1))
			val itemInputs =
				listOf(this.getItem(0), this.getItem(1), this.getItem(2), this.getItem(3))
			if (!activeRecipe.inputsStillValid(itemInputs, fluidInputs)) this.resetRecipe(level)
			// todo work on slot emptiness / fullness check
			//  (aka new canFitResults that accounts for overflow)
//			if (activeRecipe.canFitResults(
//					listOf(this.getItem(4), this.getItem(5), this.getItem(6), this.getItem(7)),
//					listOf(this.getFluid(2), this.getFluid(3)),
//					10000
//				)
//			) {
				val recipeTime = activeRecipe.rTime ?: 0
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, level)
					this.resetRecipe(level)
				} else this.progress++
//			} else this.resetRecipe(level)
		}, {
			val fluidInputs = listOf(this.getFluid(0), this.getFluid(1))
			val itemInputs =
				listOf(this.getItem(0), this.getItem(1), this.getItem(2), this.getItem(3))
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