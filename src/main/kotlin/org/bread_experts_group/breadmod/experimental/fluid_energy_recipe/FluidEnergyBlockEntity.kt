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
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class FluidEnergyBlockEntity(
	pos : BlockPos,
	state : BlockState
) : AbstractTestItemRecipeBlockEntity<FluidEnergyRecipe.FluidEnergyInput, FluidEnergyRecipeTest>(
	pos, state,
	ModBlockEntityTypes.FLUID_ENERGY.get(),
	ModRecipeTypes.FLUID_ENERGY_TEST.get(),
	8
) {
	inner class SyncedHandler(capacity : Int, canFill : Boolean, canDrain : Boolean) :
		SidedFluidTank.CustomHandler(capacity, canFill, canDrain) {
		override fun onContentsChanged() {
            level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
//			this@FluidEnergyBlockEntity.setChanged()
		}
	}
	// todo needs logic to disallow input fluids
	val tank : SidedFluidTank = SidedFluidTank(
		listOf(
			Triple(10000, true, false),
			Triple(10000, true, false),
			Triple(10000, false, true),
			Triple(10000, false, true)
		).map { this.SyncedHandler(it.first, it.second, it.third) }
	)

	private fun getFluid(tank : Int) : FluidStack = this.tank.getFluidInTank(tank)
	override fun tick(level : Level, tPos : BlockPos, tState : BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			val fluidInputs = listOf(this.getFluid(0), this.getFluid(1))
			if (!activeRecipe.inputsStillValid(this.items, fluidInputs)) this.resetRecipe()
			if (activeRecipe.canFitResults(
					listOf(this.items[4], this.items[5], this.items[6], this.items[7]),
					listOf(this.getFluid(2), this.getFluid(3)),
					10000
			)
				) {
				val recipeTime = activeRecipe.rTime ?: 0
				this.progress++
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, level)
					this.resetRecipe()
				}
			} else this.resetRecipe()

		}, {
			val fluidInputs = listOf(this.getFluid(0), this.getFluid(1))
			val itemInputs =
				listOf(this.items[0], this.items[1], this.items[2], this.items[3])
			val check = this.recipeDial.getRecipeFor(
				FluidEnergyRecipe.FluidEnergyInput(
					this.items,
					buildList { itemInputs.forEach { this.add(it.count) } },
					fluidInputs,
					buildList { fluidInputs.forEach { this.add(it.amount) } },
					2
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				this.maxProgress = recipe.rTime ?: 0
				this.currentRecipe = Optional.of(recipe)
			}
		})
	}

	override fun finalizeRecipe(recipe : FluidEnergyRecipeTest, level : Level) {
		val inputItems = listOf(this.items[0], this.items[1], this.items[2], this.items[3])
		val inputFluids = listOf(this.getFluid(0), this.getFluid(1))
		val outputSlots = mutableListOf(this.getItem(4), this.getItem(5), this.getItem(6), this.getItem(7))
		val assemble = recipe.assembleOutputs(
			FluidEnergyRecipe.FluidEnergyInput(
				inputItems,
				buildList { inputItems.filter { !it.isEmpty }.forEach { this.add(it.count) } },
				inputFluids,
				buildList { inputFluids.filter { !it.isEmpty }.forEach { this.add(it.amount) } },
				4
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
			repeat(outputSlots.size) { index -> this.items[index + 4] = outputSlots[index] }
		}
		if (assemble.second.isNotEmpty()) repeat(assemble.second.size) { index ->
			if (this.tank.tanks[index + 2].isEmpty()) this.tank.tanks[index + 2].fluid =
				assemble.second[index].copyWithAmount(recipe.rFluidOutputs[index].amount) else
				this.tank.tanks[index + 2].fluid.grow(recipe.rFluidOutputs[index].amount)
		}
		recipe.consumeInputs(inputItems, inputFluids)
	}

	override fun createMenu(containerId : Int, playerInventory : Inventory, player : Player) : AbstractContainerMenu =
		FluidEnergyMenu(containerId, playerInventory, this)

	override fun saveAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.saveAdditional(tag, registries)
		this.tank.writeToNBT(registries, tag)
	}

	override fun loadAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.loadAdditional(tag, registries)
		this.tank.readFromNBT(registries, tag)
	}

	override fun getDisplayName() : Component = Component.literal("FluidEnergyRecipe")
	override fun getWidth() : Int = 2
	override fun getHeight() : Int = 2
}