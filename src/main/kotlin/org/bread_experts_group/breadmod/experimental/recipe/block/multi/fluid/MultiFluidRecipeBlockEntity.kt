package org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid

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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiFluidTestRecipe
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
	val logger: Logger = LogManager.getLogger()
	val tank: CustomFluidTank by lazy {
		object : CustomFluidTank(10000, 4) {
			override fun onContentsChanged() {
				this@MultiFluidRecipeBlockEntity.syncToClients()
			}
		}
	}

	private fun getFluid(tank: Int): FluidStack = this.tank.getFluidInTank(tank)
	override fun tick(level: Level, tPos: BlockPos, tState: BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			val inputList = listOf(this.getFluid(0), this.getFluid(1))
			if (!activeRecipe.inputsStillValid(inputList)) this.resetRecipe()
			if (activeRecipe.canFitResults(
					listOf(
						this.getFluid(2),
						this.getFluid(3)
					), this.tank.capacity
				)
			) {
				val recipeTime = activeRecipe.rTime ?: 0
				this.progress++
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, level)
					this.resetRecipe()
				}
			}
		}, {
			val inputList = listOf(this.getFluid(0), this.getFluid(1))
			val check = this.recipeDial.getRecipeFor(
				BMRecipeInputs.MultiFluid(
					inputList,
					buildList { inputList.forEach { this.add(it.amount) } },
					0
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				this.maxProgress = recipe.rTime ?: 0
				this.currentRecipe = Optional.of(recipe)
			}
		})
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		MultiFluidRecipeMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("MultiFluidTestRecipe")
	override fun finalizeRecipe(recipe: MultiFluidTestRecipe, level: Level) {
		val inputList = listOf(this.getFluid(0), this.getFluid(1))
		val assemble = recipe.assembleFluids(
			BMRecipeInputs.MultiFluid(
				inputList.filter { !it.isEmpty },
				buildList { inputList.filter { it.amount != 0 }.forEach { this.add(it.amount) } },
				0
			)
		)
		// Index 0 for multi-item/fluid recipes should always exist.
		// If it doesn't, then something seriously went wrong...
		if (this.getFluid(2).isEmpty) {
			this.tank.setFluidInTank(2, assemble[0].copyWithAmount(recipe.rFluidOutputs[0].amount))
		} else this.getFluid(2).amount += recipe.rFluidOutputs[0].amount
		try {
			if (this.getFluid(3).isEmpty) {
				this.tank.setFluidInTank(3, assemble[1].copyWithAmount(recipe.rFluidOutputs[1].amount))
			} else this.getFluid(3).amount += recipe.rFluidOutputs[1].amount
		} catch (e: Exception) {
			this.logger.error(e)
		}
		recipe.consumeInputs(inputList)
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)

		tag.put("fluid", CompoundTag().also { this.tank.writeToNBT(registries, it) })
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)

		this.tank.readFromNBT(registries, tag.getCompound("fluid"))
	}
}