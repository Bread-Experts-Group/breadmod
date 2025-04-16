package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional

abstract class BreadModRecipeBlockEntity<I : RecipeInput, R : Recipe<I>, T : BreadModRecipeBlockEntity<I, R, T>>(
	type: BlockEntityType<T>,
	pos: BlockPos,
	state: BlockState,
	recipeType: RecipeType<R>
) : BreadModBlockEntity<T>(type, pos, state) {
	/**
	 * Counts up by 1 every tick when the recipe is valid.
	 * The recipe is completed when this is above or equal to the max recipe time.
	 */
	var progress: Int = 0

	/**
	 * Not used in recipe logic, only as a visual indicator in guis for the player
	 */
	var maxProgress: Int = 0

	/**
	 * Holds the current running recipe.
	 */
	var currentRecipe: Optional<R> = Optional.empty()
	val recipeDial: RecipeManager.CachedCheck<I, R> = RecipeManager.createCheck(recipeType)

	/**
	 * Finalizes this recipe.
	 */
	abstract fun finalizeRecipe(recipe: R, level: Level): Boolean

	/**
	 * Resets the current recipe.
	 */
	open fun resetRecipe(level: Level) {
		this.currentRecipe = Optional.empty()
		this.maxProgress = 0; this.progress = 0
	}

	fun finalizeAndReset(recipe: R, level: Level) {
		this.finalizeRecipe(recipe, level)
		this.resetRecipe(level)
	}
}