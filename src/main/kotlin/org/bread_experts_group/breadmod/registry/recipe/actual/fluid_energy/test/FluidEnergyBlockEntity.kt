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
import org.bread_experts_group.breadmod.util.handlers.ExtendedItemStackHandler
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
	override val itemHandler: ExtendedItemStackHandler = ExtendedItemStackHandler(8)
	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
		mutableListOf(
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, true, false),
			ExpansibleFluidHandler.ExpansibleTank(10_000, false, true),
			ExpansibleFluidHandler.ExpansibleTank(10_000, false, true)
		)
	)

	override fun finalizeRecipe(recipe: FluidEnergyRecipeTest, level: Level) {
		val inputItems = listOf(this.getItem(0), this.getItem(1), this.getItem(2), this.getItem(3))
		val inputFluids = listOf(this.getFluid(0), this.getFluid(1))
		val outputSlots = mutableListOf(this.getItem(4), this.getItem(5), this.getItem(6), this.getItem(7))
		val assemble = recipe.assembleOutputs(
			FluidEnergyInput(
				inputItems,
				buildList { inputItems.filter { !it.isEmpty }.forEach { this.add(it.count) } },
				inputFluids,
				buildList { inputFluids.filter { !it.isEmpty }.forEach { this.add(it.amount) } }
			)
		)
		// todo rework to account for multiple output slots, fail over into other slots when the intended slot is full
		//  and/or has the wrong item type
		if (assemble.first.isNotEmpty()) {
			repeat(assemble.first.size) { index ->
				if (outputSlots[index].isEmpty) outputSlots[index] =
					assemble.first[index].copyWithCount(recipe.rItemOutputs[index].count) else
					outputSlots[index].grow(recipe.rItemOutputs[index].count)
			}
//			for (index in 0 .. outputSlots.size) {
//				val actualIndex = index + 4
//				val item =  this.getItem(actualIndex)
//				if (item.count < outputSlots[index].maxStackSize) {
//					this.setItem(actualIndex, outputSlots[index])
//					break
//				}
//			}
			repeat(outputSlots.size) { index -> this.setItem(index + 4, outputSlots[index]) }
		}
		if (assemble.second.isNotEmpty()) repeat(assemble.second.size) { index ->
			if (this.getFluid(index + 2).isEmpty) this.setFluid(
				index + 2, assemble.second[index].copyWithAmount(recipe.rFluidOutputs[index].amount)
			) else this.growFluid(index + 2, recipe.rFluidOutputs[index].amount)
		}
		recipe.consumeInputs(inputItems, inputFluids)
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		FluidEnergyMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("Fluid Energy Recipe")
	override fun commonTick(
		clientLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			val fluidInputs = listOf(this.getFluid(0), this.getFluid(1))
			val itemInputs =
				listOf(this.getItem(0), this.getItem(1), this.getItem(2), this.getItem(3))
			if (!activeRecipe.inputsStillValid(itemInputs, fluidInputs)) this.resetRecipe()
			if (activeRecipe.canFitResults(
					listOf(this.getItem(4), this.getItem(5), this.getItem(6), this.getItem(7)),
					listOf(this.getFluid(2), this.getFluid(3)),
					10000
				)
			) {
				val recipeTime = activeRecipe.rTime ?: 0
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, clientLevel)
					this.resetRecipe()
				} else this.progress++
			} else this.resetRecipe()
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
				), clientLevel
			)

			check.ifPresent { present ->
				val recipe = present.value
				this.maxProgress = recipe.rTime ?: 0
				this.currentRecipe = Optional.of(recipe)
			}
		})
	}
}