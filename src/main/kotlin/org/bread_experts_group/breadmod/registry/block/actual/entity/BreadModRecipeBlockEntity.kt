package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.util.getRecipe
import org.bread_experts_group.breadmod.util.putRecipe
import java.util.Optional
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull

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
	var energyDivision: Int = -1
		set(value) {
			if (field == -1) field = value
		}

	fun handleEnergy(handler: IEnergyStorage): Boolean =
		(this.energyDivision < 0) && (handler.energyStored + this.energyDivision > handler.maxEnergyStored)

	override fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {
		tag.putInt("progress", this.progress)
		tag.putInt("maxProgress", this.maxProgress)
		tag.putRecipe("currentRecipe", this.currentRecipe.getOrNull() ?: return)
	}

	override fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {
		this.progress = tag.getInt("progress")
		this.maxProgress = tag.getInt("maxProgress")
		this.currentRecipe = Optional.of(tag.getRecipe<I, R>("currentRecipe") ?: return)
	}

	/**
	 * Returns a list of optional recipes matching the [recipeType].
	 */
	fun getRecipeList(recipeType: RecipeType<R>, level: Level): List<Optional<RecipeHolder<R>>> =
		level.recipeManager.getAllRecipesFor(recipeType).map { Optional.of(it) }

	/**
	 * @see getRecipeList
	 */
	fun getRecipeList(recipeType: Supplier<RecipeType<R>>, level: Level): List<Optional<RecipeHolder<R>>> =
		this.getRecipeList(recipeType.get(), level)

	/**
	 * Holds the current running recipe.
	 */
	protected var currentRecipe: Optional<R> = Optional.empty()
	private val recipeDial: RecipeManager.CachedCheck<I, R> = RecipeManager.createCheck(recipeType)

	override fun commonTick(level: Level) {
		this.runRecipe(level)
	}

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
		this.setChanged()
	}

	fun finalizeAndReset(recipe: R, level: Level) {
		this.finalizeRecipe(recipe, level)
		this.resetRecipe(level)
	}

	open fun getOptionalRecipe(input: I, level: Level): Optional<RecipeHolder<R>> =
		this.recipeDial.getRecipeFor(input, level)

	fun setRecipe(recipe: R) {
		this.currentRecipe = Optional.of(recipe)
	}

	/**
	 * Ticks the [currentRecipe].
	 */
	abstract fun runCurrentRecipe(recipe: R, level: Level)

	/**
	 * Used for recipe checking and setting.
	 */
	abstract fun runMissingRecipe(level: Level)

	/**
	 * Used for checking if specified slots or tanks are empty.
	 */
	abstract fun checkIsEmpty(level: Level): Boolean

	/**
	 * Runs the current recipe for this [net.minecraft.world.level.block.entity.BlockEntity].
	 */
	fun runRecipe(level: Level) {
		if (this.checkIsEmpty(level)) this.resetRecipe(level)
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			this.runCurrentRecipe(activeRecipe, level)
		}, {
			this.runMissingRecipe(level)
		})
	}
}