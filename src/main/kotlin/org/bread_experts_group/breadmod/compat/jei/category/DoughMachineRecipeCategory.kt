package org.bread_experts_group.breadmod.compat.jei.category

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe

// todo finish this
class DoughMachineRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<DoughMachineRecipe> {
	override fun getRecipeType(): RecipeType<DoughMachineRecipe> {
		TODO("Not yet implemented")
	}

	override fun getTitle(): Component {
		TODO("Not yet implemented")
	}

	override fun getBackground(): IDrawable {
		TODO("Not yet implemented")
	}

	override fun getIcon(): IDrawable? {
		TODO("Not yet implemented")
	}

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DoughMachineRecipe, focuses: IFocusGroup) {
		TODO("Not yet implemented")
	}
}