package org.bread_experts_group.breadmod.compat.jei.vanilla_extension

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeHolder
import org.bread_experts_group.breadmod.compat.jei.itemTagToList
import org.bread_experts_group.breadmod.compat.jei.recipeList
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.AbstractCuttingRecipe

class JEISliceCraftingExtension(
	private val input : Item,
	private val inputMulti : Int,
	private val inputRepeatCount : Int,
	private val output : Item,
	private val outputMulti : Int,
	private val outputRepeatCount : Int
) : ICraftingCategoryExtension<AbstractCuttingRecipe> {
	override fun getWidth(recipeHolder : RecipeHolder<AbstractCuttingRecipe>) : Int = 2
	override fun getHeight(recipeHolder : RecipeHolder<AbstractCuttingRecipe>) : Int = 1
	override fun setRecipe(
		recipeHolder : RecipeHolder<AbstractCuttingRecipe>,
		builder : IRecipeLayoutBuilder,
		craftingGridHelper : ICraftingGridHelper,
		focuses : IFocusGroup
	) {
		craftingGridHelper.createAndSetInputs(
			builder,
			listOf(
				itemTagToList(ItemTags.SWORDS),
				recipeList(this.input, this.inputMulti, this.inputRepeatCount)
			), this.getWidth(recipeHolder), this.getHeight(recipeHolder)
		)
		craftingGridHelper.createAndSetOutputs(
			builder,
			recipeList(this.output, this.outputMulti, this.outputRepeatCount)
		)
	}
}