package org.bread_experts_group.breadmod.experimental.recipe.block.single.item

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class SingleItemRecipeBlockEntity(
	pos : BlockPos,
	state : BlockState
) : AbstractTestItemRecipeBlockEntity<BMRecipeInputs.SingleItem, SingleItemTestRecipe>(
	pos,
	state,
	ModBlockEntityTypes.SINGLE_ITEM_TEST.get(),
	ModRecipeTypes.SINGLE_ITEM.get(),
	2
) {
	override fun tick(level : Level, tPos : BlockPos, tState : BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.inputStillValid(this.items)) this.resetRecipe()
			val recipeTime = activeRecipe.rTime ?: 0
			this.progress++
			if (this.progress >= recipeTime) {
				this.finalizeRecipe(activeRecipe, level)
				this.resetRecipe()
			}
		}, {
			val check = this.recipeDial.getRecipeFor(
				BMRecipeInputs.SingleItem(
					this.items[0],
					this.items[0].count,
					1
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				val recipeTime = recipe.rTime ?: 0
				if (!recipe.canFitResults(this.items, 1)) return@ifPresent
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipeTime
			}
		})
	}

	override fun getWidth() : Int = 1
	override fun getHeight() : Int = 1
	override fun createMenu(containerId : Int, playerInventory : Inventory, player : Player) : AbstractContainerMenu =
		SingleItemRecipeMenu(containerId, playerInventory, this)

	override fun getDisplayName() : Component = Component.literal("SingleItemRecipe")
	override fun finalizeRecipe(recipe : SingleItemTestRecipe, level : Level) {
		val assemble =
			recipe.assemble(BMRecipeInputs.SingleItem(this.items[0], this.items[0].count, 1), level.registryAccess())
		if (this.itemSlots[1].isEmpty) this.itemSlots[1] =
			assemble.copyWithCount(recipe.rItemOutput.count) else this.itemSlots[1].grow(recipe.rItemOutput.count)
		recipe.consumeInput(this.items)
	}
}