package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
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
				this@SingleFluidRecipeBlockEntity.syncToClients()
			}
		}
	}

	override fun tick(level: Level, tPos: BlockPos, tState: BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.inputStillValid(this.tank.getFluidInTank(0))) this.resetRecipe()
			if (activeRecipe.canFitResults(this.tank, 1)) {
				val recipeTime = activeRecipe.rTime ?: 0
				this.progress++
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, level)
					this.resetRecipe()
				}
			}
		}, {
			val check = this.recipeDial.getRecipeFor(
				BMRecipeInputs.SingleFluid(
					this.tank.getFluidInTank(0),
					this.tank.getFluidInTank(0).amount,
					0
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				val recipeTime = recipe.rTime ?: 0
				if (!recipe.canFitResults(this.tank, 1)) return@ifPresent
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipeTime
			}
		})
	}

	override fun finalizeRecipe(recipe: SingleFluidTestRecipe, level: Level) {
		val assemble = recipe.assembleFluid(
			BMRecipeInputs.SingleFluid(
				this.tank.getFluidInTank(0),
				this.tank.getFluidInTank(0).amount,
				0
			)
		)
		if (this.tank.getFluidInTank(1).isEmpty) this.tank.setFluidInTank(
			1,
			assemble.copyWithAmount(recipe.rFluidOutput.amount)
		) else this.tank.getFluidInTank(1).amount += recipe.rFluidOutput.amount
		recipe.consumeInput(this.tank, 0)
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)

		tag.put("fluid", CompoundTag().also { this.tank.writeToNBT(registries, it) })
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)

		this.tank.readFromNBT(registries, tag.getCompound("fluid"))
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		SingleFluidRecipeMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("SingleFluidRecipe")
}