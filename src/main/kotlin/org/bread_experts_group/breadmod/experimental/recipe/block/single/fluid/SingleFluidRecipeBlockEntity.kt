package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import java.util.Optional

class SingleFluidRecipeBlockEntity(
	pos: BlockPos,
	state: BlockState
) : AbstractTestRecipeBlockEntity<
		BMRecipeInputs.SingleFluid,
		SingleFluidTestRecipe,
		SingleFluidRecipeBlockEntity>(
	pos,
	state,
	ModBlockEntityTypes.SINGLE_FLUID_TEST.get(),
	ModRecipeTypes.SINGLE_FLUID.get()
), FluidBearingBlockEntity {
	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
		listOf(
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true),
			Triple(10000, true, true),
		).map { ExpansibleFluidHandler.ExpansibleTank(it.first, it.second, it.third) }.toMutableList()
	)

	override fun tick(level: Level, tPos: BlockPos, tState: BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.inputStillValid(this.fluidHandler.getFluidInTank(0))) this.resetRecipe()
			if (activeRecipe.canFitResults(this.fluidHandler.getUnit(1))) {
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
					this.fluidHandler.getFluidInTank(0),
					this.fluidHandler.getFluidInTank(0).amount,
					0
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				val recipeTime = recipe.rTime ?: 0
				if (!recipe.canFitResults(this.fluidHandler.getUnit(1))) return@ifPresent
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipeTime
			}
		})
	}

	override fun finalizeRecipe(recipe: SingleFluidTestRecipe, level: Level) {
		val assemble = recipe.assembleFluid(
			BMRecipeInputs.SingleFluid(
				this.fluidHandler.getFluidInTank(0),
				this.fluidHandler.getFluidInTank(0).amount,
				0
			)
		)
		if (this.fluidHandler.getFluidInTank(1).isEmpty) {
			this.fluidHandler.getUnit(1).asStack = assemble.copyWithAmount(recipe.rFluidOutput.amount)
		} else this.fluidHandler.getFluidInTank(1).amount += recipe.rFluidOutput.amount
		recipe.consumeInput(this.fluidHandler.getUnit(0))
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		SingleFluidRecipeMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("SingleFluidRecipe")
}